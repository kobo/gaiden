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
import gaiden.util.PathUtils
import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode
import groovy.xml.MarkupBuilder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

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

    @Autowired
    PageReferenceFactory pageReferenceFactory

    private List<TocNode> tocNodes = []

    /**
     * Builds a TOC from a TOC file.
     *
     * @return {@link Toc}'s instance
     */
    Toc build(BuildContext context) {
        if (Files.notExists(gaidenConfig.tocFile)) {
            return null
        }

        def tocSourceNode = parseTocFile()
        def tocNode = toTocNodes(context, tocSourceNode.children())
        def tocContent = buildContent(context, tocNode)

        def binding = new BindingBuilder(gaidenConfig.title, gaidenConfig.outputDirectory, gaidenConfig.tocOutputFile, pageReferenceFactory)
            .setContent(tocContent)
            .setOutputPath(gaidenConfig.tocOutputFile)
            .build()

        def content = templateEngine.make(binding)
        new Toc(path: gaidenConfig.tocOutputFile, content: content, tocNodes: tocNodes)
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

    private String resolveOutputPath(BuildContext context, Path path) {
        def pageReference = pageReferenceFactory.get(path)
        def pageSource = context.documentSource.findPageSource(pageReference)
        if (!pageSource) {
            if (!PathUtils.getExtension(pageReference.path) || PathUtils.getExtension(pageReference.path) in SourceCollector.PAGE_SOURCE_EXTENSIONS) {
                System.err.println("WARNING: " + messageSource.getMessage("toc.page.reference.not.exists.message", [path]))
                return null
            }
            return path.toString()
        }
        return gaidenConfig.tocOutputFile.parent.relativize(pageSource.outputPath).toString() + pageReference.hash
    }

    private List<TocNode> toTocNodes(BuildContext context, List<Node> nodes) {
        nodes.collect { Node node ->
            def tocNode = new TocNode()
            tocNode.path = Paths.get(node.name() as String)
            tocNode.title = node.attributes().title
            tocNode.pageReference = pageReferenceFactory.get(tocNode.path)
            tocNode.pageSource = context.documentSource.findPageSource(tocNode.pageReference)

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
