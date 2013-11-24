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
import gaiden.util.FileUtils

class PrevNextLinkSpec extends GaidenSpec {

    def prevLinkTemplateEngine = new TemplateEngine('$prevLink')
    def nextLinkTemplateEngine = new TemplateEngine('$nextLink')

    def "make a previous link"() {
        setup:
        tocFile.write """
            |"first.md"(title: 'first title')
            |"second.md"(title: 'second title')
            """.stripMargin()

        when:
        def firstContent = prevLinkTemplateEngine.make(bindingOf("first.md"))

        then:
        firstContent == ""

        when:
        def secondContent = prevLinkTemplateEngine.make(bindingOf("second.md"))

        then:
        secondContent == "<a href='first.html' class='prev'>&lt;&lt; first title</a>"
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
        firstContent == ""

        when:
        def secondContent = prevLinkTemplateEngine.make(bindingOf("second.md"))

        then:
        secondContent == "<a href='first.html' class='prev'>&lt;&lt; first title</a>"
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
        firstContent == ""

        when:
        def secondContent = prevLinkTemplateEngine.make(bindingOf("second.md"))

        then:
        secondContent == "<a href='first.html' class='prev'>&lt;&lt; first title</a>"
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
        firstContent == ""

        when:
        def secondContent = prevLinkTemplateEngine.make(bindingOf("second.md"))

        then:
        secondContent == "&lt;&lt; first title"
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
        firstContent == ""

        when:
        def secondContent = prevLinkTemplateEngine.make(bindingOf("second.md"))

        then:
        secondContent == "<a href='first.html#fragment' class='prev'>&lt;&lt; first title</a>"
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
        firstContent == ""

        when:
        def secondContent = prevLinkTemplateEngine.make(bindingOf("second.md"))

        then:
        secondContent == "<a href='first.html' class='prev'>&lt;&lt; first title</a>"
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
        firstContent == "<a href='second.html' class='next'>second title &gt;&gt;</a>"

        when:
        def secondContent = nextLinkTemplateEngine.make(bindingOf("second.md"))

        then:
        secondContent == ""
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
        firstContent == "<a href='second.html' class='next'>second title &gt;&gt;</a>"

        when:
        def secondContent = nextLinkTemplateEngine.make(bindingOf("second.md"))

        then:
        secondContent == ""
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
        firstContent == "<a href='second.html' class='next'>second title &gt;&gt;</a>"

        when:
        def secondContent = nextLinkTemplateEngine.make(bindingOf("second.md"))

        then:
        secondContent == ""
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
        firstContent == "second title &gt;&gt;"

        when:
        def secondContent = nextLinkTemplateEngine.make(bindingOf("second.md"))

        then:
        secondContent == ""
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
        firstContent == "<a href='second.html#fragment' class='next'>second title &gt;&gt;</a>"

        when:
        def secondContent = nextLinkTemplateEngine.make(bindingOf("second.md"))

        then:
        secondContent == ""
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
        firstContent == "<a href='second.html' class='next'>second title &gt;&gt;</a>"

        when:
        def secondContent = nextLinkTemplateEngine.make(bindingOf("second.md"))

        then:
        secondContent == ""
    }

    private Map<String, Object> bindingOf(String sourcePath) {
        def context = createBuildContext([[path: "first.md"], [path: "second.md"]])
        Toc toc = new TocBuilder(prevLinkTemplateEngine).build(context)

        def pageBuildContext = new PageBuildContext()
        pageBuildContext.toc = toc
        pageBuildContext.documentSource = context.documentSource

        new BindingBuilder()
            .setSourcePath(sourcePath)
            .setPageBuildContext(pageBuildContext)
            .setOutputPath(FileUtils.replaceExtension(sourcePath, "html"))
            .build()
    }

}
