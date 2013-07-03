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


class TocBuilderSpec extends Specification {

    def "'build' should return a toc"() {
        setup:
        def tocInputFile = GroovyMock(File)
        tocInputFile.text >> """
            "first.html"(title: 'first title')
            "second.html"(title: 'second title')
        """
        tocInputFile.exists() >> true

        and:
        def tocOutputPath = "toc.html"

        and:
        def templateEngine = new TemplateEngine(null, new File("src/test/resources/templates/simple-template.html").text, [title: "Gaiden"])

        when:
        Toc toc = new TocBuilder(templateEngine, tocInputFile, tocOutputPath).build()

        then:
        toc.path == "toc.html"
        toc.content == """<html>
                         |<head>
                         |    <title>Gaiden</title>
                         |</head>
                         |<body>
                         |<h1>Table of contents</h1>
                         |<ul>
                         |    <li>
                         |        <a href="first.html">first title</a>
                         |    </li>
                         |    <li>
                         |        <a href="second.html">second title</a>
                         |    </li>
                         |</ul>
                         |</body>
                         |</html>
                         |""".stripMargin()

        toMap(toc.node) == [
            "first.html": [title: "first title"],
            "second.html": [title: "second title"],
        ]
    }

    def "'build' should return a hierarchy toc"() {
        setup:
        def tocInputFile = GroovyMock(File)
        tocInputFile.text >> """
            "first.html"(title: 'first title')
            "second/"(title: 'second title') {
                "second/second1.html"(title: 'second 1 title')
            }
        """
        tocInputFile.exists() >> true

        and:
        def tocOutputPath = "toc.html"

        and:
        def templateEngine = new TemplateEngine(null, new File("src/test/resources/templates/simple-template.html").text, [title: "Gaiden"])

        when:
        Toc toc = new TocBuilder(templateEngine, tocInputFile, tocOutputPath).build()

        then:
        toc.path == "toc.html"
        toc.content == """<html>
                         |<head>
                         |    <title>Gaiden</title>
                         |</head>
                         |<body>
                         |<h1>Table of contents</h1>
                         |<ul>
                         |    <li>
                         |        <a href="first.html">first title</a>
                         |    </li>
                         |    <li>
                         |        <a href="second/">second title</a>
                         |        <ul>
                         |            <li>
                         |                <a href="second/second1.html">second 1 title</a>
                         |            </li>
                         |        </ul>
                         |    </li>
                         |</ul>
                         |</body>
                         |</html>
                         |""".stripMargin()

        toMap(toc.node) == [
            "first.html": [title: "first title"],
            "second/": [title: "second title", children: [
                "second/second1.html": [title: "second 1 title"]
            ]],
        ]
    }

    def "'build' should return the null object when the toc file does not exist"() {
        setup:
        def tocFile = Mock(File)
        tocFile.exists() >> false

        when:
        def toc = new TocBuilder(null, tocFile, null).build()

        then:
        toc instanceof NullToc
    }

    private Map toMap(Node node) {
        def processNode = { Node currentNode ->
            def currentNodeValues = [:]

            if (currentNode.attributes()) {
                currentNodeValues.putAll(currentNode.attributes())
            }

            if (currentNode.value()) {
                currentNodeValues.children = (currentNode.value() as List).collectEntries { Node childNode ->
                    [(childNode.name()): owner.call(childNode)]
                }
            }

            return currentNodeValues
        }

        processNode(node).children
    }

}