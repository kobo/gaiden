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

import gaiden.exception.GaidenException
import gaiden.markdown.GaidenMarkdownProcessor
import gaiden.message.MessageSource
import groovy.transform.CompileStatic

import java.nio.file.Files
import java.nio.file.Paths

import static org.apache.commons.lang3.StringEscapeUtils.*

/**
 * Builder for binding to be passed to a template engine.
 *
 * @author Kazuki YAMAMOTO
 */
@CompileStatic
class BindingBuilder {

    private MessageSource messageSource
    private GaidenMarkdownProcessor markdownProcessor
    private GaidenConfig gaidenConfig
    private Page page
    private Document document
    private String content

    BindingBuilder setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource
        return this
    }

    BindingBuilder setMarkdownProcessor(GaidenMarkdownProcessor markdownProcessor) {
        this.markdownProcessor = markdownProcessor
        return this
    }

    BindingBuilder setGaidenConfig(GaidenConfig gaidenConfig) {
        this.gaidenConfig = gaidenConfig
        return this
    }

    BindingBuilder setPage(Page page) {
        this.page = page
        return this
    }

    BindingBuilder setDocument(Document document) {
        this.document = document
        return this
    }

    BindingBuilder setContent(String content) {
        this.content = content
        return this
    }

    /**
     * Builds binding.
     *
     * @return binding
     */
    Map<String, Object> build() {
        [
            title      : escapeHtml4(gaidenConfig.title),
            content    : content,
            metadata   : page.metadata,
            resource   : this.&getResource,
            homePage   : homePage,
            prevPage   : prevPage,
            nextPage   : nextPage,
            documentToc: this.&getDocumentToc,
            pageToc    : this.&getPageToc,
            render     : this.&render,
            config     : gaidenConfig,
        ]
    }

    private Map getHomePage() {
        if (!document.homePage) {
            return Collections.emptyMap()
        }

        return toMap(document.homePage)
    }

    private Map getPrevPage() {
        def previousPage = document.previousPageOf(page)
        if (!previousPage) {
            return Collections.emptyMap()
        }

        return toMap(previousPage)
    }

    private Map getNextPage() {
        def nextPage = document.nextPageOf(page)
        if (!nextPage) {
            return Collections.emptyMap()
        }

        return toMap(nextPage)
    }

    private Map toMap(Page destPage) {
        return [
            path : page.relativize(destPage),
            title: escapeHtml4(destPage.title),
        ]
    }

    private String getResource(String path) {
        def resourceFile = Paths.get(path)
        def src = page.outputPath
        def dest = gaidenConfig.outputDirectory.resolve(resourceFile.absolute ? Paths.get(resourceFile.toString().substring(1)) : resourceFile)
        src.parent.relativize(dest).toString()
    }

    private String getDocumentToc(args) {
        def params = args instanceof Map ? args : [:]
        def maxDepth = params["depth"] as Integer ?: gaidenConfig.documentTocDepth

        List<Integer> levels = []
        StringBuilder sb = new StringBuilder()

        document.pageOrder.each { Page destPage ->
            def maxDepthOfPage = destPage.metadata.documentTocDepth as Integer ?: maxDepth
            destPage.headers.eachWithIndex { Header header, int index ->
                if (header.level > maxDepthOfPage) {
                    return
                }

                if (!levels || levels.last() < header.level) {
                    sb << "<ul>"
                    levels << header.level
                } else if (levels.last() > header.level) {
                    if (!levels.contains(header.level)) {
                        throw new GaidenException("illegal.header.level", [gaidenConfig.sourceDirectory.relativize(destPage.source.path), header.title, header.level, levels.join(",")])
                    }

                    sb << "</li>"
                    while (levels.last() != header.level) {
                        sb << "</ul></li>"
                        levels.pop()
                    }
                } else {
                    sb << "</li>"
                }

                def isFirstOfPage = index == 0
                def mainHref = page.relativize(destPage) + (isFirstOfPage ? "" : "#${header.hash}")
                def altHash = isFirstOfPage && !header.hash.empty ? " data-alt-hash=\"${header.hash}\"" : ""
                def number = header.numbers ? "<span class=\"number\">${header.number}</span>" : ""
                def cssClass = header.numbers ? "numbered" : "unnumbered"
                sb << "<li class=\"${cssClass}\"><a href=\"${mainHref}\"${altHash}>${number}${header.title}</a>"
            }
        }
        levels.size().times {
            sb << "</li></ul>"
        }
        sb.toString()
    }

    private String getPageToc(args) {
        if (!page.headers) {
            return ""
        }

        def params = args instanceof Map ? args : [:]
        def maxDepth = page.metadata.pageTocDepth as Integer ?: params["depth"] as Integer ?: gaidenConfig.pageTocDepth

        List<Integer> levels = []
        StringBuilder sb = new StringBuilder()

        page.headers.eachWithIndex { Header header, int index ->
            if (header.level > maxDepth) {
                return
            }

            if (!levels || levels.last() < header.level) {
                sb << "<ul>"
                levels << header.level
            } else if (levels.last() > header.level) {
                if (!levels.contains(header.level)) {
                    throw new GaidenException("illegal.header.level", [gaidenConfig.sourceDirectory.relativize(page.source.path), header.title, header.level, levels.join(",")])
                }

                sb << "</li>"
                while (levels.last() != header.level) {
                    sb << "</ul></li>"
                    levels.pop()
                }
            } else {
                sb << "</li>"
            }

            def hash = "#${header.hash}"
            def number = header.numbers ? "<span class=\"number\">${header.number}</span>" : ""
            def cssClass = header.numbers ? "numbered" : "unnumbered"
            sb << "<li class=\"${cssClass}\"><a href=\"${hash}\">${number}${header.title}</a>"
        }
        levels.size().times {
            sb << "</li></ul>"
        }

        sb.toString()
    }

    private String render(String filePath) {
        def file = gaidenConfig.sourceDirectory.resolve(filePath)
        def page = document.pages.find { Files.isSameFile(it.source.path, file) }
        if (!page) {
            System.err.println("WARNING: " + messageSource.getMessage("output.page.reference.not.exists.message", [filePath, gaidenConfig.getLayoutFile(page.metadata.layout as String)]))
            return ""
        }
        markdownProcessor.convertToHtml(page, document)
    }
}
