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

/**
 * A source collector collects {@link PageSource} and builds {@link DocumentSource}.
 *
 * @author Kazuki YAMAMOTO
 * @author Hideki IGARASHI
 */
class SourceCollector {

    private static final PAGE_SOURCE_EXTENSIONS = ['md', 'markdown']

    private File pagesDirectory

    SourceCollector(File pagesDirectory = GaidenConfig.instance.pagesDirectoryFile) {
        this.pagesDirectory = pagesDirectory
    }

    /**
     * Collect {@link PageSource} and then returns {@link DocumentSource}.
     *
     * @return {@link DocumentSource}'s instance
     */
    DocumentSource collect() {
        new DocumentSource(pageSources: collectPageSources())
    }

    private List<PageSource> collectPageSources() {
        def pageSources = []
        pagesDirectory.eachFileRecurse { file ->
            if (isPageSourceFile(file)) {
                pageSources << createPageSource(file)
            }
        }
        pageSources
    }

    private boolean isPageSourceFile(File file) {
        file.name ==~ /.*\.(?:${PAGE_SOURCE_EXTENSIONS.join('|')})/
    }

    private PageSource createPageSource(File file) {
        new PageSource(
            path: FileUtils.getRelativePathForDirectoryToFile(pagesDirectory, file),
            content: file.text,
        )
    }

}
