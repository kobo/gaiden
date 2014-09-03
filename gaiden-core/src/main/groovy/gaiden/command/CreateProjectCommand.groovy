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

import gaiden.exception.GaidenException
import gaiden.message.MessageSource
import gaiden.util.PathUtils
import groovy.io.FileType
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.attribute.PosixFilePermissions

/**
 * The 'create-project' command.
 *
 * @author Hideki IGARASHI
 * @author Kazuki YAMAMOTO
 */
@Component
@CompileStatic
class CreateProjectCommand extends AbstractCommand {

    final String name = "create-project"

    final boolean onlyGaidenProject = false

    @Autowired
    MessageSource messageSource

    @Override
    void execute(List<String> arguments, OptionAccessor optionAccessor) {
        if (arguments.empty) {
            throw new GaidenException("command.create.project.name.required.error")
        }

        def projectDirectory = gaidenConfig.projectDirectory.resolve(arguments.first())
        if (Files.exists(projectDirectory)) {
            throw new GaidenException("command.create.project.already.exists.error", [projectDirectory])
        }

        PathUtils.copyFiles(gaidenConfig.applicationInitialProjectTemplateDirectory, projectDirectory)
        PathUtils.copyFiles(gaidenConfig.applicationWrapperDirectory, projectDirectory)
        projectDirectory.eachFileMatch(FileType.FILES, ~/gaidenw(.bat)?/) { Path gaidenw ->
            Files.setPosixFilePermissions(gaidenw, PosixFilePermissions.fromString("rwxr-xr-x"))
        }
        println messageSource.getMessage("command.create.project.success.message", [projectDirectory])
    }
}
