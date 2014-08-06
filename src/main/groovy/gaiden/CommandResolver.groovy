package gaiden

import gaiden.command.GaidenCommand
import gaiden.command.UnknownCommand
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
@CompileStatic
class CommandResolver {

    @Autowired
    List<GaidenCommand> commnads

    GaidenCommand resolve(String commandName) {
        def command = commnads.find { it.name == commandName }
        command ?: new UnknownCommand()
    }
}
