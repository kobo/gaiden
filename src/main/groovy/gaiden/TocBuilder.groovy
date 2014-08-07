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

import gaiden.message.MessageSource
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

/**
 * A builder of TOC.
 *
 * @author Hideki IGARASHI
 * @author Kazuki YAMAMOTO
 */
@Component
@CompileStatic
class TocBuilder {

    @Autowired
    MessageSource messageSource

    Toc build(List<Page> pages) {
        List<TocNode> tocNodes = []
        List<Integer> currentNumbers = []
        pages.each { Page page ->
            page.headers.each { Header header ->
                def tocNode = new TocNode()
                tocNode.page = page
                tocNode.header = header

                if (currentNumbers.size() == header.level) {
                    def last = currentNumbers.pop()
                    currentNumbers.push(++last)
                } else if (currentNumbers.size() < header.level) {
                    (header.level - currentNumbers.size()).times {
                        currentNumbers.push(1)
                    }
                } else {
                    (currentNumbers.size() - header.level).times {
                        currentNumbers.pop()
                    }
                    def last = currentNumbers.pop()
                    currentNumbers.push(++last)
                }
                tocNode.numbers = currentNumbers.collect { it }
                tocNodes << tocNode
            }
        }
        new Toc(tocNodes: tocNodes)
    }
}
