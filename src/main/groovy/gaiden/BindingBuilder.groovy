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
import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode
import groovy.xml.MarkupBuilder

import java.nio.file.Paths

import static org.apache.commons.lang3.StringEscapeUtils.*

/**
 * Builder for binding to be passed to a template engine.
 *
 * @author Kazuki YAMAMOTO
 */
@CompileStatic
class BindingBuilder {

    private static final String EMPTY_STRING = ""

    private GaidenConfig gaidenConfig
    private PageReferenceFactory pageReferenceFactory

    private String content
    private PageSource pageSource
    private PageBuildContext pageBuildContext

    BindingBuilder(GaidenConfig gaidenConfig, PageReferenceFactory pageReferenceFactory) {
        this.gaidenConfig = gaidenConfig
        this.pageReferenceFactory = pageReferenceFactory
    }

    /**
     * Builds binding.
     *
     * @return binding
     */
    Map<String, Object> build() {
        [
            title   : escapeHtml4(gaidenConfig.title),
            content : content,
            tocPath : tocPath,
            resource: resourceMethod,
            prevPage: prevPage,
            nextPage: nextPage,
            toc     : toc,
        ]
    }

    /**
     * Sets the content.
     */
    BindingBuilder setContent(String content) {
        this.content = content
        this
    }

    BindingBuilder setPageSource(PageSource pageSource) {
        this.pageSource = pageSource
        this
    }

    /**
     * Sets the page build context.
     */
    BindingBuilder setPageBuildContext(PageBuildContext pageBuildContext) {
        this.pageBuildContext = pageBuildContext
        this
    }

    private Map getPrevPage() {
        if (!pageSource.path || !pageBuildContext) {
            return Collections.emptyMap()
        }

        def previousTocNode = pageBuildContext.toc.findTocNode(pageSource.path)?.previous
        if (!previousTocNode) {
            return Collections.emptyMap()
        }

        if (!previousTocNode.pageSource) {
            return [
                path : EMPTY_STRING,
                title: escapeHtml4(previousTocNode.title),
            ]
        }

        def relativePath = pageSource.outputPath.parent.relativize(previousTocNode.pageSource.outputPath)
        return [
            path : relativePath.toString() + previousTocNode.pageReference.hash,
            title: escapeHtml4(previousTocNode.title)
        ]
    }

    private Map getNextPage() {
        if (!pageSource.path || !pageBuildContext) {
            return Collections.emptyMap()
        }

        def nextTocNode = pageBuildContext.toc.findTocNode(pageSource.path)?.next
        if (!nextTocNode) {
            return Collections.emptyMap()
        }

        if (!nextTocNode.pageSource) {
            return [
                path : EMPTY_STRING,
                title: escapeHtml4(nextTocNode.title),
            ]
        }

        def relativePath = pageSource.outputPath.parent.relativize(nextTocNode.pageSource.outputPath)
        return [
            path : relativePath.toString() + nextTocNode.pageReference.hash,
            title: escapeHtml4(nextTocNode.title)
        ]
    }

    private Closure getResourceMethod() {
        return { String resourceFilePath ->
            def resourceFile = Paths.get(resourceFilePath)
            if (!resourceFile.absolute) {
                return resourceFilePath
            }

            def src = gaidenConfig.outputDirectory.resolve(pageSource.outputPath)
            def dest = gaidenConfig.outputDirectory.resolve(resourceFile.toString().substring(1))

            return src.parent.relativize(dest).toString()
        }
    }

    private String getTocPath() {
        pageSource.outputPath.parent.relativize(gaidenConfig.tocOutputFile).toString()
    }

    private String getToc() {
        if (!pageBuildContext) {
            return EMPTY_STRING
        }
        buildTocContent()
    }

    @CompileStatic(TypeCheckingMode.SKIP)
    private String buildTocContent() {
        def writer = new StringWriter()
        def printer = new IndentPrinter(writer, " " * 4)
        def builder = new MarkupBuilder(printer)

        builder.doubleQuotes = true // surrounds an attribute with double quotes

        buildTocNodes(builder, pageBuildContext.toc.tocNodes.findAll { !it.parent })

        writer.toString()
    }

    private String resolveOutputPath(PageReference pageReference) {
        def destPageSource = pageBuildContext.documentSource.findPageSource(pageReference)
        if (!destPageSource) {
            return null
        }
        return pageSource.outputPath.parent.relativize(destPageSource.outputPath).toString() + pageReference.hash
    }

    @CompileStatic(TypeCheckingMode.SKIP)
    private void buildTocNodes(MarkupBuilder builder, List<TocNode> tocNodes) {
        if (!tocNodes) {
            return
        }

        builder.ul {
            tocNodes.each { TocNode tocNode ->
                if (tocNode.path.startsWith("#")) {
                    li(tocNode.title) {
                        buildTocNodes(builder, tocNode.children)
                    }
                } else {
                    def outputPath = resolveOutputPath(tocNode.pageReference)
                    if (outputPath) {
                        li("class": pageSource == tocNode.pageSource ? "current" : "") {
                            a(href: outputPath, tocNode.title)
                            buildTocNodes(builder, tocNode.children)
                        }
                    } else {
                        li(tocNode.title) {
                            buildTocNodes(builder, tocNode.children)
                        }
                    }
                }
            }
        }
    }
}
