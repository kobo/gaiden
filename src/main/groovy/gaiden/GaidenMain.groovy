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

import gaiden.command.CommandFactory
import gaiden.command.GaidenCommand
import gaiden.exception.GaidenException
import gaiden.exception.IllegalOperationException
import gaiden.message.MessageSource

/**
 * A command line to execute Gaiden.
 *
 * @author Hideki IGARASHI
 * @author Kazuki YAMAMOTO
 */
class GaidenMain {

    private static final String CONFIG_FILE_NAME = "GaidenConfig.groovy"

    private CommandFactory commandFactory = new CommandFactory()

    /**
     * A main command line interface.
     *
     * @param args all command line args
     */
    static void main(String... args) {
        try {
            new GaidenMain().run(args)
        } catch (GaidenException e) {
            System.err.println(e.message)
            System.exit(1)
        }
    }

    void run(String... args) {
        Holders.messageSource = new MessageSource()

        if (!args) {
            throw new IllegalOperationException()
        }

        def configFile = new File(CONFIG_FILE_NAME)
        Holders.config = new GaidenConfigLoader().load(configFile)

        executeCommand(args.first(), configFile, args.tail() as List)
    }

    void executeCommand(String commandName, File configFile, List args) {
        GaidenCommand command = createCommand(commandName)

        if (command.onlyGaidenProject && !configFile.exists()) {
            throw new GaidenException("not.gaiden.project.error", [configFile.name])
        }

        command.execute(args)
    }

    protected void setCommandFactory(CommandFactory commandFactory) {
        this.commandFactory = commandFactory
    }

    private GaidenCommand createCommand(String commandName) {
        commandFactory.createCommand(commandName)
    }

}
