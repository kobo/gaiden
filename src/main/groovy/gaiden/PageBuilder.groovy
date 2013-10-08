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
import org.pegdown.Extensions

/**
 * A markdown page builder builds from a markdown to a HTML.
 *
 * @author Hideki IGARASHI
 * @author Kazuki YAMAMOTO
 */
class PageBuilder {

    private TemplateEngine templateEngine
    private GaidenMarkdownProcessor markdownProcessor

    PageBuilder(TemplateEngine templateEngine) {
        this(templateEngine, new GaidenMarkdownProcessor(Extensions.ALL - Extensions.HARDWRAPS))
    }

    PageBuilder(TemplateEngine templateEngine, GaidenMarkdownProcessor markdownProcessor) {
        this.templateEngine = templateEngine
        this.markdownProcessor = markdownProcessor
    }

    /**
     * Build from a page source to a page.
     *
     * @param context the context to be built
     * @return {@link Page}'s instance
     */
    Page build(PageBuildContext context, PageSource pageSource) {
        new Page(
            source: pageSource,
            content: buildPage(context, pageSource),
        )
    }

    private String buildPage(PageBuildContext context, PageSource pageSource) {
        def content = markdownProcessor.markdownToHtml(pageSource)
        templateEngine.make(content: content, outputPath: pageSource.outputPagePath)
    }

}
