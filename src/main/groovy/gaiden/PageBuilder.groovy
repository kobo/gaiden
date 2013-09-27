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
import gaiden.util.FileUtils
import org.pegdown.Extensions

/**
 * A markdown page builder builds from a markdown to a HTML.
 *
 * @author Hideki IGARASHI
 * @author Kazuki YAMAMOTO
 */
class PageBuilder {

    private static final String OUTPUT_EXTENSION = "html"

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
     * @param pageSource the page source to be built
     * @return {@link Page}'s instance
     */
    Page build(PageSource pageSource) {
        def outputPath = FileUtils.replaceExtension(pageSource.path, OUTPUT_EXTENSION)
        new Page(
            originalPath: pageSource.path,
            path: outputPath,
            content: buildPage(pageSource, outputPath),
        )
    }

    private String buildPage(PageSource pageSource, String outputPath) {
        def content = markdownProcessor.markdownToHtml(pageSource.content, outputPath)
        templateEngine.make(content: content, outputPath: outputPath)
    }

}
