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
import groovy.io.FileType
import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode
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

    private ConfigObject configObject

    static final String GAIDEN_CONFIG_FILENAME = "config.groovy"
    static final String PAGES_FILENAME = "pages.groovy"
    static final String DEFAULT_ENCODING = "UTF-8"

    static final String DEFAULT_BUILD_DIRECTORY = "build"
    static final String DEFAULT_THEMES_DIRECTORY = "themes"
    static final String DEFAULT_PROJECT_TEMPLATE_DIRECTORY = "template"
    static final String DEFAULT_LAYOUTS_DIRECTORY = "layouts"
    static final String DEFAULT_ASSETS_DIRECTORY = "assets"

    static final String DEFAULT_THEME = "default"
    static final String DEFAULT_LAYOUT = "default"

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

    /** The path of source files directory */
    Path sourceDirectory = defaultProjectDirectory

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
    Path initialProjectTemplateDirectory = applicationDirectory.resolve(DEFAULT_PROJECT_TEMPLATE_DIRECTORY)

    Path applicationThemesDirectory = applicationDirectory.resolve(DEFAULT_THEMES_DIRECTORY)

    Path projectThemesDirectory = defaultProjectDirectory.resolve(DEFAULT_THEMES_DIRECTORY)

    Path getApplicationLayoutsDirectory() {
        applicationThemesDirectory.resolve(theme).resolve(DEFAULT_LAYOUTS_DIRECTORY)
    }

    Path getProjectLayoutsDirectory() {
        projectThemesDirectory.resolve(theme).resolve(DEFAULT_LAYOUTS_DIRECTORY)
    }

    Path getApplicationAssetsDirectory() {
        applicationThemesDirectory.resolve(theme).resolve(DEFAULT_ASSETS_DIRECTORY)
    }

    Path getProjectAssetsDirectory() {
        projectThemesDirectory.resolve(theme).resolve(DEFAULT_ASSETS_DIRECTORY)
    }

    Path getLayoutFile(String layoutName) {
        def filename = layoutName ?: DEFAULT_LAYOUT
        def layoutFile = projectLayoutsDirectory.resolve("${filename}.html")
        if (Files.exists(layoutFile)) {
            return layoutFile
        }
        return applicationLayoutsDirectory.resolve("${filename}.html")
    }

    Path getApplicationThemeDirectory(String theme = this.theme) {
        applicationThemesDirectory.resolve(theme)
    }

    Path getProjectThemeDirectory(String theme = this.theme) {
        projectThemesDirectory.resolve(theme)
    }

    Path homePage

    void setHomePage(String homePage) {
        this.homePage = sourceDirectory.resolve(homePage)
    }

    List<String> getInstalledThemes() {
        PathUtils.list(applicationThemesDirectory, FileType.DIRECTORIES).collect {
            it.fileName.toString()
        }
    }

    String theme = DEFAULT_THEME

    int documentTocDepth = 3

    int pageTocDepth = 6

    boolean numbering = true

    int numberingOffset = 0

    int numberingDepth = 4

    boolean format = true

    boolean readmeToIndex = true

    List<String> assetTypes = ["jpg", "jpeg", "png", "gif"]

    void setProjectDirectoryPath(String projectDirectoryPath) {
        projectDirectory = Paths.get(projectDirectoryPath)
    }

    void setSourceDirectoryPath(String sourceDirectoryPath) {
        sourceDirectory = projectDirectory.resolve(sourceDirectoryPath)
    }

    void setOutputDirectoryPath(String outputDirectoryPath) {
        outputDirectory = projectDirectory.resolve(outputDirectoryPath)
    }

    void setPagesFilePath(String pagesFilePath) {
        pagesFile = projectDirectory.resolve(pagesFilePath)
    }

    void setApplicationThemesDirectoryPath(String applicationThemesDirectoryPath) {
        applicationThemesDirectory = applicationDirectory.resolve(applicationThemesDirectoryPath)
    }

    void setProjectThemesDirectoryPath(String projectThemesDirectoryPath) {
        projectThemesDirectory = projectDirectory.resolve(projectThemesDirectoryPath)
    }

    List<Filter> filters = Collections.emptyList()

    void setFilters(Closure closure) {
        def filterBuilder = new FilterBuilder()
        filters = filterBuilder.build(closure)
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

        configObject = new ConfigSlurper().parse(configFile.toUri().toURL())
        configObject.keySet().each { String key ->
            if (this.hasProperty(key)) {
                this.setProperty(key, configObject.get(key))
            }
        }
    }

    @CompileStatic(TypeCheckingMode.SKIP)
    def propertyMissing(String name) {
        if (configObject) {
            return configObject.get(name)
        }
    }
}
