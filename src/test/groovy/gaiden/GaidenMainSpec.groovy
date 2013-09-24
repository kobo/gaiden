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

package gaiden

import gaiden.command.CommandFactory
import gaiden.command.GaidenCommand
import spock.lang.Specification

class GaidenMainSpec extends Specification {

    def savedSystemOut
    def savedSystemErr
    def savedSystemSecurityManager

    def setup() {
        savedSystemOut = System.out
        savedSystemErr = System.err
        savedSystemSecurityManager = System.securityManager
    }

    def cleanup() {
        System.out = savedSystemOut
        System.err = savedSystemErr
        System.securityManager = savedSystemSecurityManager
    }

    def "'run' should run a command"() {
        setup:
        def configFile = Stub(File)
        configFile.exists() >> true

        GroovyMock(File, global: true)
        new File("GaidenConfig.groovy") >> configFile

        and:
        GroovyMock(GaidenConfigLoader, global: true)
        def gaidenConfigLoader = Mock(GaidenConfigLoader)
        def gaidenConfig = new GaidenConfig()

        and:
        def command = Mock(GaidenCommand)
        1 * command.onlyGaidenProject >> true
        def commandFactory = Mock(CommandFactory)
        1 * commandFactory.createCommand("valid-command") >> command
        def gaidenMain = new GaidenMain()
        gaidenMain.commandFactory = commandFactory

        when:
        gaidenMain.run("valid-command")

        then:
        1 * new GaidenConfigLoader() >> gaidenConfigLoader
        1 * gaidenConfigLoader.load(_) >> gaidenConfig
        Holders.config == gaidenConfig

        and:
        1 * command.execute([])
    }

    def "'run' should output usage if no argument"() {
        setup:
        def printStream = Mock(PrintStream)
        System.err = printStream

        and:
        def securityManager = Mock(SecurityManager)
        System.securityManager = securityManager

        when:
        new GaidenMain().run([] as String[])

        then:
        thrown(SecurityException)

        and:
        1 * printStream.println(GaidenMain.USAGE_MESSAGE)
        1 * securityManager.checkExit(1) >> {
            throw new SecurityException()
        }
    }

    def "'executeCommand' should output usage if invalid command"() {
        setup:
        def printStream = Mock(PrintStream)
        System.err = printStream

        and:
        def securityManager = Mock(SecurityManager)
        System.securityManager = securityManager

        when:
        new GaidenMain().executeCommand("invalid", null, [])

        then:
        thrown(SecurityException)

        and:
        1 * printStream.println(GaidenMain.USAGE_MESSAGE)
        1 * securityManager.checkExit(1) >> {
            throw new SecurityException()
        }
    }

    def "'executeCommand' should not execute command if GaidenConfig.groovy doesn't exist"() {
        setup:
        def gaidenBuild = Mock(GaidenCommand)
        gaidenBuild.onlyGaidenProject >> true

        and:
        def gaidenConfigFile = Stub(File)
        gaidenConfigFile.exists() >> false
        gaidenConfigFile.name >> "TEST_CONFIG_FILE_NAME"

        and:
        def printStream = Mock(PrintStream)
        System.err = printStream

        and:
        def securityManager = Mock(SecurityManager)
        System.securityManager = securityManager

        when:
        new GaidenMain().executeCommand("build", gaidenConfigFile, [])

        then:
        thrown(SecurityException)

        and:
        1 * printStream.println("ERROR: Not a Gaiden project (Cannot find TEST_CONFIG_FILE_NAME)")
        1 * securityManager.checkExit(1) >> {
            throw new SecurityException()
        }
    }

}
