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
import gaiden.exception.GaidenException
import gaiden.message.MessageSource
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

    def "'main' should catch an exception and output a message"() {
        setup:
        def printStream = Mock(PrintStream)
        System.err = printStream

        and:
        def messageSource = Stub(MessageSource)
        messageSource.getMessage("test.key", _) >> "Test Message"
        Holders.messageSource = messageSource

        and:
        def gaidenMain = Stub(GaidenMain)
        gaidenMain.run(_) >> { throw new GaidenException("test.key") }
        GroovySpy(GaidenMain, global: true)
        new GaidenMain() >> gaidenMain

        and:
        def securityManager = Mock(SecurityManager)
        System.securityManager = securityManager

        when:
        GaidenMain.main(null)

        then:
        thrown(SecurityException)

        and:
        1 * printStream.println("Test Message")
        1 * securityManager.checkExit(1) >> {
            throw new SecurityException()
        }
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

        and:
        def savedMessageSource = Holders.messageSource
        Holders.messageSource = new MessageSource("message.test_messages")

        when:
        gaidenMain.run("valid-command")

        then:
        Holders.messageSource != null

        and:
        1 * new GaidenConfigLoader() >> gaidenConfigLoader
        1 * gaidenConfigLoader.load(_) >> gaidenConfig
        Holders.config == gaidenConfig

        and:
        1 * command.execute([])

        cleanup:
        Holders.messageSource = savedMessageSource
    }

    def "'run' should output usage if no argument"() {
        when:
        new GaidenMain().run([] as String[])

        then:
        def e = thrown(GaidenException)
        e.key == "usage"
    }

    def "'executeCommand' should output usage if invalid command"() {
        when:
        new GaidenMain().executeCommand("invalid", null, [])

        then:
        def e = thrown(GaidenException)
        e.key == "usage"
    }

    def "'executeCommand' should not execute command if GaidenConfig.groovy doesn't exist"() {
        setup:
        def gaidenBuild = Mock(GaidenCommand)
        gaidenBuild.onlyGaidenProject >> true

        and:
        def gaidenConfigFile = Stub(File)
        gaidenConfigFile.exists() >> false
        gaidenConfigFile.name >> "TEST_CONFIG_FILE_NAME"

        when:
        new GaidenMain().executeCommand("build", gaidenConfigFile, [])

        then:
        def e = thrown(GaidenException)
        e.key == "not.gaiden.project.error"
    }

}
