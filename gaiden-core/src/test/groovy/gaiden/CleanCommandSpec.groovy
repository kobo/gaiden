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

import gaiden.command.CleanCommand

import java.nio.file.Files

class CleanCommandSpec extends FunctionalSpec {

    def "'execute' should clean the build directory"() {
        given:
            def command = applicationContext.getBean(CleanCommand)
            assert Files.exists(outputDirectory)

        when:
            command.execute()

        then:
            Files.notExists(outputDirectory)
    }
}
