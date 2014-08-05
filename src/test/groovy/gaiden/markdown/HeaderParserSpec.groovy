/*
 * Copyright 2014 the original author or authors
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

package gaiden.markdown

import gaiden.GaidenSpec
import gaiden.Header
import org.pegdown.PegDownProcessor

class HeaderParserSpec extends GaidenSpec {

    def "parse headers from a content of markdwon"() {
        given:
            def pegDownProcessor = new PegDownProcessor()
            def rootNode = pegDownProcessor.parseMarkdown('''
Header1
=======

Header2
-------

# Header3
## Header4
### Header5
#### Header6
##### Header7
###### Header8
'''.toCharArray())
            def headerParser = new HeaderParser(rootNode)

        when:
            def headers = headerParser.headers

        then:
            assertHeaders headers, [
                [title: "Header1", level: 1],
                [title: "Header2", level: 2],
                [title: "Header3", level: 1],
                [title: "Header4", level: 2],
                [title: "Header5", level: 3],
                [title: "Header6", level: 4],
                [title: "Header7", level: 5],
                [title: "Header8", level: 6],
            ]
    }

    private static assertHeaders(List<Header> actual, List<Map> expected) {
        def headers = actual.collect { Header header ->
            [title: header.title, level: header.level]
        }
        assert headers == expected
        return true
    }
}
