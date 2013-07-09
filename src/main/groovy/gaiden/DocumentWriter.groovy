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

import org.apache.commons.io.FileUtils


/**
 * A document writer writes a {@link Document} to files.
 *
 * @author Hideki IGARASHI
 * @author Kazuki YAMAMOTO
 */
class DocumentWriter {

    File outputDirectory
    File staticDirectory

    DocumentWriter(staticDirectory = GaidenConfig.instance.staticDirectoryFile, outputDirectory = GaidenConfig.instance.outputDirectoryFile) {
        this.staticDirectory = staticDirectory
        this.outputDirectory = outputDirectory
    }

    /**
     * Writes a {@link Document} to file.
     *
     * @param document the document to be written
     */
    void write(Document document) {
        if (!outputDirectory.exists()) {
            assert outputDirectory.mkdirs()
        }

        writePages(document.pages)
        writeToc(document.toc)

        copyStaticFiles()

        println "Built document at ${outputDirectory.canonicalPath}"
    }

    private void writePages(List<Page> pages) {
        pages.each { Page page ->
            page.writeTo(outputDirectory)
        }
    }

    private void writeToc(Toc toc) {
        toc.writeTo(outputDirectory)
    }

    private void copyStaticFiles() {
        FileUtils.copyDirectory(staticDirectory, outputDirectory)
    }

}
