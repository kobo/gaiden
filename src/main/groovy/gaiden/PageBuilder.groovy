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

import gaiden.util.FileUtils
import org.pegdown.Extensions
import org.pegdown.PegDownProcessor

/**
 * A markdown page builder builds from a markdown to a HTML.
 *
 * @author Hideki IGARASHI
 * @author Kazuki YAMAMOTO
 */
class PageBuilder {

    private static final String OUTPUT_EXTENSION = "html"

    private TemplateEngine templateEngine
    private PegDownProcessor pegDownProcessor

    PageBuilder(TemplateEngine templateEngine) {
        this(templateEngine, new PegDownProcessor(Extensions.ALL - Extensions.HARDWRAPS))
    }

    PageBuilder(TemplateEngine templateEngine, PegDownProcessor pegDownProcessor) {
        this.templateEngine = templateEngine
        this.pegDownProcessor = pegDownProcessor
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
            path: outputPath,
            content: buildPage(pageSource, outputPath),
        )
    }

    private String buildPage(PageSource pageSource, String outputPath) {
        def content = pegDownProcessor.markdownToHtml(pageSource.content)
        templateEngine.make(content: content, outputPath: outputPath)
    }

}
