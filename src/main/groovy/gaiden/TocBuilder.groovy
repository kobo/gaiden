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

    TocBuilder(TemplateEngine templateEngine) {
        this(templateEngine, Holders.config.tocPathFile, Holders.config.tocOutputPath)
    }

    TocBuilder(TemplateEngine templateEngine, File tocFile, String tocOutputPath, String tocTitle) {
        this.templateEngine = templateEngine
        this.tocFile = tocFile
        this.tocOutputPath = tocOutputPath
        this.tocTitle = tocTitle
    }

    /**
     * Builds a TOC from a TOC file.
     *
     * @return {@link Toc}'s instance
     */
    Toc build() {
        if (!tocFile.exists()) {
            return new NullToc()
        }

        Node tocNode = parseTocFile()
        def content = templateEngine.make(content: buildContent(tocNode), outputPath: tocOutputPath)

        new Toc(path: tocOutputPath, content: content, node: tocNode)
    }

    private Node parseTocFile() {
        def tocBuilder = new NodeBuilder()
        tocBuilder.toc(new GroovyShell().evaluate("{ -> ${tocFile.text} }"))
    }

    private void buildContentOfToc(builder, nodes) {
        builder.ul {
            nodes.each { Node node ->
                li {
                    a(href: node.name(), node.attributes().title)
                    if (node.value()) {
                        buildContentOfToc(builder, node.value())
                    }
                }
            }
        }
    }

    private String buildContent(Node tocNode) {
        def writer = new StringWriter()
        def printer = new IndentPrinter(writer, WHITESPACE * 4)
        def builder = new MarkupBuilder(printer)

        builder.doubleQuotes = true // surrounds an attribute with double quotes

        builder.h1(tocTitle)

        buildContentOfToc(builder, tocNode.value())

        writer.toString()
    }

}
