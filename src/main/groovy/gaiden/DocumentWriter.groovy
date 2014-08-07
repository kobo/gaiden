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

import gaiden.markdown.GaidenMarkdownProcessor
import gaiden.util.PathUtils
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import java.nio.file.Files

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

    @Autowired
    TemplateEngine templateEngine

    @Autowired
    GaidenMarkdownProcessor markdownProcessor

    /**
     * Writes a {@link Document} to file.
     *
     * @param document the document to be written
     */
    void write(Document document) {
        if (Files.notExists(gaidenConfig.outputDirectory)) {
            Files.createDirectories(gaidenConfig.outputDirectory)
        }

        writePages(document)
        copyStaticFiles()

        println "Built document at ${gaidenConfig.outputDirectory.toAbsolutePath()}"
    }

    private void writePages(Document document) {
        document.pages.each { Page page ->
            writePage(page, document)
        }
    }

    private void writePage(Page page, Document document) {
        if (Files.notExists(page.source.outputPath.parent)) {
            Files.createDirectories(page.source.outputPath.parent)
        }

        def binding = new BindingBuilder()
            .setGaidenConfig(gaidenConfig)
            .setPage(page)
            .setDocument(document)
            .setContent(markdownProcessor.convertToHtml(page, document))
            .build()

        def content = templateEngine.make(binding)
        Files.write(page.source.outputPath, content.getBytes(gaidenConfig.outputEncoding))
    }

    private void copyStaticFiles() {
        PathUtils.copyFiles(gaidenConfig.assetsDirectory, gaidenConfig.outputDirectory)
    }
}
