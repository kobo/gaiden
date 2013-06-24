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

import spock.lang.Specification

class ConfigLoaderSpec extends Specification {

    def "'load' should load a configuration file"() {
        def loader = new ConfigLoader(new File("src/test/resources/config/ValidConfig.groovy"))

        when:
        loader.load()

        then:
        with(GaidenConfig.instance) {
            title == "Test Title"
            templatePath == "test/templates"
            pagesDirectory == "test/pages"
            staticDirectory == "test/static"
            outputDirectory == "test/build/html"
        }
    }

    def "'load' should load the default configuration file"() {
        def loader = new ConfigLoader(new File("src/test/resources/config/NotFoundConfig.groovy"))

        when:
        loader.load()

        then:
        with(GaidenConfig.instance) {
            title == "Gaiden"
            templatePath == "templates"
            pagesDirectory == "pages"
            staticDirectory == "static"
            outputDirectory == "build/html"
        }
    }

    def "'load' should load the defaults configuration other than values set"() {
        def loader = new ConfigLoader(new File("src/test/resources/config/OnlyTitleConfig.groovy"))

        when:
        loader.load()

        then:
        with(GaidenConfig.instance) {
            title == "Test Title"
            templatePath == "templates"
            pagesDirectory == "pages"
            staticDirectory == "static"
            outputDirectory == "build/html"
        }
    }

    def cleanup() {
        def config = GaidenConfig.instance

        config.properties.each { String key, value ->
            if (key in ['class', 'instance', 'pagesDirectoryFile', 'outputDirectoryFile', 'staticDirectoryFile', 'templatePathFile']) {
                return
            }

            config[key] = null
        }
    }

}
