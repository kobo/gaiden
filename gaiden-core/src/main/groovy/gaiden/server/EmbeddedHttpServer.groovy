package gaiden.server

import groovy.transform.CompileStatic
import org.vertx.groovy.core.Vertx
import org.vertx.groovy.core.http.HttpServer
import org.vertx.groovy.core.http.HttpServerRequest
import org.vertx.groovy.core.http.ServerWebSocket

import java.nio.file.Path

@CompileStatic
class EmbeddedHttpServer {

    private List<ServerWebSocket> webSockets = [].asSynchronized()
    private HttpServer httpServer

    int port

    EmbeddedHttpServer(Path outputDirectory) {
        this.port = localPort
        this.httpServer = Vertx.newVertx().createHttpServer().websocketHandler { ServerWebSocket webSocket ->
            webSocket.closeHandler {
                webSockets.remove(webSocket)
            }
            webSockets << webSocket
        }.requestHandler { HttpServerRequest request ->
            request.response.sendFile("${outputDirectory}/${request.path == '/' ? 'index.html' : request.path}")
        }
    }

    void start() {
        httpServer.listen(port)
    }

    void reload() {
        webSockets.each { ServerWebSocket webSocket ->
            webSocket.writeTextFrame("reload")
        }
    }

    private static int getLocalPort() {
        new ServerSocket(0).withCloseable { socket ->
            (socket as ServerSocket).localPort
        } as int
    }
}
