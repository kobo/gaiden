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
import gaiden.markdown.GaidenMarkdownProcessor
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

/**
 * A markdown page builder builds from a markdown to a HTML.
 *
 * @author Hideki IGARASHI
 * @author Kazuki YAMAMOTO
 */
@Component
@CompileStatic
class PageBuilder {

    @Autowired
    GaidenMarkdownProcessor markdownProcessor

    @Autowired
    TemplateEngine templateEngine

    @Autowired
    GaidenConfig gaidenConfig

    @Autowired
    PageReferenceFactory pageReferenceFactory

    /**
     * Build from a page source to a page.
     *
     * @param context the context to be built
     * @param pageSource the page source to be built
     * @return {@link Page}'s instance
     */
    Page build(PageBuildContext context, PageSource pageSource) {
        buildPage(context, pageSource)
    }

    private Page buildPage(PageBuildContext context, PageSource pageSource) {
        def rootNode = markdownProcessor.parseMarkdown(pageSource)

        def headers = markdownProcessor.getHeaders(rootNode)
        def content = markdownProcessor.convertToHtml(rootNode, context, pageSource)

        def binding = new BindingBuilder(gaidenConfig, pageReferenceFactory)
            .setContent(content)
            .setPageBuildContext(context)
            .setPageSource(pageSource)
            .build()
        def pageContent = templateEngine.make(binding)

        new Page(source: pageSource, headers: headers, content: pageContent)
    }
}
