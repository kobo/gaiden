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
import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode
import groovy.xml.MarkupBuilder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

/**
 * A builder of TOC.
 *
 * @author Hideki IGARASHI
 * @author Kazuki YAMAMOTO
 */
@Component
@CompileStatic
class TocBuilder {

    private static final String WHITESPACE = " "

    @Autowired
    TemplateEngine templateEngine

    @Autowired
    GaidenConfig gaidenConfig

    @Autowired
    MessageSource messageSource

    private List<TocNode> tocNodes = []

    /**
     * Builds a TOC from a TOC file.
     *
     * @return {@link Toc}'s instance
     */
    Toc build(BuildContext context) {
        if (!gaidenConfig.tocFile.exists()) {
            return null
        }

        def tocSourceNode = parseTocFile()
        def tocNode = toTocNodes(context, tocSourceNode.children())
        def tocContent = buildContent(context, tocNode)

        def binding = new BindingBuilder(gaidenConfig.title, gaidenConfig.tocOutputFilePath)
            .setContent(tocContent)
            .setOutputPath(gaidenConfig.tocOutputFilePath)
            .build()

        def content = templateEngine.make(binding)
        new Toc(path: gaidenConfig.tocOutputFilePath, content: content, tocNodes: tocNodes)
    }

    @CompileStatic(TypeCheckingMode.SKIP)
    private Node parseTocFile() {
        def tocBuilder = new NodeBuilder()
        tocBuilder.toc(new GroovyShell().evaluate("{ -> ${gaidenConfig.tocFile.getText(gaidenConfig.inputEncoding)} }"))
    }

    @CompileStatic(TypeCheckingMode.SKIP)
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

    @CompileStatic(TypeCheckingMode.SKIP)
    private String buildContent(BuildContext context, List<TocNode> tocNodes) {
        def writer = new StringWriter()
        def printer = new IndentPrinter(writer, WHITESPACE * 4)
        def builder = new MarkupBuilder(printer)

        builder.doubleQuotes = true // surrounds an attribute with double quotes

        builder.h1(gaidenConfig.tocTitle)

        buildContentOfToc(context, builder, tocNodes)

        writer.toString()
    }

    private String resolveOutputPath(BuildContext context, String path) {
        def pageReference = new PageReference(path)
        if (pageReference.extension != null && pageReference.extension != "md") {
            return path
        }

        def pageSource = context.documentSource.findPageSource(pageReference)
        if (!pageSource) {
            System.err.println("WARNING: " + messageSource.getMessage("toc.page.reference.not.exists.message", [path]))
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
