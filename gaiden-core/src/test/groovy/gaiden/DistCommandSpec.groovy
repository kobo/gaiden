/*
 * Copyright 2017 the original author or authors
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

import gaiden.command.DistCommand

import java.nio.file.Files
import java.nio.file.Path

class DistCommandSpec extends FunctionalSpec {

    def "make a zipped archive"() {
        given:
            setupProjectDirectory("src/test/resources/test-project")
            def command = applicationContext.getBean(DistCommand)

        when:
            command.execute()

        then:
            Path distFile = gaidenConfig.projectDirectory.resolve(GaidenConfig.DEFAULT_DIST_FILE_NAME + ".zip")
            Files.exists(distFile)

        cleanup:
            distFile.toFile().delete()
    }
}
