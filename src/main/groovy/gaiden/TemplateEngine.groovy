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

import gaiden.util.FileUtils
import groovy.text.SimpleTemplateEngine
import groovy.text.Template

/**
 * Substitutes variables in a template source text.
 *
 * @author Hideki IGARASHI
 * @author Kazuki YAMAMOTO
 */
class TemplateEngine {

    private Template template
    private Map baseBinding
    private File outputDirectoryFile

    /**
     * Creates a new {@link TemplateEngine} instance by the given template text and the base binding variables.
     *
     * @param templateText a template text
     * @param baseBinding base binding variables
     */
    TemplateEngine(File outputDirectoryFile, String templateText, Map baseBinding) {
        this.outputDirectoryFile = outputDirectoryFile
        template = new SimpleTemplateEngine().createTemplate(templateText)
        this.baseBinding = baseBinding
    }

    /**
     * Produces a result from a template.
     *
     * @param binding variables of placeholder, this variables overwrite a base binding variables
     * @return a result produced
     */
    String make(Map binding) {
        binding.link = createLinkTag(binding.outputPath)

        template.make(baseBinding + binding)
    }

    private Closure createLinkTag(String outputPath) {
        return { String resourcePath ->
            if (!new File(resourcePath).absolute) {
                return resourcePath
            }

            def outputFile = new File(outputDirectoryFile, outputPath)
            def resourceFile = new File(outputDirectoryFile, resourcePath)

            FileUtils.getRelativePathForFileToFile(outputFile, resourceFile)
        }
    }

}
