/*
 * Copyright 2014 the original author or authors
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

import java.nio.file.Path
import java.nio.file.Paths

abstract class GaidenSpec extends Specification {

    def savedSystemOut
    def savedSystemErr
    def savedSystemSecurityManager

    def setup() {
        saveSystemProperties()
        System.properties["app.home"] = rootProject.resolve("src/main/dist").toString()
    }

    def cleanup() {
        restoreSystemProperties()
    }

    private void saveSystemProperties() {
        savedSystemOut = System.out
        savedSystemErr = System.err
        savedSystemSecurityManager = System.securityManager
    }

    private void restoreSystemProperties() {
        System.out = savedSystemOut
        System.err = savedSystemErr
        System.securityManager = savedSystemSecurityManager
    }

    static Path getRootProject() {
        def userDirectory = System.properties["user.dir"] as String
        if (userDirectory.endsWith("gaiden")) {
            return Paths.get(userDirectory)
        }
        return Paths.get(userDirectory, "../")
    }

    static Path getProject() {
        def userDirectory = System.properties["user.dir"] as String
        if (userDirectory.endsWith("gaiden")) {
            return Paths.get(userDirectory, "gaiden-core")
        }
        return Paths.get(userDirectory)
    }
}
