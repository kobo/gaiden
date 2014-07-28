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

import gaiden.message.MessageSource
import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode
import org.springframework.beans.factory.annotation.Autowired
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

    @Autowired
    MessageSource messageSource

    static final Path CONFIG_PATH = Paths.get("GaidenConfig.groovy")

    /** The base title of page */
    String title = "Gaiden"

    /** The title of TOC */
    String tocTitle = "Table of contents"

    /** The path of template file */
    String templateFilePath = "templates/layout.html"

    /** The path of TOC file */
    String tocFilePath = "pages/toc.groovy"

    /** The path of TOC output file */
    String tocOutputFilePath = "toc.html"

    /** The path of page source files directory */
    String pagesDirectoryPath = "pages"

    /** The path of static files directory */
    String staticDirectoryPath = "static"

    /** The path of directory to be outputted a document */
    String outputDirectoryPath = "build"

    /** The input encoding of files */
    String inputEncoding = "UTF-8"

    /** The output encoding of files */
    String outputEncoding = "UTF-8"

    /** Returns the {@link #templateFilePath} as {@link File} */
    File getTemplateFile() {
        new File(templateFilePath)
    }

    /** Returns the {@link #tocFilePath} as {@link File} */
    File getTocFile() {
        new File(tocFilePath)
    }

    /** Returns the {@link #pagesDirectoryPath} as {@link File} */
    File getPagesDirectory() {
        new File(pagesDirectoryPath)
    }

    /** Returns the {@link #staticDirectoryPath} as {@link File} */
    File getStaticDirectory() {
        new File(staticDirectoryPath)
    }

    /** Returns the {@link #outputDirectoryPath} as {@link File} */
    File getOutputDirectory() {
        new File(outputDirectoryPath)
    }

    /** Returns the application home directory as {@link Path} */
    Path getAppHome() {
        Paths.get(System.properties["app.home"] as String)
    }

    /** Returns the user directory as {@link Path} */
    Path getUserDirectory() {
        Paths.get(System.properties["user.dir"] as String)
    }

    /** Returns the default template directory as {@link Path} */
    Path getDefaultTemplateDirectory() {
        appHome.resolve("template")
    }

    @PostConstruct
    void loadConfigFile() {
        if (Files.notExists(CONFIG_PATH)) {
            return
        }

        def configObject = new ConfigSlurper().parse(CONFIG_PATH.toUri().toURL())
        configObject.each { Map.Entry entry ->
            def key = entry.key as String
            if (this.hasProperty(key)) {
                this.setProperty(key, entry.value)
            }
        }
    }

    /**
     * Replaces a deprecated parameter with new one.
     *
     * @param name a property name
     * @param value a property value
     */
    @CompileStatic(TypeCheckingMode.SKIP)
    def propertyMissing(String name, value) {
        def deprecatedMapping = [
            templatePath : "templateFilePath",
            tocPath      : "tocFilePath",
            tocOutputPath: "tocOutputFilePath",
        ]

        if (!deprecatedMapping.containsKey(name)) {
            throw new MissingPropertyException(name, this.class)
        }

        printDeprecatedWarning(name, deprecatedMapping[name])
        this."${deprecatedMapping[name]}" = value
    }

    // Conflicts with getPagesDirectory()
    @Deprecated
    void setPagesDirectory(String value) {
        printDeprecatedWarning("pagesDirectory", "pagesDirectoryPath")
        pagesDirectoryPath = value
    }

    // Conflicts with getStaticDirectory()
    @Deprecated
    void setStaticDirectory(String value) {
        printDeprecatedWarning("staticDirectory", "staticDirectoryPath")
        staticDirectoryPath = value
    }

    // Conflicts with getOutputDirectory()
    @Deprecated
    void setOutputDirectory(String value) {
        printDeprecatedWarning("outputDirectory", "outputDirectoryPath")
        outputDirectoryPath = value
    }

    private void printDeprecatedWarning(String deprecatedName, String insteadName) {
        System.err.println("WARNING: ${messageSource.getMessage("config.deprecated.parameter.message", [deprecatedName, insteadName] as Object[], Locale.default)}")
    }

}
