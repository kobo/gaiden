/*
 * Copyright 2014 the original author or authors
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
