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
import gaiden.message.MessageSource
import gaiden.util.PathUtils
import groovy.transform.CompileStatic
import net.htmlparser.jericho.Source
import net.htmlparser.jericho.SourceFormatter
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

    @Autowired
    TemplateEngine templateEngine

    @Autowired
    GaidenMarkdownProcessor markdownProcessor

    @Autowired
    MessageSource messageSource

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
        copyAssets()
    }

    private void writePages(Document document) {
        document.pages.each { Page page ->
            def message = "\r>> ${gaidenConfig.sourceDirectory.relativize(page.source.path)} => ${gaidenConfig.outputDirectory.relativize(page.outputPath)}: "
            print message
            writePage(page, document)
            print "\r${' ' * message.size()}"
        }
        print "\r"
    }

    private void writePage(Page page, Document document) {
        if (Files.notExists(page.outputPath.parent)) {
            Files.createDirectories(page.outputPath.parent)
        }

        def binding = new BindingBuilder()
            .setGaidenConfig(gaidenConfig)
            .setMessageSource(messageSource)
            .setMarkdownProcessor(markdownProcessor)
            .setPage(page)
            .setDocument(document)
            .setContent(markdownProcessor.convertToHtml(page, document))
            .build()

        def content = templateEngine.make(page.metadata.layout as String, binding)
        def formattedContent = format(content)
        def filteredContent = gaidenConfig.filters.inject(formattedContent) { String text, Filter filter ->
            filter.doAfterTemplate(text)
        } as String
        Files.write(page.outputPath, filteredContent.getBytes(gaidenConfig.outputEncoding))
    }

    private String format(String content) {
        if (!gaidenConfig.format) {
            return content
        }
        def source = new Source(content)
        def writer = new StringWriter()
        new SourceFormatter(source).setIndentString("  ").writeTo(writer)
        return writer.toString()
    }

    private void copyAssets() {
        PathUtils.copyFiles(gaidenConfig.applicationAssetsDirectory, gaidenConfig.outputDirectory)
        gaidenConfig.extensions.each {
            PathUtils.copyFiles(it.assetsDirectory, gaidenConfig.getExtensionAssetsOutputDirectoryOf(it))
        }
        PathUtils.copyFiles(gaidenConfig.projectAssetsDirectory, gaidenConfig.outputDirectory)
        PathUtils.eachFileRecurse(gaidenConfig.sourceDirectory, [gaidenConfig.outputDirectory, gaidenConfig.extensionsDirectory, gaidenConfig.projectThemesDirectory]) { Path src ->
            if (isAsset(src)) {
                def dest = gaidenConfig.outputDirectory.resolve(gaidenConfig.sourceDirectory.relativize(src))
                PathUtils.copyFile(src, dest)
            }
        }
    }

    private boolean isAsset(Path path) {
        PathUtils.getExtension(path)?.toLowerCase() in gaidenConfig.assetTypes
    }
}
