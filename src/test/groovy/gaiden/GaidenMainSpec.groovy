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

import gaiden.command.GaidenBuild
import gaiden.command.GaidenClean
import spock.lang.Specification

class GaidenMainSpec extends Specification {

    private static final String USAGE_MESSAGE = """\
Usage: gaiden <command>

   commands:
       build  Build pages from source pages
       clean  Clean the build directory
"""

    def "'run' should run the build command"() {
        setup:
        GroovyMock(GaidenConfigLoader, global: true)
        def gaidenConfigLoader = Mock(GaidenConfigLoader)
        def gaidenConfig = new GaidenConfig()

        and:
        GroovyMock(GaidenBuild, global: true)
        def gaidenBuild = Mock(GaidenBuild)

        when:
        new GaidenMain().run("build")

        then:
        1 * new GaidenConfigLoader() >> gaidenConfigLoader
        1 * gaidenConfigLoader.load(_) >> gaidenConfig
        Holders.config == gaidenConfig

        and:
        1 * new GaidenBuild() >> gaidenBuild
        1 * gaidenBuild.execute()
    }

    def "'run' should run the clean command"() {
        setup:
        GroovyMock(GaidenConfigLoader, global: true)
        def gaidenConfigLoader = Mock(GaidenConfigLoader)

        and:
        GroovyMock(GaidenClean, global: true)
        def gaidenClean = Mock(GaidenClean)

        when:
        new GaidenMain().run("clean")

        then:
        1 * new GaidenConfigLoader() >> gaidenConfigLoader
        1 * gaidenConfigLoader.load(_)

        and:
        1 * new GaidenClean() >> gaidenClean
        1 * gaidenClean.execute()
    }

    def "'run' should output usage if invalid command"() {
        setup:
        GroovyMock(GaidenConfigLoader, global: true)
        def gaidenConfigLoader = Mock(GaidenConfigLoader)

        and:
        def saved = System.out
        def printStream = Mock(PrintStream)
        System.out = printStream

        when:
        new GaidenMain().run("invalid")

        then:
        1 * new GaidenConfigLoader() >> gaidenConfigLoader
        1 * gaidenConfigLoader.load(_)

        and:
        1 * printStream.println(USAGE_MESSAGE)

        cleanup:
        System.out = saved
    }

    def "'run' should output usage if null command"() {
        setup:
        def saved = System.out
        def printStream = Mock(PrintStream)
        System.out = printStream

        when:
        new GaidenMain().run([] as String[])

        then:
        1 * printStream.println(USAGE_MESSAGE)

        cleanup:
        System.out = saved
    }

}
