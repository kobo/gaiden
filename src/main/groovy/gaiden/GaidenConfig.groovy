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

/**
 * The Gaiden configuration.
 *
 * @author Kazuki YAMAMOTO
 * @author Hideki IGARASHI
 */
class GaidenConfig {

    /** The base title of page */
    String title

    /** The title of TOC */
    String tocTitle

    /** The path of template file */
    String templateFilePath

    /** The path of TOC file */
    String tocFilePath

    /** The path of TOC output file */
    String tocOutputFilePath

    /** The path of page source files directory */
    String pagesDirectoryPath

    /** The path of static files directory */
    String staticDirectoryPath

    /** The path of directory to be outputted a document */
    String outputDirectoryPath

    /** The input encoding of files */
    String inputEncoding

    /** The output encoding of files */
    String outputEncoding

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

    /**
     * Replaces a deprecated parameter with new one.
     *
     * @param name a property name
     * @param value a property value
     */
    def propertyMissing(String name, value) {
        def deprecatedMapping = [
            templatePath: "templateFilePath",
            tocPath: "tocFilePath",
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
        System.err.println("WARNING: ${Holders.messageSource.getMessage("config.deprecated.parameter.message", [deprecatedName, insteadName])}")
    }

}
