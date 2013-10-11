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

    private File outputDirectory
    private File staticDirectory
    private String outputEncoding

    DocumentWriter(
        File staticDirectory = Holders.config.staticDirectory,
        File outputDirectory = Holders.config.outputDirectory,
        String outputEncoding = Holders.config.outputEncoding) {
        this.staticDirectory = staticDirectory
        this.outputDirectory = outputDirectory
        this.outputEncoding = outputEncoding
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
            writeToFile(page)
        }
    }

    private void writeToc(Toc toc) {
        writeToFile(toc)
    }

    void writeToFile(data) {
        if (!data) {
            return
        }

        def file = new File(outputDirectory, data.path as String)
        if (!file.parentFile.exists()) {
            assert file.parentFile.mkdirs()
        }
        file.write(data.content as String, outputEncoding)
    }

    private void copyStaticFiles() {
        FileUtils.copyDirectory(staticDirectory, outputDirectory)
    }

}
