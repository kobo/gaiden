/*
 * Copyright 2013 the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package gaiden

import gaiden.exception.GaidenException
import gaiden.message.MessageSource
import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode

/**
 * A command line to execute Gaiden.
 *
 * @author Hideki IGARASHI
 * @author Kazuki YAMAMOTO
 */
class GaidenMain {

    private GaidenApplication gaidenApplication = new GaidenApplication()

    /**
     * A main command line interface.
     *
     * @param args all command line args
     */
    static void main(String... args) {
        new GaidenMain().executeCommand(args)
    }

    protected void run(String... args) {
        try {
            executeCommand(args)
        } catch (GaidenException e) {
            def messageSource = gaidenApplication.applicationContext.getBean(MessageSource)
            System.err.println(messageSource.getMessage(e.code, e.arguments as Object[], Locale.default))
            System.exit(1)
        }
    }

    protected void executeCommand(String... args) {
        def options = getOptions(args)
        def commandName = getCommandName(args, options)
        def commandResolver = gaidenApplication.applicationContext.getBean(CommandResolver)
        def command = commandResolver.resolve(commandName)

        command.execute(options.arguments() ? options.arguments().tail() : options.arguments())
    }

    @CompileStatic(TypeCheckingMode.SKIP)
    private String getCommandName(String[] args, OptionAccessor options) {
        if (options.v) {
            return "version"
        } else if (args) {
            return args.first()
        }
        null
    }

    @CompileStatic(TypeCheckingMode.SKIP)
    private OptionAccessor getOptions(String[] args) {
        def cliBuilder = new CliBuilder()
        cliBuilder.with {
            v longOpt: 'version', 'show version'
        }
        cliBuilder.parse(args)
    }
}
