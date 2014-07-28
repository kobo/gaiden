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
import org.apache.commons.io.FileUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component


/**
 * A document writer writes a {@link Document} to files.
 *
 * @author Hideki IGARASHI
 * @author Kazuki YAMAMOTO
 */
@Component
@CompileStatic
class DocumentWriter {

    @Autowired
    GaidenConfig gaidenConfig

    /**
     * Writes a {@link Document} to file.
     *
     * @param document the document to be written
     */
    void write(Document document) {
        if (!gaidenConfig.outputDirectory.exists()) {
            assert gaidenConfig.outputDirectory.mkdirs()
        }

        writePages(document.pages)
        writeToc(document.toc)

        copyStaticFiles()

        println "Built document at ${gaidenConfig.outputDirectory.canonicalPath}"
    }

    private void writePages(List<Page> pages) {
        pages.each { Page page ->
            writeToFile(page.path, page.content)
        }
    }

    private void writeToc(Toc toc) {
        writeToFile(toc.path, toc.content)
    }

    private void writeToFile(String path, String content) {
        def file = new File(gaidenConfig.outputDirectory, path as String)
        if (!file.parentFile.exists()) {
            assert file.parentFile.mkdirs()
        }
        file.write(content, gaidenConfig.outputEncoding)
    }

    private void copyStaticFiles() {
        FileUtils.copyDirectory(gaidenConfig.staticDirectory, gaidenConfig.outputDirectory)
    }

}
