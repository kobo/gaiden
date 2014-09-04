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

import groovy.text.SimpleTemplateEngine
import groovy.text.Template
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

/**
 * Substitutes variables in a template source text.
 *
 * @author Hideki IGARASHI
 * @author Kazuki YAMAMOTO
 */
@Component
@CompileStatic
class TemplateEngine {

    private Map<String, Template> templateCache = [:]

    @Autowired
    GaidenConfig gaidenConfig

    /**
     * Produces a result from a template.
     *
     * @param binding variables of placeholder, this variables overwrite a base binding variables
     * @return a result produced
     */
    String make(String templateName, Map binding) {
        def template = getTemplate(templateName)
        template.make(binding)
    }

    private Template getTemplate(String layoutName) {
        if (templateCache.containsKey(layoutName)) {
            return templateCache[layoutName]
        }
        def template = new SimpleTemplateEngine().createTemplate(gaidenConfig.getLayoutFile(layoutName).text)
        templateCache[layoutName] = template
        return template
    }
}
