/*
 * Copyright 2014 the original author or authors
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
import groovy.io.FileType
import net.htmlparser.jericho.Source
import net.htmlparser.jericho.SourceFormatter
import org.springframework.context.ApplicationContext
import spock.lang.AutoCleanup

import java.nio.file.Files
import java.nio.file.LinkOption
import java.nio.file.Path
import java.nio.file.Paths

import static org.hamcrest.CoreMatchers.*
import static org.junit.Assert.*

abstract class FunctionalSpec extends GaidenSpec {

    ApplicationContext applicationContext

    GaidenConfig gaidenConfig

    @AutoCleanup("deleteDir")
    Path projectDirectory = Files.createTempDirectory("projectDirectory")

    @AutoCleanup("deleteDir")
    Path outputDirectory = Files.createTempDirectory("outputDirectory")

    def savedSystemOut
    def savedSystemErr
    def savedSystemSecurityManager

    def setup() {
        saveSystemProperties()

        System.properties["app.home"] = Paths.get("src/dist").toRealPath().toString()

        def gaidenApplication = new GaidenApplication()
        applicationContext = gaidenApplication.applicationContext
        gaidenConfig = applicationContext.getBean(GaidenConfig)

        setProjectDirectory(projectDirectory.toString())
        gaidenConfig.outputDirectory = outputDirectory
        Files.createFile(gaidenConfig.gaidenConfigFile)
    }

    def cleanup() {
        restoreSystemProperties()
    }

    void setProjectDirectory(String path) {
        def projectDirectory = Paths.get(path).toRealPath(LinkOption.NOFOLLOW_LINKS)
        gaidenConfig.projectDirectory = projectDirectory
        gaidenConfig.gaidenConfigFile = projectDirectory.resolve(GaidenConfig.GAIDEN_CONFIG_FILENAME)
        gaidenConfig.pagesDirectory = projectDirectory
        gaidenConfig.templateFile = projectDirectory.resolve(GaidenConfig.DEFAULT_TEMPLATE_FILE)
        gaidenConfig.pagesFile = projectDirectory.resolve(GaidenConfig.PAGES_FILENAME)
    }

    void assertOutputDirectory(String path) {
        def expectedOutputDirectory = Paths.get(path)
        expectedOutputDirectory.eachFileRecurse(FileType.FILES) { Path file ->
            def outputFile = outputDirectory.resolve(expectedOutputDirectory.relativize(file))
            assert Files.exists(outputFile)

            if (PathUtils.getExtension(file) == "html") {
                def actual = format(outputFile.text)
                def expected = format(file.text)
                assertThat(actual, is(expected))
            }
        }
    }

    void assertDirectory(Path actual, Path expected) {
        expected.eachFileRecurse(FileType.FILES) { Path file ->
            assert Files.exists(actual.resolve(expected.relativize(file)))
        }
    }

    String format(String html) {
        def source = new Source(html)
        def writer = new StringWriter()
        new SourceFormatter(source).setIndentString("  ").writeTo(writer)
        return writer.toString()
    }

    private void saveSystemProperties() {
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
