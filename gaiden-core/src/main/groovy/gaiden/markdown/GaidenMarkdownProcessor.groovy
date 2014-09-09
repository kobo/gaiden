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
import gaiden.Filter
import gaiden.GaidenConfig
import gaiden.Header
import gaiden.Page
import gaiden.PageReference
import gaiden.PageSource
import gaiden.message.MessageSource
import groovy.transform.CompileStatic
import org.parboiled.Parboiled
import org.pegdown.GaidenParser
import org.pegdown.Parser
import org.pegdown.ParsingTimeoutException
import org.pegdown.PegDownProcessor
import org.pegdown.ast.RootNode
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import java.util.Map.Entry

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
        super(Parboiled.createParser(GaidenParser) as Parser)
    }

    RootNode parseMarkdown(PageSource pageSource) {
        def content = gaidenConfig.filters.inject(pageSource.content) { String text, Entry<String, Filter> entry ->
            entry.value.doBefore(text)
        } as String

        return parseMarkdown(content.toCharArray())
    }

    String convertToHtml(Page page, Document document) throws ParsingTimeoutException {
        def linkRenderer = new GaidenLinkRenderer(page: page, document: document, messageSource: messageSource)
        def imageRenderer = new ImageRenderer(page, gaidenConfig.outputDirectory, messageSource)
        def html = new GaidenToHtmlSerializer(gaidenConfig, linkRenderer, imageRenderer, page).toHtml(page.contentNode)

        return gaidenConfig.filters.inject(html) { String text, Entry<String, Filter> entry ->
            entry.value.doAfter(text)
        } as String
    }

    static List<Header> getHeaders(RootNode rootNode, PageReference pageReference) {
        new HeaderParser(rootNode, pageReference).headers
    }
}
