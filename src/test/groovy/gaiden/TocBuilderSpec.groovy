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

import gaiden.context.BuildContext
import gaiden.message.MessageSource
import spock.lang.Specification


class TocBuilderSpec extends Specification {

    def savedSystemOut
    def savedSystemErr
    def savedSystemSecurityManager

    def setup() {
        savedSystemOut = System.out
        savedSystemErr = System.err
        savedSystemSecurityManager = System.securityManager

        Holders.messageSource = new MessageSource()
    }

    def cleanup() {
        System.out = savedSystemOut
        System.err = savedSystemErr
        System.securityManager = savedSystemSecurityManager
    }

    def "'build' should return a toc"() {
        setup:
        def tocInputFile = GroovyMock(File)
        tocInputFile.getText("UTF-8") >> """
            "first"(title: 'first title')
            "second.md"(title: 'second title')
            "third.html"(title: 'third title')
        """
        tocInputFile.exists() >> true

        and:
        def tocOutputPath = "toc.html"
        def tocTitle = "Test TocBuilder Title"

        and:
        def templateEngine = new TemplateEngine(new File("/output"),
            new File("src/test/resources/templates/simple-template.html").text, [title: "Gaiden", tocPath: "toc.html"])

        and:
        def pageSources = [
            new PageSource(path: "first.md"),
            new PageSource(path: "second.md"),
        ]
        def documentSource = new DocumentSource(pageSources: pageSources)
        def context = new BuildContext(documentSource: documentSource)

        when:
        Toc toc = new TocBuilder(templateEngine, tocInputFile, tocOutputPath, tocTitle, "UTF-8").build(context)

        then:
        toc.path == "toc.html"
        toc.content == """<html>
                         |<head>
                         |    <title>Gaiden</title>
                         |</head>
                         |<body>
                         |<h1>$tocTitle</h1>
                         |<ul>
                         |    <li>
                         |        <a href="first.html">first title</a>
                         |    </li>
                         |    <li>
                         |        <a href="second.html">second title</a>
                         |    </li>
                         |    <li>
                         |        <a href="third.html">third title</a>
                         |    </li>
                         |</ul>
                         |</body>
                         |</html>
                         |""".stripMargin()

        toMap(toc.node) == [
            "first": [title: "first title"],
            "second.md": [title: "second title"],
            "third.html": [title: "third title"],
        ]
    }

    def "'build' should not make a link if not found a page source"() {
        setup:
        def tocInputFile = GroovyMock(File)
        tocInputFile.getText("UTF-8") >> """
            "first"(title: 'first title')
            "second.md"(title: 'second title')
            "third.html"(title: 'third title')
        """
        tocInputFile.exists() >> true

        and:
        def printStream = Mock(PrintStream)
        System.err = printStream

        and:
        def tocOutputPath = "toc.html"
        def tocTitle = "Test TocBuilder Title"

        and:
        def templateEngine = new TemplateEngine(new File("/output"),
            new File("src/test/resources/templates/simple-template.html").text, [title: "Gaiden", tocPath: "toc.html"])

        when:
        Toc toc = new TocBuilder(templateEngine, tocInputFile, tocOutputPath, tocTitle, "UTF-8").build(contextOfEmptyPageSource)

        then:
        toc.path == "toc.html"
        toc.content == """<html>
                         |<head>
                         |    <title>Gaiden</title>
                         |</head>
                         |<body>
                         |<h1>$tocTitle</h1>
                         |<ul>
                         |    <li>first title</li>
                         |    <li>second title</li>
                         |    <li>
                         |        <a href="third.html">third title</a>
                         |    </li>
                         |</ul>
                         |</body>
                         |</html>
                         |""".stripMargin()

        and:
        1 * printStream.println("WARNING: 'second.md' in the Table of Contents refers to non-existent page")
        1 * printStream.println("WARNING: 'first' in the Table of Contents refers to non-existent page")
    }

    def "'build' should return a hierarchy toc"() {
        setup:
        def tocInputFile = GroovyMock(File)
        tocInputFile.getText("UTF-8") >> """
            "first"(title: 'first title')
            "second/"(title: 'second title') {
                "second/second1"(title: 'second 1 title')
            }
        """
        tocInputFile.exists() >> true

        and:
        def tocOutputPath = "toc.html"
        def tocTitle = "Test TocBuilder Title"

        and:
        def templateEngine = new TemplateEngine(new File("/output"),
            new File("src/test/resources/templates/simple-template.html").text, [title: "Gaiden", tocPath: "toc.html"])

        and:
        def pageSources = [
            new PageSource(path: "first.md"),
            new PageSource(path: "second/second1.md"),
        ]
        def documentSource = new DocumentSource(pageSources: pageSources)
        def context = new BuildContext(documentSource: documentSource)

        when:
        Toc toc = new TocBuilder(templateEngine, tocInputFile, tocOutputPath, tocTitle, "UTF-8").build(context)

        then:
        toc.path == "toc.html"
        toc.content == """<html>
                         |<head>
                         |    <title>Gaiden</title>
                         |</head>
                         |<body>
                         |<h1>$tocTitle</h1>
                         |<ul>
                         |    <li>
                         |        <a href="first.html">first title</a>
                         |    </li>
                         |    <li>second title
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
            "first": [title: "first title"],
            "second/": [title: "second title", children: [
                "second/second1": [title: "second 1 title"]
            ]],
        ]
    }

    def "'build' should return a toc which contains a fragment"() {
        setup:
        def tocInputFile = GroovyMock(File)
        tocInputFile.getText("UTF-8") >> """
            "first#fragment"(title: 'first title')
        """
        tocInputFile.exists() >> true

        and:
        def tocOutputPath = "toc.html"
        def tocTitle = "Test TocBuilder Title"

        and:
        def templateEngine = new TemplateEngine(new File("/output"),
            new File("src/test/resources/templates/simple-template.html").text, [title: "Gaiden", tocPath: "toc.html"])

        and:
        def pageSources = [
            new PageSource(path: "first.md"),
        ]
        def documentSource = new DocumentSource(pageSources: pageSources)
        def context = new BuildContext(documentSource: documentSource)

        when:
        Toc toc = new TocBuilder(templateEngine, tocInputFile, tocOutputPath, tocTitle, "UTF-8").build(context)

        then:
        toc.path == "toc.html"
        toc.content == """<html>
                         |<head>
                         |    <title>Gaiden</title>
                         |</head>
                         |<body>
                         |<h1>$tocTitle</h1>
                         |<ul>
                         |    <li>
                         |        <a href="first.html#fragment">first title</a>
                         |    </li>
                         |</ul>
                         |</body>
                         |</html>
                         |""".stripMargin()

        toMap(toc.node) == [
            "first#fragment": [title: "first title"],
        ]
    }

    def "'build' should return the null object when the toc file does not exist"() {
        setup:
        def tocFile = Mock(File)
        tocFile.exists() >> false

        when:
        def toc = new TocBuilder(null, tocFile, null, null, null).build(contextOfEmptyPageSource)

        then:
        toc instanceof NullToc
    }

    def "'build' should not link a path with '#'"() {
        setup:
        def tocInputFile = GroovyMock(File)
        tocInputFile.getText("UTF-8") >> """
            "#first.html"(title: 'first title')
            "#second/"(title: 'second title') {
                "second/second1.html"(title: 'second 1 title')
            }
        """
        tocInputFile.exists() >> true

        and:
        def tocOutputPath = "toc.html"
        def tocTitle = "Test TocBuilder Title"

        and:
        def templateEngine = new TemplateEngine(new File("/output"),
            new File("src/test/resources/templates/simple-template.html").text, [title: "Gaiden", tocPath: "toc.html"])

        when:
        Toc toc = new TocBuilder(templateEngine, tocInputFile, tocOutputPath, tocTitle, "UTF-8").build(contextOfEmptyPageSource)

        then:
        toc.path == "toc.html"
        toc.content == """<html>
                         |<head>
                         |    <title>Gaiden</title>
                         |</head>
                         |<body>
                         |<h1>$tocTitle</h1>
                         |<ul>
                         |    <li>first title</li>
                         |    <li>second title
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
            "#first.html": [title: "first title"],
            "#second/": [title: "second title", children: [
                "second/second1.html": [title: "second 1 title"]
            ]],
        ]
    }

    def "'build' should read a specified encoding"() {
        setup:
        def tocInputFile = new File("src/test/resources/toc/shiftjis-toc.groovy")

        and:
        def tocOutputPath = "toc.html"
        def tocTitle = "Test TocBuilder Title"

        and:
        def templateEngine = new TemplateEngine(new File("/output"),
            new File("src/test/resources/templates/simple-template.html").text, [title: "Gaiden", tocPath: "toc.html"])

        when:
        Toc toc = new TocBuilder(templateEngine, tocInputFile, tocOutputPath, tocTitle, "Shift_JIS").build(contextOfEmptyPageSource)

        then:
        toc.path == "toc.html"
        toc.content == """<html>
                         |<head>
                         |    <title>Gaiden</title>
                         |</head>
                         |<body>
                         |<h1>$tocTitle</h1>
                         |<ul>
                         |    <li>Shift_JISのタイトルです</li>
                         |</ul>
                         |</body>
                         |</html>
                         |""".stripMargin()

        toMap(toc.node) == ["shiftjis": [title: "Shift_JISのタイトルです"]]
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

    private BuildContext getContextOfEmptyPageSource() {
        def documentSource = new DocumentSource(pageSources: [])
        new BuildContext(documentSource: documentSource)
    }

}
