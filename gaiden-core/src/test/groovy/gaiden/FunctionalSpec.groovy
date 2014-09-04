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

import static org.hamcrest.CoreMatchers.*
import static org.junit.Assert.*

abstract class FunctionalSpec extends GaidenSpec {

    ApplicationContext applicationContext

    GaidenConfig gaidenConfig

    @AutoCleanup("deleteDir")
    Path projectDirectory = Files.createTempDirectory("projectDirectory")

    @AutoCleanup("deleteDir")
    Path outputDirectory = Files.createTempDirectory("outputDirectory")

    def setup() {
        def gaidenApplication = new GaidenApplication()
        applicationContext = gaidenApplication.applicationContext
        gaidenConfig = applicationContext.getBean(GaidenConfig)

        gaidenConfig.outputDirectory = outputDirectory

        setupProjectDirectory(projectDirectory)
        Files.createFile(gaidenConfig.gaidenConfigFile)
    }

    void setupProjectDirectory(String path) {
        setupProjectDirectory(project.resolve(path).toRealPath(LinkOption.NOFOLLOW_LINKS))
    }

    void setupProjectDirectory(Path projectDirectory) {
        gaidenConfig.projectDirectory = projectDirectory
        gaidenConfig.sourceDirectory = projectDirectory

        if (Files.exists(gaidenConfig.gaidenConfigFile)) {
            gaidenConfig.initialize()
        }
    }

    void assertOutputDirectory(String path) {
        def expectedOutputDirectory = project.resolve(path)
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
}
