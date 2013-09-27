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
import groovy.xml.MarkupBuilder

/**
 * A builder of TOC.
 *
 * @author Hideki IGARASHI
 * @author Kazuki YAMAMOTO
 */
class TocBuilder {

    private static final String WHITESPACE = " "

    private TemplateEngine templateEngine
    private File tocFile
    private String tocOutputPath
    private String tocTitle
    private String inputEncoding

    TocBuilder(TemplateEngine templateEngine) {
        this(templateEngine, Holders.config.tocPathFile, Holders.config.tocOutputPath, Holders.config.tocTitle, Holders.config.inputEncoding)
    }

    TocBuilder(TemplateEngine templateEngine, File tocFile, String tocOutputPath, String tocTitle, String inputEncoding) {
        this.templateEngine = templateEngine
        this.tocFile = tocFile
        this.tocOutputPath = tocOutputPath
        this.tocTitle = tocTitle
        this.inputEncoding = inputEncoding
    }

    /**
     * Builds a TOC from a TOC file.
     *
     * @return {@link Toc}'s instance
     */
    Toc build(List<Page> pages) {
        if (!tocFile.exists()) {
            return new NullToc()
        }

        Node tocNode = parseTocFile()
        def content = templateEngine.make(content: buildContent(tocNode, pages), outputPath: tocOutputPath)

        new Toc(path: tocOutputPath, content: content, node: tocNode)
    }

    private Node parseTocFile() {
        def tocBuilder = new NodeBuilder()
        tocBuilder.toc(new GroovyShell().evaluate("{ -> ${tocFile.getText(inputEncoding)} }"))
    }

    private void buildContentOfToc(builder, nodes, List<Page> pages) {
        if (!nodes) {
            return
        }

        builder.ul {
            nodes.each { Node node ->
                if (node.name().startsWith("#")) {
                    li(node.attributes().title) {
                        buildContentOfToc(builder, node.value(), pages)
                    }
                } else {
                    def outputPath = resolveOutputPath(pages, node.name() as String)
                    if (outputPath) {
                        li {
                            a(href: outputPath, node.attributes().title)
                            buildContentOfToc(builder, node.value(), pages)
                        }
                    } else {
                        li(node.attributes().title) {
                            buildContentOfToc(builder, node.value(), pages)
                        }
                    }
                }
            }
        }
    }

    private String buildContent(Node tocNode, List<Page> pages) {
        def writer = new StringWriter()
        def printer = new IndentPrinter(writer, WHITESPACE * 4)
        def builder = new MarkupBuilder(printer)

        builder.doubleQuotes = true // surrounds an attribute with double quotes

        builder.h1(tocTitle)

        buildContentOfToc(builder, tocNode.value(), pages)

        writer.toString()
    }

    private static String resolveOutputPath(List<Page> pages, String abstractPath) {
        def hasFragment = abstractPath.contains("#")
        def targetPath = hasFragment ? abstractPath.substring(0, abstractPath.lastIndexOf("#")) : abstractPath
        def fragment = hasFragment ? abstractPath.substring(abstractPath.lastIndexOf("#")) : ""

        def extension = FileUtils.getExtension(new File(targetPath).name)
        if (extension != null && extension != "md") {
            return abstractPath
        }

        def page = pages.find { Page page ->
            targetPath ==~ /${FileUtils.removeExtension(page.originalPath)}(\.md)?/
        }
        if (!page) {
            // TODO resolve from messageSource
            System.err.println("WARNING: '${abstractPath}' in the Table of Contents refers to non-existent page")
            return null
        }
        return page.path + fragment
    }

}
