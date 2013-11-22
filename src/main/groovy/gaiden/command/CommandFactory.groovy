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

package gaiden.command

import gaiden.exception.IllegalOperationException
import org.codehaus.groovy.runtime.typehandling.GroovyCastException

/**
 * A factory of command.
 */
class CommandFactory {

    /**
     * Creates instance of command.
     *
     * @param commandName a command name
     * @return A instance of command. When a command is invalid, returns {@code null}.
     */
    GaidenCommand createCommand(String commandName) {
        def commandClassName = getCommandClassName(commandName)
        if (!commandClassName) {
            throw new IllegalOperationException()
        }

        def commandClass = resolveCommandClass(commandClassName)
        if (!commandClass) {
            throw new IllegalOperationException()
        }

        return commandClass.newInstance() as GaidenCommand
    }

    private Class resolveCommandClass(String commandClass) {
        try {
            "gaiden.command.${commandClass}" as Class
        } catch (GroovyCastException e) {
            null
        }
    }

    private String getCommandClassName(String command) {
        if (!command || command.startsWith("-")) {
            return command
        }

        command.split("-").collect { word ->
            if (word.size() == 1) {
                return word.toUpperCase()
            }
            word[0].toUpperCase() + word[1..-1].toLowerCase()
        }.join("")
    }

}
