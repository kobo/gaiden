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

package gaiden.markdown

import gaiden.DocumentSource
import gaiden.GaidenSpec
import gaiden.PageSource
import gaiden.SourceCollector
import gaiden.context.PageBuildContext

class GaidenLinkRendererSpec extends GaidenSpec {

    def "'render' should return a rendering object which has replaced link url"() {
        setup:
        def documentSource = new DocumentSource(pageSources: [
            new PageSource(path: "first.md"),
            new PageSource(path: "path/second.md"),
        ])
        createFiles("first.md", "path/second.md", pageSourcePath)
        def context = new PageBuildContext(documentSource: documentSource)
        def pageSource = new PageSource(path: pageSourcePath)
        def linkRenderer = new GaidenLinkRenderer(context, pageSource, pagesDirectory)

        when:
        def rendering = linkRenderer.render(path, "Test Title", "Test Text")

        then:
        rendering.href == renderingHref
        rendering.text == "Test Text"

        where:
        pageSourcePath | path                 | renderingHref
        "test.md"      | "first.md"           | "first.html"
        "test.md"      | "first"              | "first.html"
        "test.md"      | "./first.md"         | "first.html"
        "test.md"      | "./first"            | "first.html"
        "test.md"      | "/first.md"          | "first.html"
        "test.md"      | "/first"             | "first.html"
        "path/test.md" | "../first.md"        | "../first.html"
        "path/test.md" | "/first.md"          | "../first.html"
        "path/test.md" | "second.md"          | "second.html"
        "path/test.md" | "/path/second.md"    | "second.html"
        "test.md"      | "first.md#fragment"  | "first.html#fragment"
        "test.md"      | "first#fragment"     | "first.html#fragment"
        "test.md"      | "/first.md#fragment" | "first.html#fragment"
    }

    def "'render' should not replace path if not found a page source"() {
        setup:
        def context = new PageBuildContext(documentSource: new DocumentSource())
        def pageSource = new PageSource(path: pageSourcePath)
        def linkRenderer = new GaidenLinkRenderer(context, pageSource, pagesDirectory)
        createFile(pageSourcePath)

        and:
        def printStream = Mock(PrintStream)
        System.err = printStream

        when:
        def rendering = linkRenderer.render(path, "Test Title", "Test Text")

        then:
        rendering.href == path
        rendering.text == "Test Text"

        and:
        (1..SourceCollector.PAGE_SOURCE_EXTENSIONS.size()) * printStream.println("WARNING: '$path' in $pageSourcePath refers to non-existent page")

        where:
        pageSourcePath | path
        "test.md"      | "not-exist.md"
        "test.md"      | "not-exist"
        "test.md"      | "./not-exist.md"
        "test.md"      | "./not-exist"
        "test.md"      | "/not-exist.md"
        "test.md"      | "/not-exist"
        "test.md"      | "../not-exist.md"
        "test.md"      | "../not-exist"
        "path/test.md" | "not-exist.md"
        "path/test.md" | "./not-exist.md"
        "path/test.md" | "/not-exist.md"
        "path/test.md" | "../not-exist.md"
        "test.md"      | "not-exist.md#fragment"
        "test.md"      | "/not-exist.md#fragment"
    }

    def "'render' should not output a warning message when a path is not Markdown"() {
        setup:
        def context = new PageBuildContext(documentSource: new DocumentSource())
        def pageSource = new PageSource(path: "test.md")
        def linkRenderer = new GaidenLinkRenderer(context, pageSource, pagesDirectory)

        and:
        def printStream = Mock(PrintStream)
        System.err = printStream

        when:
        def rendering = linkRenderer.render(path, "Test Title", "Test Text")

        then:
        rendering.href == path
        rendering.text == "Test Text"

        and:
        0 * printStream.println(_)

        where:
        pageSourcePath | path
        "test.md"      | "first.html"
        "test.md"      | "first.test"
        "test.md"      | "./first.html"
        "test.md"      | "./first.test"
        "test.md"      | "/first.html"
        "test.md"      | "/first.test"
        "path/test.md" | "../first.test"
        "path/test.md" | "/first.test"
        "path/test.md" | "second.test"
        "path/test.md" | "/path/second.test"
        "test.md"      | "first.html#fragment"
        "test.md"      | "first.test#fragment"
        "test.md"      | "/first.test#fragment"
    }

    def "'render' should not output a warning message when a path is URL"() {
        setup:
        def context = new PageBuildContext(documentSource: new DocumentSource())
        def pageSource = new PageSource(path: "test.md")
        def linkRenderer = new GaidenLinkRenderer(context, pageSource, pagesDirectory)

        and:
        def printStream = Mock(PrintStream)
        System.err = printStream

        when:
        def rendering = linkRenderer.render(path, "Test Title", "Test Text")

        then:
        rendering.href == path
        rendering.text == "Test Text"

        and:
        0 * printStream.println(_)

        where:
        path << [
            "http://www.example.com",
            "http://www.example.com/",
            "http://www.example.com/test",
            "http://www.example.com/test/",
            "http://www.example.com/test.html",
            "http://www.example.com/test.md",
            "https://www.example.com/test.md",
            "ftp://www.example.com/test.md",
            "file://www.example.com/test.md",
        ]
    }

    private void createFiles(String[] paths) {
        paths.each { createFile(it) }
    }

    private void createFile(String path) {
        def file = new File(pagesDirectory, path)
        if (!file.parentFile.exists()) {
            assert file.parentFile.mkdirs()
        }
        assert file.createNewFile()
    }
}
