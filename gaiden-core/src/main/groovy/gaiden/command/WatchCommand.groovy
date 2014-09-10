package gaiden.command

import gaiden.GaidenApplication
import gaiden.GaidenConfig
import gaiden.message.MessageSource
import gaiden.server.EmbeddedHttpServer
import gaiden.server.FileWatcher
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import java.nio.file.Paths

@Component
@CompileStatic
class WatchCommand extends AbstractCommand {

    @Autowired
    GaidenConfig gaidenConfig

    @Autowired
    BuildCommand buildCommand

    @Autowired
    MessageSource messageSource

    final String name = "watch"

    final boolean onlyGaidenProject = true

    EmbeddedHttpServer server

    @Override
    void execute(List<String> arguments, OptionAccessor optionAccessor) {
        server = new EmbeddedHttpServer(gaidenConfig.outputDirectory)
        server.start()

        // At first, normally build a documentation.
        build()

        def doBuild = { List<File> changedFiles, List<File> createdFiles, List<File> deletedFiles ->
            changedFiles.each { File file ->
                println messageSource.getMessage("command.watch.found.message", [file, "changed"])
            }
            createdFiles.each { File file ->
                println messageSource.getMessage("command.watch.found.message", [file, "created"])
            }
            deletedFiles.each { File file ->
                println messageSource.getMessage("command.watch.found.message", [file, "deleted"])
            }
            println messageSource.getMessage("command.watch.rebuild.message")
            build()
            server.reload()
        }

        new FileWatcher(gaidenConfig.projectDirectory, [gaidenConfig.outputDirectory]).watch(doBuild)
        println messageSource.getMessage("command.watch.start.message", [gaidenConfig.projectDirectory])

        // For specified extra watch targets
        for (String targetFilePath : arguments) {
            new FileWatcher(Paths.get(targetFilePath)).watch(doBuild)
            println messageSource.getMessage("command.watch.start.message", [targetFilePath])
        }
    }

    private void build() {
        def command = new GaidenApplication().applicationContext.getBean(BuildCommand)
        command.gaidenConfig.add("watch", true)
        command.gaidenConfig.add("serverPort", server.port)
        command.execute()
    }
}
