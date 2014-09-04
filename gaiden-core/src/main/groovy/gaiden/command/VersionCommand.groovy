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

package gaiden.command

import groovy.transform.CompileStatic
import org.springframework.stereotype.Component

@Component
@CompileStatic
class VersionCommand extends AbstractCommand {

    final String name = "version"

    final boolean onlyGaidenProject = false

    @Override
    void execute(List<String> arguments, OptionAccessor optionAccessor) {
        def properties = new Properties()
        getClass().getResourceAsStream("/build-receipt.properties").withStream { InputStream stream ->
            properties.load(stream)
        }
        println """\
             _     _
            (_)   | |
  __ _  __ _ _  __| | ___ _ __
 / _` |/ _` | |/ _` |/ _ \\ '_ \\
| (_| | (_| | | (_| |  __/ | | |
 \\__, |\\__,_|_|\\__,_|\\___|_| |_|
  __/ |
 |___/

Version    : ${properties.version}
Revision   : ${properties.revision}
Build date : ${properties.buildDate}

OS         : ${System.properties["os.name"]} ${System.properties["os.version"]}
JVM        : ${System.properties["java.version"]}
Groovy     : ${GroovySystem.getVersion()}\
"""
    }
}
