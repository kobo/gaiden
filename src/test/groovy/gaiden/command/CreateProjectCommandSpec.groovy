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
import gaiden.exception.GaidenException
import spock.lang.Specification

class CreateProjectCommandSpec extends Specification {

    GaidenConfig gaidenConfig
    String newProjectName
    File newProjectDirectory
    File outputDirectory

    CreateProjectCommand command

    def setup() {
        GaidenApplication gaidenApplication = new GaidenApplication()
        command = gaidenApplication.applicationContext.getBean(CreateProjectCommand)

        outputDirectory = File.createTempDir()

        gaidenConfig = gaidenApplication.applicationContext.getBean(GaidenConfig)
        gaidenConfig.appHomePath = "src/dist"
        gaidenConfig.projectDirectoryPath = outputDirectory.canonicalPath

        newProjectName = "newProject"
        newProjectDirectory = new File(outputDirectory, newProjectName)
    }

    def cleanup() {
        assert outputDirectory.deleteDir()
    }

    def "'execute' should create the Gaiden project directory"() {
        when:
            command.execute([newProjectName])

        then:
            newProjectDirectory.exists()
            collectFilePathRecurse(newProjectDirectory) == collectFilePathRecurse(gaidenConfig.defaultTemplateDirectory.toFile())
    }

    def "'execute' should not create the project directory when the directory already exists"() {
        setup:
            newProjectDirectory.mkdir()

        when:
            command.execute([newProjectName])

        then:
            def e = thrown(GaidenException)
            e.code == "command.create.project.already.exists.error"

        and:
            collectFilePathRecurse(newProjectDirectory).size() == 0
    }

    def "'execute' should output error message when project name is not given"() {
        when:
            command.execute([])

        then:
            def e = thrown(GaidenException)
            e.code == "command.create.project.name.required.error"
    }

    private static Set collectFilePathRecurse(File directory) {
        def fileset = []
        directory.eachFileRecurse {
            fileset << it.path.replaceFirst(directory.path, "")
        }
        fileset
    }
}
