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
import groovy.transform.CompileStatic

/**
 * A command line to execute Gaiden.
 *
 * @author Hideki IGARASHI
 * @author Kazuki YAMAMOTO
 */
@CompileStatic
class GaidenMain {

    private boolean stacktrace = false
    private static final String STACKTRACE_OPTION = '--stacktrace'

    /**
     * A main command line interface.
     *
     * @param args all command line args
     */
    static void main(String... args) {
        new GaidenMain().run(args)
    }

    private void run(String... args) {
        try {
            executeCommand(args.toList())
        } catch (GaidenException e) {
            System.err.println e.message
            if (stacktrace) {
                e.printStackTrace()
            }
            System.exit(1)
        }
    }

    private void executeCommand(List<String> args) {
        stacktrace = args.remove(STACKTRACE_OPTION)

        def commandName = getCommandName(args)
        args.remove(commandName)

        GaidenApplication.initialize()
        def commandResolver = GaidenApplication.getBean(CommandResolver)
        def command = commandResolver.resolve(commandName)

        command.execute(args)
    }

    private String getCommandName(List<String> args) {
        if (args.any { it in ['-v', '--version'] }) {
            return 'version'
        }
        args.find { !it.startsWith('-') }
    }
}
