/*
 * Copyright 2013 the original author or authors
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
import spock.lang.Specification

import java.nio.file.Files
import java.nio.file.Path

class CleanCommandSpec extends Specification {

    GaidenConfig gaidenConfig
    Path gaidenProjectDirectory

    CleanCommand command

    def setup() {
        GaidenApplication gaidenApplication = new GaidenApplication()
        gaidenConfig = gaidenApplication.applicationContext.getBean(GaidenConfig)

        gaidenProjectDirectory = Files.createTempDirectory("project-dir")
        gaidenConfig.userDirectoryPath = gaidenProjectDirectory.toString()
        Files.createFile(gaidenConfig.gaidenConfig)

        assert Files.exists(gaidenConfig.gaidenConfig)

        command = gaidenApplication.applicationContext.getBean(CleanCommand)
    }

    def cleanup() {
        assert gaidenProjectDirectory.deleteDir()
    }

    def "'execute' should clean the build directory"() {
        given:
            def buildDirectory = gaidenConfig.userDirectory.resolve(gaidenConfig.outputDirectoryPath)
            Files.createDirectory(buildDirectory)
            assert Files.exists(buildDirectory)

        when:
            command.execute()
            true

        then:
            Files.notExists(buildDirectory)
    }
}
