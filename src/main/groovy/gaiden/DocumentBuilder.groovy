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

/**
 * A document builder builds from a document source to a document.
 *
 * @author Hideki IGARASHI
 * @author Kazuki YAMAMOTO
 */
class DocumentBuilder {

    private File templateFile
    private File tocFile
    private Map baseBinding

    DocumentBuilder() {
        this(GaidenConfig.instance.templatePathFile, GaidenConfig.instance.tocPathFile, [title: GaidenConfig.instance.title])
    }

    DocumentBuilder(File templateFile, File tocFile, Map baseBinding) {
        this.templateFile = templateFile
        this.tocFile = tocFile
        this.baseBinding = baseBinding
    }

    /**
     * Builds a document from a document source.
     *
     * @param documentSource the document source to be built
     * @return {@link Document}'s instance
     */
    Document build(DocumentSource documentSource) {
        new Document(toc: buildToc(), pages: buildPages(documentSource))
    }

    private Toc buildToc() {
        new TocBuilder(templateEngine: createTemplateEngine(), tocFile: tocFile).build()
    }

    private List<Page> buildPages(DocumentSource documentSource) {
        def builder = new PageBuilder(templateEngine: createTemplateEngine())

        documentSource.pageSources.collect { pageSource ->
            builder.build(pageSource)
        }
    }

    private TemplateEngine createTemplateEngine() {
        new TemplateEngine(templateFile.text, baseBinding)
    }

}
