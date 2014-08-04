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

class PrevNextLinkSpec extends GaidenSpec {

    Path tocFile
    Path prevLinkTemplateFile
    Path nextLinkTemplateFile

    TemplateEngine prevLinkTemplateEngine
    TemplateEngine nextLinkTemplateEngine

    def setup() {
        tocFile = gaidenConfig.tocFile
        if (Files.notExists(tocFile.parent)) {
            Files.createDirectories(tocFile.parent)
        }

        prevLinkTemplateEngine = new TemplateEngine()
        prevLinkTemplateFile = Files.createTempFile("prevLinkTemplate", "html")
        prevLinkTemplateFile << '<% if (prevPage) { %><a href=\'${prevPage.path}\'>${prevPage.title}</a><% } %>'

        prevLinkTemplateEngine.gaidenConfig = new GaidenConfig()
        prevLinkTemplateEngine.gaidenConfig.templateFilePath = prevLinkTemplateFile
        prevLinkTemplateEngine.gaidenConfig.tocFilePath = gaidenConfig

        nextLinkTemplateEngine = new TemplateEngine()
        nextLinkTemplateFile = Files.createTempFile("nextLinkTemplate", "html")
        nextLinkTemplateFile << '<% if (nextPage) { %><a href=\'${nextPage.path}\'>${nextPage.title}</a><% } %>'

        nextLinkTemplateEngine.gaidenConfig = new GaidenConfig()
        nextLinkTemplateEngine.gaidenConfig.templateFilePath = nextLinkTemplateFile
        nextLinkTemplateEngine.gaidenConfig.tocFilePath = tocFile
    }

    def cleanup() {
        Files.delete(prevLinkTemplateFile)
        Files.delete(nextLinkTemplateFile)
        Files.delete(tocFile)
    }

    def "make a previous link"() {
        setup:
            tocFile.write """
            |"first.md"(title: 'first title')
            |"second.md"(title: 'second title')
            """.stripMargin()

        when:
            def firstContent = prevLinkTemplateEngine.make(bindingOf("first.md"))

        then:
            !firstContent

        when:
            def secondContent = prevLinkTemplateEngine.make(bindingOf("second.md"))

        then:
            secondContent == "<a href='first.html'>first title</a>"
    }

    def "make a previous link with a TOC which file an extension is not specified"() {
        setup:
            tocFile.write """
            |"first"(title: 'first title')
            |"second"(title: 'second title')
            """.stripMargin()

        when:
            def firstContent = prevLinkTemplateEngine.make(bindingOf("first.md"))

        then:
            !firstContent

        when:
            def secondContent = prevLinkTemplateEngine.make(bindingOf("second.md"))

        then:
            secondContent == "<a href='first.html'>first title</a>"
    }

    def "make a previous link with a TOC which an output file path is specified"() {
        setup:
            tocFile.write """
            |"first.html"(title: 'first title')
            |"second.html"(title: 'second title')
            """.stripMargin()

        when:
            def firstContent = prevLinkTemplateEngine.make(bindingOf("first.md"))

        then:
            !firstContent

        when:
            def secondContent = prevLinkTemplateEngine.make(bindingOf("second.md"))

        then:
            secondContent == "<a href='first.html'>first title</a>"
    }

    def "make a previous link with TOC which a missing file is specified"() {
        setup:
            tocFile.write """
            |"missing.md"(title: 'first title')
            |"second.md"(title: 'second title')
            """.stripMargin()

        when:
            def firstContent = prevLinkTemplateEngine.make(bindingOf("first.md"))

        then:
            !firstContent

        when:
            def secondContent = prevLinkTemplateEngine.make(bindingOf("second.md"))

        then:
            secondContent == "<a href=''>first title</a>"
    }

    def "make a previous link with TOC which a fragment is specified"() {
        setup:
            tocFile.write """
            |"first.md#fragment"(title: 'first title')
            |"second.md#fragment"(title: 'second title')
            """.stripMargin()

        when:
            def firstContent = prevLinkTemplateEngine.make(bindingOf("first.md"))

        then:
            !firstContent

        when:
            def secondContent = prevLinkTemplateEngine.make(bindingOf("second.md"))

        then:
            secondContent == "<a href='first.html#fragment'>first title</a>"
    }

    def "make a previous link with TOC which a nested pages is specified"() {
        setup:
            tocFile.write """
            |"first.md"(title: 'first title') {
            |   "second.md"(title: 'second title')
            |}
            """.stripMargin()

        when:
            def firstContent = prevLinkTemplateEngine.make(bindingOf("first.md"))

        then:
            !firstContent

        when:
            def secondContent = prevLinkTemplateEngine.make(bindingOf("second.md"))

        then:
            secondContent == "<a href='first.html'>first title</a>"
    }

    def "make a next link"() {
        setup:
            tocFile.write """
            |"first.md"(title: 'first title')
            |"second.md"(title: 'second title')
            """.stripMargin()

        when:
            def firstContent = nextLinkTemplateEngine.make(bindingOf("first.md"))

        then:
            firstContent == "<a href='second.html'>second title</a>"

        when:
            def secondContent = nextLinkTemplateEngine.make(bindingOf("second.md"))

        then:
            !secondContent
    }

    def "make a next link with a TOC which file an extension is not specified"() {
        setup:
            tocFile.write """
            |"first"(title: 'first title')
            |"second"(title: 'second title')
            """.stripMargin()

        when:
            def firstContent = nextLinkTemplateEngine.make(bindingOf("first.md"))

        then:
            firstContent == "<a href='second.html'>second title</a>"

        when:
            def secondContent = nextLinkTemplateEngine.make(bindingOf("second.md"))

        then:
            !secondContent
    }

    def "make a next link with a TOC which an output file path is specified"() {
        setup:
            tocFile.write """
            |"first.html"(title: 'first title')
            |"second.html"(title: 'second title')
            """.stripMargin()

        when:
            def firstContent = nextLinkTemplateEngine.make(bindingOf("first.md"))

        then:
            firstContent == "<a href='second.html'>second title</a>"

        when:
            def secondContent = nextLinkTemplateEngine.make(bindingOf("second.md"))

        then:
            !secondContent
    }

    def "make a next link with TOC which a missing file is specified"() {
        setup:
            tocFile.write """
            |"first.md"(title: 'first title')
            |"missing.md"(title: 'second title')
            """.stripMargin()

        when:
            def firstContent = nextLinkTemplateEngine.make(bindingOf("first.md"))

        then:
            firstContent == "<a href=''>second title</a>"

        when:
            def secondContent = nextLinkTemplateEngine.make(bindingOf("second.md"))

        then:
            !secondContent
    }

    def "make a next link with TOC which a fragment is specified"() {
        setup:
            tocFile.write """
            |"first.md#fragment"(title: 'first title')
            |"second.md#fragment"(title: 'second title')
            """.stripMargin()

        when:
            def firstContent = nextLinkTemplateEngine.make(bindingOf("first.md"))

        then:
            firstContent == "<a href='second.html#fragment'>second title</a>"

        when:
            def secondContent = nextLinkTemplateEngine.make(bindingOf("second.md"))

        then:
            !secondContent
    }

    def "make a next link with TOC which a nested pages is specified"() {
        setup:
            tocFile.write """
            |"first.md"(title: 'first title') {
            |   "second.md"(title: 'second title')
            |}
            """.stripMargin()

        when:
            def firstContent = nextLinkTemplateEngine.make(bindingOf("first.md"))

        then:
            firstContent == "<a href='second.html'>second title</a>"

        when:
            def secondContent = nextLinkTemplateEngine.make(bindingOf("second.md"))

        then:
            !secondContent
    }

    private Map<String, Object> bindingOf(String filename) {
        def context = createBuildContext([[path: "first.md"], [path: "second.md"]])
        def pageReferenceFactory = new PageReferenceFactory(gaidenConfig: gaidenConfig)

        def tocBuilder = new TocBuilder()
        tocBuilder.templateEngine = prevLinkTemplateEngine
        tocBuilder.gaidenConfig = gaidenConfig
        tocBuilder.messageSource = messageSource
        tocBuilder.pageReferenceFactory = pageReferenceFactory
        def toc = tocBuilder.build(context)

        def pageBuildContext = new PageBuildContext()
        pageBuildContext.toc = toc
        pageBuildContext.documentSource = context.documentSource

        new BindingBuilder(gaidenConfig, pageReferenceFactory)
            .setPageSource(context.documentSource.pageSources.find { it.path.fileName.toString() == filename })
            .setPageBuildContext(pageBuildContext)
            .build()
    }
}
