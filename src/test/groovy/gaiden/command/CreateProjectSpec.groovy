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

import spock.lang.Specification

class CreateProjectSpec extends Specification {

    def savedSystemOut
    def savedSystemErr
    def savedSystemSecurityManager

    String appHome
    String newProjectName
    File outputDirectory
    File newProjectDirectory
    GaidenCommand command

    def setup() {
        savedSystemOut = System.out
        savedSystemErr = System.err
        savedSystemSecurityManager = System.securityManager

        appHome = "src/dist"
        newProjectName = "newProject"
        outputDirectory = File.createTempDir()
        newProjectDirectory = new File(outputDirectory, newProjectName)
        command = new CreateProject(appHome, outputDirectory.path)
    }

    def cleanup() {
        System.out = savedSystemOut
        System.err = savedSystemErr
        System.securityManager = savedSystemSecurityManager

        outputDirectory.deleteDir()
    }

    def "'execute' should create the Gaiden project directory"() {
        when:
        command.execute([newProjectName])

        then:
        newProjectDirectory.exists()
        collectFilePathRecurse(newProjectDirectory) == collectFilePathRecurse(new File("$appHome/template"))
    }

    def "'execute' should not create the project directory when the directory already exists"() {
        setup:
        newProjectDirectory.mkdir()

        and:
        def securityManager = Mock(SecurityManager)
        System.securityManager = securityManager

        and:
        def printStream = Mock(PrintStream)
        System.err = printStream

        when:
        command.execute([newProjectName])

        then:
        1 * securityManager.checkExit(1) >> { throw new SecurityException() }

        and:
        thrown(SecurityException)

        and:
        1 * printStream.println("ERROR: The Gaiden project already exists: ${newProjectName}")

        and:
        collectFilePathRecurse(newProjectDirectory).size() == 0

        cleanup:
        outputDirectory.deleteDir()
    }

    def "'execute' should output error message when project name is not given"() {
        setup:
        def command = new CreateProject(null, null)

        and:
        def securityManager = Mock(SecurityManager)
        System.securityManager = securityManager

        and:
        def printStream = Mock(PrintStream)
        System.err = printStream

        when:
        command.execute([])

        then:
        1 * securityManager.checkExit(1) >> { throw new SecurityException() }

        and:
        thrown(SecurityException)

        and:
        1 * printStream.println("ERROR: Project name is required")
        1 * printStream.println("Usage: gaiden create-project <project name>")
    }

    private Set collectFilePathRecurse(File directory) {
        def fileset = []
        directory.eachFileRecurse {
            fileset << it.path.replaceFirst(directory.path, "")
        }
        fileset
    }

}
