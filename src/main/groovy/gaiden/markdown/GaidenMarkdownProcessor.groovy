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

import gaiden.GaidenConfig
import gaiden.PageReferenceFactory
import gaiden.PageSource
import gaiden.context.PageBuildContext
import gaiden.message.MessageSource
import groovy.transform.CompileStatic
import org.pegdown.Extensions
import org.pegdown.ParsingTimeoutException
import org.pegdown.PegDownProcessor
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

    @Autowired
    PageReferenceFactory pageReferenceFactory

    GaidenMarkdownProcessor() {
        super(Extensions.ALL - Extensions.HARDWRAPS)
    }

    /**
     * Converts the given markdown source to HTML.
     *
     * @param context the context to be built
     * @param pageSource the page source to convert
     * @return the HTML
     * @throws ParsingTimeoutException if the input cannot be parsed within the configured parsing timeout
     */
    String markdownToHtml(PageBuildContext context, PageSource pageSource) throws ParsingTimeoutException {
        def astRoot = parseMarkdown(pageSource.content.toCharArray())

        def linkRenderer = new GaidenLinkRenderer(context, pageSource, gaidenConfig.pagesDirectory, messageSource, pageReferenceFactory)
        def imageRenderer = new ImageRenderer(pageSource, gaidenConfig.staticDirectory, gaidenConfig.outputDirectory, messageSource)
        new GaidenToHtmlSerializer(linkRenderer, imageRenderer).toHtml(astRoot)
    }
}
