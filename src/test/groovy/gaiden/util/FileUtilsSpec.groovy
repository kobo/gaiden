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

import groovy.io.FileType
import spock.lang.Specification

class FileUtilsSpec extends Specification {

    def "'getRelativePath' should return a relative path"() {
        expect:
        FileUtils.getRelativePath(baseFile, targetFile) == expected

        where:
        base        | target                       | expected
        '/path/to'  | '/path/to/target.txt'        | 'target.txt'
        '/path/to/' | '/path/to/target.txt'        | 'target.txt'
        '/path/to'  | '/path/to/sub/target.txt'    | 'sub/target.txt'
        '/path/to'  | '/path/to/sub/../target.txt' | 'target.txt'
        './dir'     | './dir/target.txt'           | 'target.txt'

        baseFile = new File(base)
        targetFile = new File(target)
    }

    def "'getRelativePath' should cause assertion error when target isn't into base"() {
        when:
        FileUtils.getRelativePath(new File('/path/to'), new File('/other/target.txt'))

        then:
        thrown(AssertionError)
    }

    def "'replaceExtension' should replace extension of target filename"() {
        expect:
        FileUtils.replaceExtension("/path/to/target.md", "html") == "/path/to/target.html"
    }

    private Set getFiles(File file) {
        def files = []
        file.eachFileRecurse(FileType.FILES) {
            files << FileUtils.getRelativePath(file, it)
        }
        files
    }

}
