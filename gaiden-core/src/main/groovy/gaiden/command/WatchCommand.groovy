package gaiden.command

import gaiden.GaidenConfig
import gaiden.message.MessageSource
import gaiden.server.EmbeddedHttpServer
import gaiden.server.FileWatcher
import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode
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

    @Override
    void execute(List<String> arguments, OptionAccessor optionAccessor) {
        def port = localPort

        gaidenConfig.add("watch", true)
        gaidenConfig.add("serverPort", port)

        def server = new EmbeddedHttpServer(gaidenConfig.outputDirectory, port)
        server.start()

        // At first, normally build a documentation.
        buildCommand.execute()

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
            buildCommand.execute()
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

    @CompileStatic(TypeCheckingMode.SKIP)
    private static int getLocalPort() {
        new ServerSocket(0).withCloseable { ServerSocket socket ->
            socket.localPort
        } as int
    }
}
