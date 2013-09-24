package gaiden.command

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
            // TODO Throw exception
            return null
        }

        def commandClass = resolveCommandClass(commandClassName)
        if (!commandClass) {
            // TODO Throw exception
            return null
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
        if (!command) {
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
