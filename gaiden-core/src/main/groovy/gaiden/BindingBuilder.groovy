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
import groovy.io.FileType
import groovy.transform.CompileStatic

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

import static org.apache.commons.lang3.StringEscapeUtils.*

/**
 * Builder for binding to be passed to a template engine.
 *
 * @author Kazuki YAMAMOTO
 */
@CompileStatic
class BindingBuilder {

    MessageSource messageSource
    GaidenMarkdownProcessor markdownProcessor
    GaidenConfig gaidenConfig
    Page page
    Document document
    String content
    TemplateEngine templateEngine

    /**
     * Builds binding.
     *
     * @return binding
     */
    Map<String, Object> build() {
        [
            title           : escapeHtml4(gaidenConfig.title),
            document        : document,
            content         : content,
            metadata        : page.metadata,
            currentPage     : page,
            prevPage        : document.previousPageOf(page),
            nextPage        : document.nextPageOf(page),
            config          : gaidenConfig,
            homePage        : document.homePage,
            extensionScripts: extensionScripts,
            extensionStyles : extensionStyles,
            resource        : this.&getResource,
            render          : this.&render,
            include         : this.&include,
        ]
    }

    private Path getResource(String path) {
        def resourceFile = Paths.get(path)
        def dest = gaidenConfig.outputDirectory.resolve(resourceFile.absolute ? Paths.get(resourceFile.toString().substring(1)) : resourceFile)
        return page.relativize(dest)
    }

    private String render(String filePath) {
        def file = gaidenConfig.sourceDirectory.resolve(filePath)
        def page = document.pages.find { Files.isSameFile(it.source.path, file) }
        if (!page) {
            System.err.println("WARNING: " + messageSource.getMessage("output.page.reference.not.exists.message", [filePath, gaidenConfig.getLayoutFile(page.metadata.layout as String)]))
            return ""
        }
        return markdownProcessor.convertToHtml(page, document)
    }

    private List<Path> getExtensionScripts() {
        List<Path> scripts = []
        gaidenConfig.extensions.each { String name, Extension extension ->
            if (Files.notExists(extension.assetsDirectory)) {
                return
            }
            extension.assetsDirectory.eachFileRecurse(FileType.FILES) { Path file ->
                if (PathUtils.getExtension(file) == "js") {
                    scripts << gaidenConfig.getExtensionAssetsOutputDirectoryOf(extension).resolve(extension.assetsDirectory.relativize(file))
                }
            }
        }
        return scripts.collect { page.relativize(it) }
    }

    private List<Path> getExtensionStyles() {
        List<Path> styles = []
        gaidenConfig.extensions.each { String name, Extension extension ->
            if (Files.notExists(extension.assetsDirectory)) {
                return
            }
            extension.assetsDirectory.eachFileRecurse(FileType.FILES) { Path file ->
                if (PathUtils.getExtension(file) == "css") {
                    styles << gaidenConfig.getExtensionAssetsOutputDirectoryOf(extension).resolve(extension.assetsDirectory.relativize(file))
                }
            }
        }
        return styles.collect { page.relativize(it) }
    }

    private String include(String layout) {
        templateEngine.make(layout, build())
    }
}
