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

import gaiden.GaidenConfig
import gaiden.exception.GaidenException
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired

import java.nio.file.Files

@CompileStatic
abstract class AbstractCommand implements GaidenCommand {

    @Autowired
    GaidenConfig gaidenConfig

    void execute(List<String> arguments = []) {
        if (onlyGaidenProject && !Files.exists(gaidenConfig.gaidenConfigFile)) {
            throw new GaidenException("not.gaiden.project.error", [gaidenConfig.gaidenConfigFile])
        }

        def cliBuilder = new CliBuilder()
        def optionAccessor = cliBuilder.parse(arguments)
        execute(optionAccessor.arguments(), optionAccessor)
    }

    abstract void execute(List<String> arguments, OptionAccessor optionAccessor)
}
