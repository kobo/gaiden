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

package gaiden.command

import groovy.transform.CompileStatic
import org.apache.commons.cli.CommandLine
import org.springframework.stereotype.Component

/**
 * The 'clean' command.
 *
 * @author Hideki IGARASHI
 * @author Kazuki YAMAMOTO
 */
@Component
@CompileStatic
class CleanCommand extends AbstractCommand {

    final String name = "clean"

    final boolean onlyGaidenProject = true

    /**
     * Executes cleaning.
     */
    @Override
    void execute(CommandLine commandLine) {
        gaidenConfig.outputDirectory.deleteDir()
        gaidenConfig.projectDirectory.resolve(gaidenConfig.distFileName + ".zip").toFile().delete()
        println getMessage("command.clean.success.message")
    }
}
