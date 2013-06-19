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

class TemplateEngineSpec extends Specification {

    def "'make' should make a page with a template"() {
        setup:
        def templateText = new File("src/test/resources/templates/simple-template.html").text
        def templateEngine = new TemplateEngine(templateText, [title: "Gaiden"])
        def binding = [
            content: "<h1>Hello</h1>"
        ]

        when:
        def content = templateEngine.make(binding)

        then:
        content == '''<html>
                     |<head>
                     |    <title>Gaiden</title>
                     |</head>
                     |<body>
                     |<h1>Hello</h1>
                     |</body>
                     |</html>
                     |'''.stripMargin()
    }

}
