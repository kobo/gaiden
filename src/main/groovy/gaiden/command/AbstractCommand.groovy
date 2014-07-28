package gaiden.command

import gaiden.GaidenConfig
import gaiden.exception.GaidenException
import groovy.transform.CompileStatic

import java.nio.file.Files

@CompileStatic
abstract class AbstractCommand implements GaidenCommand {

    void execute(List<String> arguments = []) {
        if (onlyGaidenProject && !Files.exists(GaidenConfig.CONFIG_PATH)) {
            throw new GaidenException("not.gaiden.project.error", [GaidenConfig.CONFIG_PATH])
        }

        def cliBuilder = new CliBuilder()
        def optionAccessor = cliBuilder.parse(arguments)
        execute(optionAccessor.arguments(), optionAccessor)
    }

    abstract void execute(List<String> arguments, OptionAccessor optionAccessor)

    void parseOptions(CliBuilder cliBuilder) {
    }
}
