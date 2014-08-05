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
import gaiden.markdown.GaidenMarkdownProcessor

import java.nio.file.Files

class PageBuilderSpec extends GaidenSpec {

    TemplateEngine templateEngine
    GaidenMarkdownProcessor gaidenMarkdownProcessor

    def setup() {
        def templateFile = gaidenConfig.templateFile
        if (Files.notExists(templateFile.parent)) {
            Files.createDirectories(templateFile.parent)
        }
        templateFile << '''
            |<html>
            |<head>
            |    <title>$title</title>
            |</head>
            |<body>
            |$content
            |</body>
            |</html>
            '''.stripMargin()

        gaidenConfig.title = "Test Title"

        templateEngine = new TemplateEngine()
        templateEngine.gaidenConfig = gaidenConfig

        gaidenMarkdownProcessor = new GaidenMarkdownProcessor()
        gaidenMarkdownProcessor.gaidenConfig = gaidenConfig
        gaidenMarkdownProcessor.messageSource = messageSource
    }

    def "'build' should build a page"() {
        given:
            def pageSource = createPageSource("test.md", '''
            |Header1
            |=======
            |
            |Header2
            |-------
            |
            |# Header3
            |## Header4
            |### Header5
            |#### Header6
            |##### Header7
            |###### Header8
            '''.stripMargin())
            def documentSource = new DocumentSource(pageSources: [pageSource])
            def context = new PageBuildContext(documentSource: documentSource, toc: new Toc(tocNodes: []))

        when:
            def pageBuilder = new PageBuilder()
            pageBuilder.templateEngine = templateEngine
            pageBuilder.gaidenConfig = gaidenConfig
            pageBuilder.markdownProcessor = gaidenMarkdownProcessor
            def page = pageBuilder.build(context, pageSource)

        then:
            page.path == gaidenConfig.outputDirectory.resolve("test.html")
            page.content == '''
            |<html>
            |<head>
            |    <title>Test Title</title>
            |</head>
            |<body>
            |<h1>Header1</h1><h2>Header2</h2><h1>Header3</h1><h2>Header4</h2><h3>Header5</h3><h4>Header6</h4><h5>Header7</h5><h6>Header8</h6>
            |</body>
            |</html>
            '''.stripMargin()
            page.headers.size() == 8
    }
}
