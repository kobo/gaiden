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

package gaiden.command

import gaiden.GaidenApplication
import gaiden.GaidenConfig
import gaiden.server.EmbeddedHttpServer
import gaiden.server.FileWatcher
import groovy.transform.CompileStatic
import org.apache.commons.cli.CommandLine
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

    final String name = "watch"

    final boolean onlyGaidenProject = true

    EmbeddedHttpServer server

    @Override
    void execute(CommandLine commandLine) {
        server = new EmbeddedHttpServer(gaidenConfig.outputDirectory)
        server.start()

        // At first, normally build a documentation.
        build()

        def doBuild = { List<File> changedFiles, List<File> createdFiles, List<File> deletedFiles ->
            changedFiles.each { File file ->
                println getMessage("command.watch.found.message", [file, "changed"])
            }
            createdFiles.each { File file ->
                println getMessage("command.watch.found.message", [file, "created"])
            }
            deletedFiles.each { File file ->
                println getMessage("command.watch.found.message", [file, "deleted"])
            }
            println getMessage("command.watch.rebuild.message")
            build()
            server.reload()
        }

        new FileWatcher(gaidenConfig.projectDirectory, [gaidenConfig.outputDirectory]).watch(doBuild)
        println getMessage("command.watch.start.message", [gaidenConfig.projectDirectory])

        // For specified extra watch targets
        commandLine.args.each { String targetFilePath ->
            new FileWatcher(Paths.get(targetFilePath)).watch(doBuild)
            println getMessage("command.watch.start.message", [targetFilePath])
        }
    }

    private void build() {
        try {
            GaidenApplication.initialize()
            def command = GaidenApplication.getBean(BuildCommand)
            GaidenApplication.config.add("watch", true)
            GaidenApplication.config.add("serverPort", server.port)
            command.execute()
        } catch (IOException e) {
            println getMessage("command.watch.io.error", [e.class.name, e.message])
        }
    }
}
