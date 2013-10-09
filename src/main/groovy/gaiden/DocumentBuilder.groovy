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

import gaiden.context.BuildContext
import gaiden.context.PageBuildContext

/**
 * A document builder builds from a document source to a document.
 *
 * @author Hideki IGARASHI
 * @author Kazuki YAMAMOTO
 */
class DocumentBuilder {

    private File templateFile
    private Map baseBinding

    private PageBuilder pageBuilder
    private TocBuilder tocBuilder

    private File outputDirectory

    DocumentBuilder() {
        this.templateFile = Holders.config.templateFile
        this.baseBinding = [
            title: Holders.config.title,
            tocPath: Holders.config.tocOutputFilePath,
        ]
        this.outputDirectory = Holders.config.outputDirectory

        def templateEngine = createTemplateEngine()
        this.pageBuilder = new PageBuilder(templateEngine)
        this.tocBuilder = new TocBuilder(templateEngine)
    }

    DocumentBuilder(File templateFile, PageBuilder pageBuilder, TocBuilder tocBuilder, File outputDirectory, Map baseBinding) {
        this.templateFile = templateFile
        this.pageBuilder = pageBuilder
        this.tocBuilder = tocBuilder
        this.outputDirectory = outputDirectory
        this.baseBinding = baseBinding
    }

    /**
     * Builds a document from a document source.
     *
     * @param context the context to be built
     * @return {@link Document}'s instance
     */
    Document build(BuildContext context) {
        def toc = buildToc(context)
        def pages = buildPages(new PageBuildContext(documentSource: context.documentSource, toc: toc))
        new Document(toc: toc, pages: pages)
    }

    private Toc buildToc(BuildContext context) {
        tocBuilder.build(context)
    }

    private List<Page> buildPages(PageBuildContext context) {
        context.documentSource.pageSources.collect { pageSource ->
            pageBuilder.build(context, pageSource)
        }
    }

    private TemplateEngine createTemplateEngine() {
        new TemplateEngine(outputDirectory, templateFile.text, baseBinding)
    }

}
