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

import spock.lang.Specification

class SourceCollectorSpec extends Specification {

    SourceCollector collector
    GaidenConfig gaidenConfig

    def setup() {
        gaidenConfig = new GaidenConfig()
        collector = new SourceCollector()
        collector.gaidenConfig = gaidenConfig
    }

    def "'collect' should return a document source"() {
        setup:
            gaidenConfig.pagesDirectoryPath = "src/test/resources/flat-pages"

        when:
            def documentSource = collector.collect()

        then:
            documentSource.pageSources.size() == 2

        and:
            documentSource.pageSources*.path as Set == ['flat-1.md', 'flat-2.md'] as Set

        and:
            documentSource.pageSources.collect { it.content.trim() } as Set == ['# Flat 1', '# Flat 2'] as Set
    }

    def "'collect' should return markdown files of valid filename"() {
        setup:
            gaidenConfig.pagesDirectoryPath = "src/test/resources/including-invalid-filename-pages"

        when:
            def documentSource = collector.collect()

        then:
            documentSource.pageSources.size() == 2

        and:
            documentSource.pageSources*.path as Set == ['short-extension.md', 'long-extension.markdown'] as Set
    }

    def "'collect' should return markdown files recursively"() {
        setup:
            gaidenConfig.pagesDirectoryPath = "src/test/resources/recursive-pages"

        when:
            def documentSource = collector.collect()

        then:
            documentSource.pageSources.size() == 7

        and:
            documentSource.pageSources*.path as Set == [
                "first-1.md", "first-2.md",
                "second/second-1.md", "second/second-2.md", "second/second-3.md",
                "second/third/third-1.md", "second/third/third-2.md",
            ] as Set
    }

    def "'collect' should read a specified encoding"() {
        setup:
            gaidenConfig.pagesDirectoryPath = "src/test/resources/shiftjis-pages"
            gaidenConfig.inputEncoding = "Shift_JIS"

        when:
            def documentSource = collector.collect()

        then:
            documentSource.pageSources.size() == 1

        and:
            def pageSource = documentSource.pageSources.first()
            pageSource.content == "これはShift_JISで書かれた文章です\n"
    }

}
