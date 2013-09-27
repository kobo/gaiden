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

package gaiden.util

import spock.lang.Specification

class FileUtilsSpec extends Specification {

    def "'getRelativePathForFileToFile' should return a relative path"() {
        expect:
        FileUtils.getRelativePathForFileToFile(fromFile, toFile) == expected

        where:
        from                    | to                    | expected
        "/aaa/bbb/from.txt"     | "/aaa/bbb/to.txt"     | "to.txt"
        "/aaa/bbb/from.txt"     | "/aaa/to.txt"         | "../to.txt"
        "/aaa/bbb/from.txt"     | "/aaa/bbb/ccc/to.txt" | "ccc/to.txt"
        "/aaa/bbb/from.txt"     | "/aaa/ccc/to.txt"     | "../ccc/to.txt"
        "/aaa/bbb/from.txt"     | "/ddd/ccc/to.txt"     | "../../ddd/ccc/to.txt"
        "/aaa/bbb/ccc.txt"      | "/aaa/bbb/ccc.txt"    | "ccc.txt"
        "/aaa/bbb/ccc/from.txt" | "/aaa/xxx/ccc/to.txt" | "../../xxx/ccc/to.txt"
        "../aaa/bbb/from.txt"   | "../aaa/ccc/to.txt"   | "../ccc/to.txt"
        "/aaa/bbb/"             | "/aaa/bbb/to.txt"     | "bbb/to.txt"  // When give a directory as 'fromFile', like this.
        "/aaa/bbb/from.txt"     | "/aaa/bbb/"           | "../bbb"      // When give a directory as 'toFile', like this.
        "/from.txt"             | "/to.txt"             | "to.txt"

        fromFile = new File(from)
        toFile = new File(to)
    }

    def "'getRelativePathForDirectoryToFile' should return a relative path"() {
        expect:
        FileUtils.getRelativePathForDirectoryToFile(fromFile, toFile) == expected

        where:
        from                | to                    | expected
        "/aaa/bbb/"         | "/aaa/bbb/to.txt"     | "to.txt"
        "/aaa/bbb/"         | "/aaa/to.txt"         | "../to.txt"
        "/aaa/bbb/"         | "/aaa/bbb/ccc/to.txt" | "ccc/to.txt"
        "/aaa/bbb/"         | "/aaa/ccc/to.txt"     | "../ccc/to.txt"
        "/aaa/bbb/"         | "/ddd/ccc/to.txt"     | "../../ddd/ccc/to.txt"
        "/aaa/bbb/"         | "/aaa/bbb/ccc.txt"    | "ccc.txt"
        "/aaa/bbb/ccc/"     | "/aaa/xxx/ccc/to.txt" | "../../xxx/ccc/to.txt"
        "./bbb/ccc/"        | "./bbb/ccc/to.txt"    | "to.txt"
        "/aaa/bbb/from.txt" | "/aaa/bbb/to.txt"     | "../to.txt"  // When give a file as 'fromFile', like this.
        "/aaa/bbb/"         | "/aaa/bbb/"           | "../bbb"     // When give a directory as 'toFile', like this.
        "/"                 | "/to.txt"             | "to.txt"
        "/"                 | "/bbb/"               | "bbb"

        fromFile = new File(from)
        toFile = new File(to)
    }

    def "'replaceExtension' should replace extension of target filename"() {
        expect:
        FileUtils.replaceExtension(filename, extension) == replaced

        where:
        filename                  | extension | replaced
        "/path/to/target.md"      | "html"    | "/path/to/target.html"
        "/path/to/target.html.md" | "html"    | "/path/to/target.html.html"
        "/path/to/target"         | "html"    | "/path/to/target"
    }

    def "'removeExtension' should remove extension of target filename"() {
        expect:
        FileUtils.removeExtension(filename) == expected

        where:
        filename                  | expected
        "/path/to/target.md"      | "/path/to/target"
        "/path/to/target.html.md" | "/path/to/target.html"
        "/path/to/target"         | "/path/to/target"
    }

    def "'getExtension' should return extension of target filename"() {
        expect:
        FileUtils.getExtension(filename) == expected

        where:
        filename         | expected
        "target.md"      | "md"
        "target.html.md" | "md"
        "target"         | null
    }

}
