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

import gaiden.command.BuildCommand

class BuildCommandSpec extends FunctionalSpec {

    def "build a document"() {
        given:
        setupProjectDirectory("src/test/resources/test-project")
        def command = applicationContext.getBean(BuildCommand)

        when:
        command.execute()

        then:
        assertOutputDirectory "src/test/resources/test-project/build"
    }

    def "build a document without 'pages.groovy'"() {
        given:
        setupProjectDirectory("src/test/resources/without-pages")
        def command = applicationContext.getBean(BuildCommand)

        when:
        command.execute()

        then:
        assertOutputDirectory "src/test/resources/without-pages/build"
    }
}
