/*
 * Copyright 2014. the original author or authors
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

import gaiden.command.InstallThemeCommand
import gaiden.exception.GaidenException

class InstallThemeCommandSpec extends FunctionalSpec {

    def "install with a valid theme name"() {
        given:
        def theme = "default"
        def command = applicationContext.getBean(InstallThemeCommand)

        when:
        command.execute([theme])

        then:
        assertDirectory(gaidenConfig.getProjectThemeDirectory(theme), gaidenConfig.getApplicationThemeDirectory(theme))
    }

    def "install without a theme name"() {
        given:
        def command = applicationContext.getBean(InstallThemeCommand)

        when:
        command.execute([])

        then:
        thrown(GaidenException)
    }

    def "install with an invalid theme name"() {
        given:
        def theme = "invalid theme"
        def command = applicationContext.getBean(InstallThemeCommand)

        when:
        command.execute([theme])

        then:
        thrown(GaidenException)
    }
}
