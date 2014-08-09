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

package gaiden.markdown

import gaiden.Document
import gaiden.GaidenConfig
import gaiden.Header
import gaiden.Page
import gaiden.PageSource
import gaiden.message.MessageSource
import groovy.transform.CompileStatic
import org.pegdown.Extensions
import org.pegdown.ParsingTimeoutException
import org.pegdown.PegDownProcessor
import org.pegdown.ast.RootNode
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

/**
 * A Processor for Markdown.
 *
 * @author Hideki IGARASHI
 * @author Kazuki YAMAMOTO
 */
@Component
@CompileStatic
class GaidenMarkdownProcessor extends PegDownProcessor {

    @Autowired
    GaidenConfig gaidenConfig

    @Autowired
    MessageSource messageSource

    GaidenMarkdownProcessor() {
        super(Extensions.ALL - Extensions.HARDWRAPS)
    }

    RootNode parseMarkdown(PageSource pageSource) {
        parseMarkdown(pageSource.content.toCharArray())
    }

    List<Header> getHeaders(RootNode rootNode) {
        new HeaderParser(rootNode).headers
    }

    /**
     * FIXME:
     * Converts the given markdown nodes to HTML.
     *
     * @param context the context to be built
     * @param pageSource the page source to convert
     * @return the HTML
     * @throws ParsingTimeoutException if the input cannot be parsed within the configured parsing timeout
     */
    String convertToHtml(Page page, Document document) throws ParsingTimeoutException {
        def linkRenderer = new GaidenLinkRenderer(page: page, document: document, messageSource: messageSource)
        def imageRenderer = new ImageRenderer(page, gaidenConfig.outputDirectory, messageSource)
        new GaidenToHtmlSerializer(gaidenConfig, linkRenderer, imageRenderer, page).toHtml(page.contentNode)
    }
}
