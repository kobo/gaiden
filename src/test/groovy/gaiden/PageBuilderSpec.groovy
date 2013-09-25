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

import gaiden.markdown.GaidenMarkdownProcessor
import spock.lang.Specification

class PageBuilderSpec extends Specification {

    def "'build' should build a page"() {
        setup:
        def templateEngine = Mock(TemplateEngine)
        def markdownProcessor = Mock(GaidenMarkdownProcessor)

        and:
        def builder = new PageBuilder(templateEngine, markdownProcessor)

        and:
        def pageSource = new PageSource(path: "test.md", content: "SOURCE_CONTENT")

        when:
        def page = builder.build(pageSource)

        then:
        1 * markdownProcessor.markdownToHtml("SOURCE_CONTENT", "test.html") >> "PROCESSED_CONTENT"
        1 * templateEngine.make([content: "PROCESSED_CONTENT", outputPath: "test.html"])

        and:
        page.originalPath == "test.md"
    }

}
