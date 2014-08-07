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
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import java.nio.file.Files

/**
 * A document builder builds from a document source to a document.
 *
 * @author Hideki IGARASHI
 * @author Kazuki YAMAMOTO
 */
@Component
@CompileStatic
class DocumentBuilder {

    @Autowired
    PageBuilder pageBuilder

    @Autowired
    TocBuilder tocBuilder

    @Autowired
    GaidenConfig gaidenConfig

    /**
     * Builds a document from a document source.
     *
     * @param context the context to be built
     * @return {@link Document}'s instance
     */
    Document build(DocumentSource documentSource) {
        def pageReferences = getPageReferences()
        def pages = buildPages(documentSource, pageReferences)
        def pageOrder = getPageOrder(pageReferences, pages)
        def toc = buildToc(pageOrder)

        new Document(toc: toc, pages: pages, pageOrder: pageOrder)
    }

    private List<PageReference> getPageReferences() {
        def pagesParser = new PagesParser()
        pagesParser.pagesDirectory = gaidenConfig.pagesDirectory
        pagesParser.parse(gaidenConfig.pagesFile.getText(gaidenConfig.inputEncoding))
    }

    private static List<Page> getPageOrder(List<PageReference> pageReferences, List<Page> pages) {
        def pageOrder = []
        pageReferences.each { PageReference pageReference ->
            def page = pages.find { Files.isSameFile(it.source.path, pageReference.path) }
            if (!page) {
                // TODO warning log
                return
            }
            pageOrder << page
        }
        pageOrder
    }

    private Toc buildToc(List<Page> pageOrder) {
        tocBuilder.build(pageOrder)
    }

    private List<Page> buildPages(DocumentSource documentSource, List<PageReference> pageReferences) {
        documentSource.pageSources.collect { PageSource pageSource ->
            def pageReference = pageReferences.find { Files.isSameFile(it.path, pageSource.path) }
            pageBuilder.build(pageSource, pageReference)
        }
    }
}
