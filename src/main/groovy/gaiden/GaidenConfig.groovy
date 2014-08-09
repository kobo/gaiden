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
import groovy.transform.TypeCheckingMode
import org.codehaus.groovy.control.CompilerConfiguration
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

/**
 * The Gaiden configuration.
 *
 * @author Kazuki YAMAMOTO
 * @author Hideki IGARASHI
 */
@Component
@CompileStatic
class GaidenConfig {

    static final String DEFAULT_BUILD_DIRECTORY = "build"
    static final String DEFAULT_TEMPLATE_FILE = "templates/layout.html"
    static final String DEFAULT_ENCODING = "UTF-8"

    static final String GAIDEN_CONFIG_FILENAME = "config.groovy"
    static final String PAGES_FILENAME = "pages.groovy"
    static final String DEFAULT_PROJECT_TEMPLATE_DIRECTORY = "template"

    private static Path getDefaultProjectDirectory() {
        Paths.get(System.properties["user.dir"] as String)
    }

    private static Path getApplicationDirectory() {
        Paths.get(System.properties["app.home"] as String)
    }

    /** The base title of page */
    String title = "Gaiden"

    /** The path of project directory */
    Path projectDirectory = defaultProjectDirectory

    /** The path of template file */
    Path templateFile = defaultProjectDirectory.resolve(DEFAULT_TEMPLATE_FILE)

    /** The path of page source files directory */
    Path pagesDirectory = defaultProjectDirectory

    /** The path of directory to be outputted a document */
    Path outputDirectory = defaultProjectDirectory.resolve(DEFAULT_BUILD_DIRECTORY)

    /** The input encoding of files */
    String inputEncoding = DEFAULT_ENCODING

    /** The output encoding of files */
    String outputEncoding = DEFAULT_ENCODING

    /** The path of pages file */
    Path pagesFile = defaultProjectDirectory.resolve(PAGES_FILENAME)

    /** The path of gaiden config file */
    Path gaidenConfigFile = defaultProjectDirectory.resolve(GAIDEN_CONFIG_FILENAME)

    /** The path of project template directory */
    Path projectTemplateDirectory = applicationDirectory.resolve(DEFAULT_PROJECT_TEMPLATE_DIRECTORY)

    int documentTocDepth = 3

    boolean numbering = true

    boolean format = true

    List<String> assetTypes = ["jpg", "jpeg", "png", "gif"]

    void setProjectDirectoryPath(String projectDirectory) {
        this.projectDirectory = Paths.get(projectDirectory)
    }

    void setTemplateFilePath(String templateFile) {
        this.templateFile = projectDirectory.resolve(templateFile)
    }

    void setPagesDirectoryPath(String pagesDirectory) {
        this.pagesDirectory = projectDirectory.resolve(pagesDirectory)
    }

    void setOutputDirectoryPath(String outputDirectory) {
        this.outputDirectory = projectDirectory.resolve(outputDirectory)
    }

    void setPagesFilePath(String pagesFile) {
        this.pagesFile = projectDirectory.resolve(pagesFile)
    }

    @PostConstruct
    void initialize() {
        initialize(gaidenConfigFile)
    }

    @CompileStatic(TypeCheckingMode.SKIP)
    void initialize(Path configFile) {
        if (Files.notExists(configFile)) {
            return
        }

        CompilerConfiguration configuration = new CompilerConfiguration()
        configuration.scriptBaseClass = DelegatingScript.name
        GroovyShell shell = new GroovyShell(new Binding(), configuration)
        DelegatingScript script = shell.parse(configFile.text) as DelegatingScript
        script.setDelegate(this)
        script.run()
    }
}
