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

import java.nio.file.Files
import java.nio.file.Path

class TocBuilderSpec extends GaidenSpec {

    GaidenConfig gaidenConfig

    TocBuilder tocBuilder
    TemplateEngine templateEngine

    Path tocFile
    Path templateFile

    def setup() {
        templateFile = Files.createTempFile("layout", "html")
        this.templateFile.write '''
            |<html>
            |<head>
            |    <title>$title</title>
            |</head>
            |<body>
            |$content
            |</body>
            |</html>
            '''.stripMargin()

        tocFile = Files.createTempFile("Toc", "html")

        gaidenConfig = new GaidenConfig()
        gaidenConfig.templateFilePath = templateFile
        gaidenConfig.tocOutputFilePath = "test/toc.html"
        gaidenConfig.tocFilePath = tocFile
        gaidenConfig.title = "Test Title"
        gaidenConfig.tocTitle = "Test TOC Title"

        templateEngine = new TemplateEngine()
        templateEngine.gaidenConfig = gaidenConfig

        tocBuilder = new TocBuilder()
        tocBuilder.gaidenConfig = gaidenConfig
        tocBuilder.templateEngine = templateEngine
        tocBuilder.messageSource = messageSource
    }

    def clean() {
        Files.delete(templateFile)
        Files.delete(tocFile)
    }

    def "'build' should return a toc"() {
        setup:
            tocFile.write """
            |"first"(title: 'first title')
            |"second.md"(title: 'second title')
            |"third.html"(title: 'third title')
            """.stripMargin()

        and:
            def context = createBuildContext([[path: "first.md"], [path: "second.md"]])

        when:
            Toc toc = tocBuilder.build(context)

        then:
            toc.path == "test/toc.html"
            toc.content == """
            |<html>
            |<head>
            |    <title>Test Title</title>
            |</head>
            |<body>
            |<h1>Test TOC Title</h1>
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
            """.stripMargin()
    }

    def "'build' should not make a link if not found a page source"() {
        setup:
            tocFile.write """
            |"first"(title: 'first title')
            |"second.md"(title: 'second title')
            |"third.html"(title: 'third title')
            """.stripMargin()

        and:
            def systemErr = Mock(PrintStream)
            System.err = systemErr

        when:
            Toc toc = tocBuilder.build(emptyContext)

        then:
            toc.path == "test/toc.html"
            toc.content == """
            |<html>
            |<head>
            |    <title>Test Title</title>
            |</head>
            |<body>
            |<h1>Test TOC Title</h1>
            |<ul>
            |    <li>first title</li>
            |    <li>second title</li>
            |    <li>
            |        <a href="third.html">third title</a>
            |    </li>
            |</ul>
            |</body>
            |</html>
            """.stripMargin()

        and:
            1 * systemErr.println("WARNING: 'second.md' in the Table of Contents refers to non-existent page")
            1 * systemErr.println("WARNING: 'first' in the Table of Contents refers to non-existent page")
    }

    def "'build' should return a hierarchy toc"() {
        setup:
            tocFile.write """
            |"first"(title: 'first title')
            |"second/"(title: 'second title') {
            |    "second/second1"(title: 'second 1 title')
            |}
            """.stripMargin()

        and:
            def context = createBuildContext([[path: "first.md"], [path: "second/second1.md"]])

        when:
            Toc toc = tocBuilder.build(context)

        then:
            toc.path == "test/toc.html"
            toc.content == """
            |<html>
            |<head>
            |    <title>Test Title</title>
            |</head>
            |<body>
            |<h1>Test TOC Title</h1>
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
            """.stripMargin()
    }

    def "'build' should return a toc which contains a fragment"() {
        setup:
            tocFile.write "'first#fragment'(title: 'first title')"

        and:
            def context = createBuildContext([[path: "first.md"]])

        when:
            Toc toc = tocBuilder.build(context)

        then:
            toc.path == "test/toc.html"
            toc.content == """
            |<html>
            |<head>
            |    <title>Test Title</title>
            |</head>
            |<body>
            |<h1>Test TOC Title</h1>
            |<ul>
            |    <li>
            |        <a href="first.html#fragment">first title</a>
            |    </li>
            |</ul>
            |</body>
            |</html>
            """.stripMargin()
    }

    def "'build' should return the null when the toc file does not exist"() {
        setup:
            Files.delete(tocFile)

        when:
            def toc = tocBuilder.build(emptyContext)

        then:
            toc == null
    }

    def "'build' should not link a path with '#'"() {
        setup:
            tocFile.write """
            |"#first.html"(title: 'first title')
            |"#second/"(title: 'second title') {
            |    "second/second1.html"(title: 'second 1 title')
            |}
            """.stripMargin()

        when:
            Toc toc = tocBuilder.build(emptyContext)

        then:
            toc.path == "test/toc.html"
            toc.content == """
            |<html>
            |<head>
            |    <title>Test Title</title>
            |</head>
            |<body>
            |<h1>Test TOC Title</h1>
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
            """.stripMargin()
    }

    def "'build' should read a specified encoding"() {
        setup:
            tocFile.withOutputStream { out ->
                out.write(new File("src/test/resources/toc/shiftjis-toc.groovy").bytes)
            }

        and:
            gaidenConfig.inputEncoding = "Shift_JIS"

        when:
            Toc toc = tocBuilder.build(emptyContext)

        then:
            toc.path == "test/toc.html"
            toc.content == """
            |<html>
            |<head>
            |    <title>Test Title</title>
            |</head>
            |<body>
            |<h1>Test TOC Title</h1>
            |<ul>
            |    <li>Shift_JISのタイトルです</li>
            |</ul>
            |</body>
            |</html>
            """.stripMargin()
    }

    private BuildContext getEmptyContext() {
        def documentSource = new DocumentSource(pageSources: [])
        new BuildContext(documentSource: documentSource)
    }

}
