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

import gaiden.util.PathUtils
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import java.nio.file.Files
import java.nio.file.Path

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
        if (Files.notExists(gaidenConfig.outputDirectory)) {
            Files.createDirectories(gaidenConfig.outputDirectory)
        }

        writePages(document.pages)
        writeToc(document.toc)

        copyStaticFiles()

        println "Built document at ${gaidenConfig.outputDirectory.toAbsolutePath()}"
    }

    private void writePages(List<Page> pages) {
        pages.each { Page page ->
            writeToFile(page.path, page.content)
        }
    }

    private void writeToc(Toc toc) {
        if (!toc) {
            return
        }
        writeToFile(toc.path, toc.content)
    }

    private void writeToFile(Path path, String content) {
        if (Files.notExists(path.parent)) {
            Files.createDirectories(path.parent)
        }
        Files.write(path, content.getBytes(gaidenConfig.outputEncoding))
    }

    private void copyStaticFiles() {
        PathUtils.copyFiles(gaidenConfig.staticDirectory, gaidenConfig.outputDirectory)
    }
}
