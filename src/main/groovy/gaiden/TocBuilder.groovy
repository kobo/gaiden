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
    private String tocOutputFilePath
    private String tocTitle
    private String inputEncoding

    private List<TocNode> tocNodes = []

    TocBuilder(TemplateEngine templateEngine,
               File tocFile = Holders.config.tocFile,
               String tocOutputFilePath = Holders.config.tocOutputFilePath,
               String tocTitle = Holders.config.tocTitle,
               String inputEncoding = Holders.config.inputEncoding) {
        this.templateEngine = templateEngine
        this.tocFile = tocFile
        this.tocOutputFilePath = tocOutputFilePath
        this.tocTitle = tocTitle
        this.inputEncoding = inputEncoding
    }

    /**
     * Builds a TOC from a TOC file.
     *
     * @return {@link Toc}'s instance
     */
    Toc build(BuildContext context) {
        if (!tocFile.exists()) {
            return null
        }

        def tocSourceNode = parseTocFile()
        def tocNode = toTocNodes(context, tocSourceNode.children())
        def tocContent = buildContent(context, tocNode)

        def binding = new BindingBuilder()
            .setContent(tocContent)
            .setOutputPath(tocOutputFilePath)
            .build()

        def content = templateEngine.make(binding)
        new Toc(path: tocOutputFilePath, content: content, tocNodes: tocNodes)
    }

    private Node parseTocFile() {
        def tocBuilder = new NodeBuilder()
        tocBuilder.toc(new GroovyShell().evaluate("{ -> ${tocFile.getText(inputEncoding)} }"))
    }

    private void buildContentOfToc(BuildContext context, MarkupBuilder builder, List<TocNode> tocNodes) {
        if (!tocNodes) {
            return
        }

        builder.ul {
            tocNodes.each { TocNode tocNode ->
                if (tocNode.path.startsWith("#")) {
                    li(tocNode.title) {
                        buildContentOfToc(context, builder, tocNode.children)
                    }
                } else {
                    def outputPath = resolveOutputPath(context, tocNode.path)
                    if (outputPath) {
                        li {
                            a(href: outputPath, tocNode.title)
                            buildContentOfToc(context, builder, tocNode.children)
                        }
                    } else {
                        li(tocNode.title) {
                            buildContentOfToc(context, builder, tocNode.children)
                        }
                    }
                }
            }
        }
    }

    private String buildContent(BuildContext context, List<TocNode> tocNodes) {
        def writer = new StringWriter()
        def printer = new IndentPrinter(writer, WHITESPACE * 4)
        def builder = new MarkupBuilder(printer)

        builder.doubleQuotes = true // surrounds an attribute with double quotes

        builder.h1(tocTitle)

        buildContentOfToc(context, builder, tocNodes)

        writer.toString()
    }

    private static String resolveOutputPath(BuildContext context, String path) {
        def pageReference = new PageReference(path)
        if (pageReference.extension != null && pageReference.extension != "md") {
            return path
        }

        def pageSource = context.documentSource.findPageSource(pageReference)
        if (!pageSource) {
            System.err.println("WARNING: " + Holders.getMessage("toc.page.reference.not.exists.message", [path]))
            return null
        }
        return pageSource.outputPath + pageReference.fragment
    }

    private List<TocNode> toTocNodes(BuildContext context, List<Node> nodes) {
        nodes.collect { Node node ->
            def tocNode = new TocNode()
            tocNode.path = node.name()
            tocNode.title = node.attributes().title
            tocNode.pageSource = context.documentSource.findPageSource(new PageReference(tocNode.path))

            if (!tocNodes.empty) {
                def previous = tocNodes.last()
                tocNode.previous = previous
                previous.next = tocNode
            }
            tocNodes << tocNode

            def children = toTocNodes(context, node.children())
            children.each { TocNode child ->
                child.parent = tocNode
            }
            tocNode.children = children
            tocNode
        }
    }
}
