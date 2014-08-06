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
import gaiden.message.MessageSource
import org.apache.commons.io.FilenameUtils
import spock.lang.Shared
import spock.lang.Specification

import java.nio.file.Files

abstract class GaidenSpec extends Specification {

    def savedSystemOut
    def savedSystemErr
    def savedSystemSecurityManager

    @Shared
    MessageSource messageSource = new MessageSource()

    def pagesDirectory = File.createTempDir()

    def setup() {
        saveSystemProperties()
        setupGaidenConfig()
    }

    def cleanup() {
        cleanupGaidenConfig()
        restoreSystemProperties()
    }

    BuildContext createBuildContext(List<Map<String, String>> pageSourceMaps) {
        def pageSources = pageSourceMaps.collect { Map<String, String> params -> createPageSource(params.path) }
        def documentSource = new DocumentSource(pageSources: pageSources)
        new BuildContext(documentSource: documentSource)
    }

    PageSource createPageSource(String path, String content = null) {
        def pageSource = new PageSource()

        pageSource.path = gaidenConfig.pagesDirectory.resolve(path)
        if (Files.notExists(pageSource.path.parent)) {
            Files.createDirectories(pageSource.path.parent)

        }
        if (Files.exists(pageSource.path)) {
            Files.delete(pageSource.path)
        }
        Files.createFile(pageSource.path)
        if (content) {
            pageSource.path.write(content)
        }

        pageSource.outputPath = gaidenConfig.outputDirectory.resolve(FilenameUtils.removeExtension(path) + ".html")
        pageSource.intputEncoding = gaidenConfig.inputEncoding

        pageSource
    }

    GaidenConfig gaidenConfig

    private void setupGaidenConfig() {
        gaidenConfig = new GaidenConfig()
        gaidenConfig.projectDirectoryPath = Files.createTempDirectory("gaiden-project")
    }

    private void cleanupGaidenConfig() {
        gaidenConfig.projectDirectory.deleteDir()
    }

    private saveSystemProperties() {
        savedSystemOut = System.out
        savedSystemErr = System.err
        savedSystemSecurityManager = System.securityManager
    }

    private void restoreSystemProperties() {
        System.out = savedSystemOut
        System.err = savedSystemErr
        System.securityManager = savedSystemSecurityManager
    }
}
