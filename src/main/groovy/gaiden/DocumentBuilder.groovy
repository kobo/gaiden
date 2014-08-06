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
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

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
        context.documentSource.pageSources.collect { PageSource pageSource ->
            pageBuilder.build(context, pageSource)
        }
    }
}
