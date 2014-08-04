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

import gaiden.context.PageBuildContext

import java.nio.file.Files
import java.nio.file.Path

class TemplateEngineSpec extends GaidenSpec {

    Path templateFile
    TemplateEngine templateEngine
    PageReferenceFactory pageReferenceFactory

    def setup() {
        if (Files.notExists(gaidenConfig.templateFile.parent)) {
            Files.createDirectories(gaidenConfig.templateFile.parent)
        }
        templateFile = gaidenConfig.templateFile

        templateEngine = new TemplateEngine()
        templateEngine.gaidenConfig = gaidenConfig

        pageReferenceFactory = new PageReferenceFactory(gaidenConfig: gaidenConfig)
    }

    def "'make' should make a page with a template"() {
        setup:
            templateFile.write '''
            |<html>
            |<head>
            |    <title>$title</title>
            |</head>
            |<body>
            |$content
            |</body>
            |</html>
            '''.stripMargin()

        and:
            def binding = [
                title  : "Test Title",
                content: "<h1>Hello</h1>"
            ]

        when:
            def content = templateEngine.make(binding)

        then:
            content == '''
            |<html>
            |<head>
            |    <title>Test Title</title>
            |</head>
            |<body>
            |<h1>Hello</h1>
            |</body>
            |</html>
            '''.stripMargin()
    }

    def "'make' should evaluate the 'resource'"() {
        setup:
            templateFile.write '${resource("' + resourcePath + '")}'

        and:
            def binding = new BindingBuilder(gaidenConfig, pageReferenceFactory)
                .setPageSource(createPageSource(pageSource))
                .setPageBuildContext(new PageBuildContext(toc: new Toc(tocNodes: [])))
                .build()

        when:
            def content = templateEngine.make(binding)

        then:
            content == expected

        where:
            resourcePath   | pageSource   | expected
            "/aaa/bbb.txt" | "ccc/ddd.md" | "../aaa/bbb.txt"
            "aaa/bbb.txt"  | "ccc/ddd.md" | "aaa/bbb.txt"
    }

    def "'make' should evaluate the 'tocPath'"() {
        setup:
            templateFile.write '${tocPath}'
            def binding = new BindingBuilder(gaidenConfig, pageReferenceFactory)
                .setPageSource(createPageSource(pageSource))
                .setPageBuildContext(new PageBuildContext(toc: new Toc(tocNodes: [])))
                .build()

        expect:
            templateEngine.make(binding) == expected

        where:
            pageSource   | expected
            "ddd.md"     | "toc.html"
            "ccc/ddd.md" | "../toc.html"
    }
}
