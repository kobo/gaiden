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
    private GaidenConfig gaidenConfig
    private Page page
    private Document document
    private String content

    BindingBuilder setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource
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
            resource   : this.&getResource,
            prevPage   : prevPage,
            nextPage   : nextPage,
            documentToc: this.&getDocumentToc,
            render     : this.&render,
        ]
    }

    private Map getPrevPage() {
        def previousPage = document.previousPageOf(page)
        if (!previousPage) {
            return Collections.emptyMap()
        }

        return [
            path : page.relativize(previousPage),
            title: escapeHtml4(previousPage.headers?.first()?.title),
        ]
    }

    private Map getNextPage() {
        def nextPage = document.nextPageOf(page)
        if (!nextPage) {
            return Collections.emptyMap()
        }

        return [
            path : page.relativize(nextPage),
            title: escapeHtml4(nextPage.headers?.first()?.title),
        ]
    }

    private String getResource(String path) {
        def resourceFile = Paths.get(path)
        def src = page.source.outputPath
        def dest = gaidenConfig.outputDirectory.resolve(resourceFile.absolute ? Paths.get(resourceFile.toString().substring(1)) : resourceFile)
        src.parent.relativize(dest).toString()
    }

    private String getDocumentToc(args) {
        def params = args instanceof Map ? args : [:]
        def depth = params["depth"] as Integer ?: gaidenConfig.documentTocDepth

        StringBuilder sb = new StringBuilder()
        def currentLevel = 0
        List<TocNode> tocNodes = document.toc.documentToc
        tocNodes.each { TocNode tocNode ->
            if (tocNode.header.level > depth) {
                return
            }

            if (currentLevel < tocNode.header.level) {
                sb << "<ul>"
                currentLevel++
                (tocNode.header.level - currentLevel).times {
                    sb << "<li><ul>"
                    currentLevel++
                }
            } else if (currentLevel > tocNode.header.level) {
                sb << "</li>"
                (currentLevel - tocNode.header.level).times {
                    currentLevel--
                    sb << "</ul></li>"
                }
            } else {
                sb << "</li>"
            }

            def isFirstHeaderInPage = document.toc.getPageToc(tocNode.page).first() == tocNode
            def hash = isFirstHeaderInPage ? "" : "#${URLEncoder.encode(tocNode.header.title, gaidenConfig.outputEncoding)}"
            def number = gaidenConfig.numbering ? "<span class=\"number\">${tocNode.numbers.join(".")}.</span>" : ""
            sb << "<li><a href=\"${page.relativize(tocNode.page)}${hash}\">${number}${tocNode.header.title}</a>"
        }
        currentLevel.times {
            sb << "</li></ul>"
        }
        sb.toString()
    }

    private String render(String filePath) {
        def file = gaidenConfig.pagesDirectory.resolve(filePath)
        if (Files.notExists(file)) {
            System.err.println("WARNING: " + messageSource.getMessage("output.page.reference.not.exists.message", [filePath, gaidenConfig.templateFile]))
            return ""
        }

        def processor = new GaidenMarkdownProcessor()
        processor.markdownToHtml(file.text)
    }
}
