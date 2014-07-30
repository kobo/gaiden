package gaiden.command

import gaiden.GaidenConfig
import gaiden.exception.GaidenException
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired

import java.nio.file.Files

@CompileStatic
abstract class AbstractCommand implements GaidenCommand {

    @Autowired
    GaidenConfig gaidenConfig

    void execute(List<String> arguments = []) {
        if (onlyGaidenProject && !Files.exists(gaidenConfig.gaidenConfigFile)) {
            throw new GaidenException("not.gaiden.project.error", [gaidenConfig.gaidenConfigFile])
        }

        def cliBuilder = new CliBuilder()
        def optionAccessor = cliBuilder.parse(arguments)
        execute(optionAccessor.arguments(), optionAccessor)
    }

    abstract void execute(List<String> arguments, OptionAccessor optionAccessor)
}
