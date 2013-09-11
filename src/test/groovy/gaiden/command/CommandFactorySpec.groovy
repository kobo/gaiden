package gaiden.command

import gaiden.GaidenConfig
import gaiden.Holders
import spock.lang.Specification
import spock.lang.Unroll

class CommandFactorySpec extends Specification {

    @Unroll
    def "'createCommand' should create a #commandName command"() {
        setup:
        Holders.config = Mock(GaidenConfig)
        CommandFactory factory = new CommandFactory()

        when:
        def command = factory.createCommand(commandName)

        then:
        command.class == commandClass

        where:
        commandName      | commandClass
        "build"          | Build
        "clean"          | Clean
        "create-project" | CreateProject
    }

    def "'createCommand' should return null when a command is invalid"() {
        setup:
        Holders.config = Mock(GaidenConfig)
        CommandFactory factory = new CommandFactory()

        expect:
        factory.createCommand(commandName) == null

        where:
        commandName << ["", "invalid"]
    }

}
