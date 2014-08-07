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

import groovy.transform.CompileStatic

import java.nio.file.Paths

import static org.apache.commons.lang3.StringEscapeUtils.*

/**
 * Builder for binding to be passed to a template engine.
 *
 * @author Kazuki YAMAMOTO
 */
@CompileStatic
class BindingBuilder {

    private GaidenConfig gaidenConfig
    private Page page
    private Document document
    private String content

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
            resource   : this.&getResourceMethod,
            prevPage   : prevPage,
            nextPage   : nextPage,
            documentToc: this.&getDocumentToc,
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

    private String getResourceMethod(String path) {
        def resourceFile = Paths.get(path)
        def src = page.source.outputPath
        def dest = gaidenConfig.outputDirectory.resolve(resourceFile.absolute ? Paths.get(resourceFile.toString().substring(1)) : resourceFile)
        src.parent.relativize(dest).toString()
    }

    private String getDocumentToc(args) {
        def params = args instanceof Map ? args : [:]
        def indent = ' ' * (params["indent"] as Integer ?: 2)
        def depth = params["depth"] as Integer ?: gaidenConfig.documentTocDepth

        StringBuilder sb = new StringBuilder()
        def currentLevel = 0
        document.toc.documentToc.each { TocNode tocNode ->
            if (tocNode.header.level > depth) {
                return
            }

            if (currentLevel < tocNode.header.level) {
                (tocNode.header.level - currentLevel).times {
                    sb << "${indent * currentLevel}<ul>\n"
                    currentLevel++
                }
            } else if (currentLevel > tocNode.header.level) {
                (currentLevel - tocNode.header.level).times {
                    currentLevel--
                    sb << "${indent * currentLevel}</ul>\n"
                }
            }

            def isFirstHeaderInPage = document.toc.getPageToc(tocNode.page).first() == tocNode
            def hash = isFirstHeaderInPage ? "" : "#${URLEncoder.encode(tocNode.header.title, gaidenConfig.outputEncoding)}"
            def number = gaidenConfig.numbering ? "<span class=\"number\">${tocNode.numbers.join(".")}.</span>" : ""
            sb << "${indent * currentLevel}<li><a href=\"${page.relativize(tocNode.page)}${hash}\">${number}${tocNode.header.title}</a></li>\n"
        }
        currentLevel.times {
            currentLevel--
            sb << "${indent * currentLevel}</ul>\n"
        }
        sb.toString()
    }
}
