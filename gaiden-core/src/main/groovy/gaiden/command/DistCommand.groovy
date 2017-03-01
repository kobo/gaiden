/*
 * Copyright 2017 the original author or authors
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

import gaiden.Document
import gaiden.DocumentBuilder
import gaiden.DocumentWriter
import gaiden.GaidenApplication
import gaiden.GaidenConfig
import gaiden.SourceCollector
import java.nio.file.Path
import java.nio.file.Files
import org.apache.commons.cli.CommandLine
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

/**
 * The 'dist' command.
 *
 * @author Yasuharu NAKANO
 */
@Component
class DistCommand extends AbstractCommand {

    final String name = "dist"

    final boolean onlyGaidenProject = true

    @Autowired
    GaidenConfig gaidenConfig

    @Override
    void execute(CommandLine commandLine) {
        // Use temp directory for "outputDirectory".
        // It's because the sub directory (i.e. "doc") is required
        // and general "outputDirectory" specifed by "outputDirectoryPath" is only for HTML files which are accessed directly.
        Path tempBaseDir = Files.createTempDirectory("gaiden-dist-")
        Path tempDocDir = tempBaseDir.resolve(gaidenConfig.distFileName)
        gaidenConfig.outputDirectory = tempDocDir

        // At first, normally build a documentation.
        build()

        // Make a zip file.
        def destFilePath = gaidenConfig.projectDirectory.resolve(gaidenConfig.distFileName + ".zip")
        new AntBuilder().zip(destfile: destFilePath.toString(), basedir: tempBaseDir.toString())
        println "Created dist file: " +  destFilePath

        // Clean up.
        tempBaseDir.toFile().deleteDir()
    }

    private void build() {
        try {
            def command = GaidenApplication.getBean(BuildCommand)
            command.gaidenConfig = gaidenConfig
            command.execute()
        } catch (IOException e) {
            println getMessage("command.dist.io.error", [e.class.name, e.message])
        }
    }
}
