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

import gaiden.util.FileUtils
import groovy.io.FileType
import spock.lang.Specification

class DocumentWriterSpec extends Specification {

    def outputDirectory = new File("build/gaiden-test-doc")

    def setup() {
        outputDirectory.deleteDir()
    }

    def "'write' should write a document to files"() {
        setup:
        def page1 = createPage("document1.md")
        def page2 = createPage("document2.md")
        def page3 = createPage("sub/document3.md")

        and:
        def toc = new Toc(path: "toc.html", content: "<h1>table of contents</h1>")

        and:
        def document = new Document(pages: [page1, page2, page3], toc: toc)
        def documentWriter = new DocumentWriter(new File("src/test/resources/static-files"), outputDirectory, "UTF-8")

        and:
        def saved = System.out
        def printStream = Mock(PrintStream)
        System.out = printStream

        when:
        documentWriter.write(document)

        then:
        getFiles(outputDirectory) == [
            "document1.html",
            "document2.html",
            "sub/document3.html",
            "images/dummy.png",
            "css/main.css",
            "js/test.js",
            "toc.html",
        ] as Set

        and:
        new File("build/gaiden-test-doc/document1.html").text == page1.content
        new File("build/gaiden-test-doc/document2.html").text == page2.content
        new File("build/gaiden-test-doc/sub/document3.html").text == page3.content

        and:
        new File("build/gaiden-test-doc/toc.html").text == toc.content

        and:
        1 * printStream.println("Built document at ${outputDirectory.canonicalPath}")

        cleanup:
        System.out = saved
    }

    def "'write' should overwrite when file already exists"() {
        setup:
        def page = createPage("document1.md")
        def toc = new Toc(path: "toc.html", content: "<h1>table of contents</h1>")
        def document = new Document(pages: [page], toc: toc)

        and:
        def documentWriter = new DocumentWriter(new File("src/test/resources/static-files"), outputDirectory, "UTF-8")
        documentWriter.write(document)

        when:
        documentWriter.write(document)

        then:
        new File("build/gaiden-test-doc/document1.html").text == page.content
        new File("build/gaiden-test-doc/toc.html").text == toc.content
    }

    def "'write' should write a specified encoding"() {
        setup:
        def page = createPage("document.md", "これはShift_JISのドキュメントです。")

        and:
        def toc = new Toc(path: "toc.html", content: "<h1>これはShift_JISのTOCです</h1>")

        and:
        def document = new Document(pages: [page], toc: toc)
        def documentWriter = new DocumentWriter(new File("src/test/resources/static-files"), outputDirectory, "Shift_JIS")

        when:
        documentWriter.write(document)

        then:
        getFiles(outputDirectory) == [
            "document.html",
            "images/dummy.png",
            "css/main.css",
            "js/test.js",
            "toc.html",
        ] as Set

        and:
        new File("build/gaiden-test-doc/document.html").getText("Shift_JIS") == page.content

        and:
        new File("build/gaiden-test-doc/toc.html").getText("Shift_JIS") == toc.content
    }

    def "'write' should not write anything if toc file doesn't exist"() {
        setup:
        def document = new Document(pages: [], toc: null)
        def documentWriter = new DocumentWriter(new File("src/test/resources/static-files"), outputDirectory, "UTF-8")

        when:
        documentWriter.write(document)

        then:
        getFiles(outputDirectory) == [
            "images/dummy.png",
            "css/main.css",
            "js/test.js",
        ] as Set
    }

    private Page createPage(String path, String content = null) {
        def source = new PageSource(path: path)
        new Page(source: source, content: content ?: "<title>Document</title><p>${source.outputPath} content</p>")
    }

    private Set getFiles(File directory) {
        def files = []
        directory.eachFileRecurse(FileType.FILES) {
            files << FileUtils.getRelativePathForDirectoryToFile(directory, it)
        }
        files
    }

}
