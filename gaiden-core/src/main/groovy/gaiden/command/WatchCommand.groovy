package gaiden.command

import gaiden.GaidenConfig
import gaiden.message.MessageSource
import gaiden.server.EmbeddedHttpServer
import gaiden.server.FileWatcher
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

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

        buildCommand.execute()
        new FileWatcher().watch(gaidenConfig.projectDirectory, [gaidenConfig.outputDirectory]) {
            println messageSource.getMessage("command.watch.rebuild.message")
            buildCommand.execute()
            server.reload()
        }
    }

    private static int getLocalPort() {
        ServerSocket socket = new ServerSocket(0)
        socket.getLocalPort()
    }
}
