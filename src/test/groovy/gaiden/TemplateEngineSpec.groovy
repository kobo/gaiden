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

class TemplateEngineSpec extends GaidenSpec {

    def "'make' should make a page with a template"() {
        setup:
        def templateEngine = new TemplateEngine('''
            |<html>
            |<head>
            |    <title>$title</title>
            |</head>
            |<body>
            |$content
            |</body>
            |</html>
            '''.stripMargin())

        and:
        def binding = [
            title: "Test Title",
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
        def templateEngine = new TemplateEngine('${resource("' + resourcePath + '")}')

        and:
        def binding = new BindingBuilder().setOutputPath(outputPath).build()

        when:
        def content = templateEngine.make(binding)

        then:
        content == expected

        where:
        resourcePath   | outputPath     | expected
        "/aaa/bbb.txt" | "ccc/ddd.html" | "../aaa/bbb.txt"
        "aaa/bbb.txt"  | "ccc/ddd.html" | "aaa/bbb.txt"
        ""             | "ccc/ddd.html" | ""
    }

    def "'make' should evaluate the 'tocPath'"() {
        setup:
        config.tocOutputFilePath = "toc.html"

        and:
        def templateEngine = new TemplateEngine('${tocPath}')
        def binding = new BindingBuilder().setOutputPath(outputPath).build()

        expect:
        templateEngine.make(binding) == expected

        where:
        outputPath     | expected
        "ddd.html"     | "toc.html"
        "ccc/ddd.html" | "../toc.html"
    }

}
