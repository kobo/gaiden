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

class GaidenConfigInitializerSpec extends Specification {

    def "'initialize' should initialize a configuration file"() {
        setup:
        def initializer = new GaidenConfigInitializer()

        when:
        initializer.initialize(new File("src/test/resources/config/ValidConfig.groovy"))

        then:
        with(GaidenConfig.instance) {
            title == "Test Title"
            templatePath == "test/templates/layout.html"
            tocPath == "test/pages/toc.groovy"
            tocOutputPath == "test/toc.html"
            pagesDirectory == "test/pages"
            staticDirectory == "test/static"
            outputDirectory == "test/build/html"
        }
    }

    def "'initialize' should initialize the default configuration file"() {
        setup:
        def initializer = new GaidenConfigInitializer()

        when:
        initializer.initialize(new File("src/test/resources/config/NotFoundConfig.groovy"))

        then:
        with(GaidenConfig.instance) {
            title == "Gaiden"
            templatePath == "templates/layout.html"
            tocPath == "pages/toc.groovy"
            tocOutputPath == "toc.html"
            pagesDirectory == "pages"
            staticDirectory == "static"
            outputDirectory == "build"
        }
    }

    def "'initialize' should initialize the defaults configuration other than values set"() {
        setup:
        def initializer = new GaidenConfigInitializer()

        when:
        initializer.initialize(new File("src/test/resources/config/OnlyTitleConfig.groovy"))

        then:
        with(GaidenConfig.instance) {
            title == "Test Title"
            templatePath == "templates/layout.html"
            tocPath == "pages/toc.groovy"
            tocOutputPath == "toc.html"
            pagesDirectory == "pages"
            staticDirectory == "static"
            outputDirectory == "build"
        }
    }

    def cleanup() {
        def config = GaidenConfig.instance

        config.properties.each { String key, value ->
            if (key in ['class', 'instance', 'pagesDirectoryFile', 'outputDirectoryFile', 'staticDirectoryFile', 'templatePathFile', 'tocPathFile']) {
                return
            }

            config[key] = null
        }
    }

}
