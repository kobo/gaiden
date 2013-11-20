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

import gaiden.GaidenMain
import spock.lang.Specification

class VersionSpec extends Specification {

    def savedSystemOut

    def setup() {
        savedSystemOut = System.out
    }

    def cleanup() {
        System.out = savedSystemOut
    }

    def "'version' command should display a version"() {
        setup:
        def printStream = Mock(PrintStream)
        System.out = printStream

        when:
        new GaidenMain().run([command] as String[])

        then:
        1 * printStream.println({ it =~ /Version:/ })

        where:
        command << ["-v", "--version", "version"]
    }
}
