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

    private ConfigObject configObject = new ConfigObject()

    static final String GAIDEN_CONFIG_FILENAME = "config.groovy"
    static final String PAGES_FILENAME = "pages.groovy"
    static final String DEFAULT_ENCODING = "UTF-8"

    static final String DEFAULT_BUILD_DIRECTORY = "build"
    static final String THEMES_DIRECTORY = "themes"
    static final String PROJECT_TEMPLATE_DIRECTORY = "template"
    static final String LAYOUTS_DIRECTORY = "layouts"
    static final String ASSETS_DIRECTORY = "assets"
    static final String WRAPPER_DIRECTORY = "wrapper"
    static final String EXTENSIONS_DIRECTORY = "extensions"

    static final String DEFAULT_THEME = "default"
    static final String DEFAULT_LAYOUT = "main"

    private static Path getDefaultProjectDirectory() {
        Paths.get(System.properties["user.dir"] as String)
    }

    private static Path getDefaultApplicationDirectory() {
        Paths.get(System.properties["app.home"] as String)
    }

    /** The base title of page */
    String title = "Gaiden"

    /** The path of application directory */
    Path applicationDirectory = defaultApplicationDirectory

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

    boolean interactive = true

    Path homePage

    String theme = DEFAULT_THEME

    int documentTocDepth = 3

    int pageTocDepth = 99

    boolean numbering = true

    int numberingOffset = 0

    int numberingDepth = 4

    boolean format = true

    boolean readmeToIndex = true

    List<String> assetTypes = ["jpg", "jpeg", "png", "gif"]

    LinkedHashMap<String, Filter> filters = [:]

    SortedMap<String, Extension> extensions = new TreeMap<>()

    Path getApplicationInitialProjectTemplateDirectory() {
        applicationDirectory.resolve(PROJECT_TEMPLATE_DIRECTORY)
    }

    Path getApplicationThemesDirectory() {
        applicationDirectory.resolve(THEMES_DIRECTORY)
    }

    Path getApplicationWrapperDirectory() {
        applicationDirectory.resolve(WRAPPER_DIRECTORY)
    }

    Path getApplicationLayoutsDirectory() {
        applicationThemesDirectory.resolve(theme).resolve(LAYOUTS_DIRECTORY)
    }

    Path getApplicationAssetsDirectory() {
        applicationThemesDirectory.resolve(theme).resolve(ASSETS_DIRECTORY)
    }

    Path getApplicationThemeDirectory(String theme = this.theme) {
        applicationThemesDirectory.resolve(theme)
    }

    /** The path of pages file */
    Path getPagesFile() {
        sourceDirectory.resolve(PAGES_FILENAME)
    }

    /** The path of gaiden config file */
    Path getGaidenConfigFile() {
        projectDirectory.resolve(GAIDEN_CONFIG_FILENAME)
    }

    Path getProjectLayoutsDirectory() {
        projectThemesDirectory.resolve(theme).resolve(LAYOUTS_DIRECTORY)
    }

    Path getProjectAssetsDirectory() {
        projectThemesDirectory.resolve(theme).resolve(ASSETS_DIRECTORY)
    }

    Path getProjectThemesDirectory() {
        projectDirectory.resolve(THEMES_DIRECTORY)
    }

    Path getProjectThemeDirectory(String theme = this.theme) {
        projectThemesDirectory.resolve(theme)
    }

    Path getExtensionsDirectory() {
        projectDirectory.resolve(EXTENSIONS_DIRECTORY)
    }

    Path getExtensionAssetsOutputDirectoryOf(Extension extension) {
        outputDirectory.resolve(EXTENSIONS_DIRECTORY).resolve(extension.name)
    }

    Path getLayoutFile(String layoutName) {
        def filename = layoutName ?: DEFAULT_LAYOUT
        def layoutFile = projectLayoutsDirectory.resolve("${filename}.html")
        if (Files.exists(layoutFile)) {
            return layoutFile
        }
        return applicationLayoutsDirectory.resolve("${filename}.html")
    }

    void setProjectDirectoryPath(String projectDirectoryPath) {
        projectDirectory = Paths.get(projectDirectoryPath)
    }

    void setSourceDirectoryPath(String sourceDirectoryPath) {
        sourceDirectory = projectDirectory.resolve(sourceDirectoryPath)
    }

    void setOutputDirectoryPath(String outputDirectoryPath) {
        outputDirectory = projectDirectory.resolve(outputDirectoryPath)
    }

    void setHomePage(String homePage) {
        this.homePage = sourceDirectory.resolve(homePage)
    }

    List<String> getInstalledThemes() {
        PathUtils.list(applicationThemesDirectory, FileType.DIRECTORIES).collect {
            it.fileName.toString()
        }
    }

    void setFilters(Closure closure) {
        filters.putAll(new FilterBuilder().build(closure).collectEntries { Filter filter ->
            [(filter.name), filter]
        })
    }

    @PostConstruct
    void initialize() {
        loadExtensions(extensionsDirectory)
        loadConfig(gaidenConfigFile)
    }

    void loadConfig(Path configFile) {
        if (Files.notExists(configFile)) {
            return
        }

        def configObject = parse(configFile)
        (configObject.keySet() as Set<String>).each { String key ->
            if (this.hasProperty(key)) {
                this.setProperty(key, configObject.get(key))
            }
        }

        this.configObject.merge(configObject)
    }

    void loadExtensions(Path extensionsDirectory) {
        if (Files.notExists(extensionsDirectory)) {
            return
        }

        extensionsDirectory.eachDir { Path directory ->
            def name = directory.fileName.toString()
            def configFile = directory.resolve(GAIDEN_CONFIG_FILENAME)
            extensions[name] = new Extension([
                name           : name,
                configObject   : Files.exists(configFile) ? parse(configFile) : new ConfigObject(),
                assetsDirectory: directory.resolve(ASSETS_DIRECTORY),
            ])
        }

        extensions.each { String name, Extension extension ->
            filters.putAll(extension.filters.collectEntries { Filter filter ->
                [(extension.name + '.' + filter.name): filter]
            })
        }
    }

    @CompileStatic(TypeCheckingMode.SKIP)
    def propertyMissing(String name) {
        configObject.get(name)
    }

    void add(String key, Object value) {
        configObject.put(key, value)
    }

    private static ConfigObject parse(Path configFile) {
        new ConfigSlurper().parse(configFile.toUri().toURL())
    }
}
