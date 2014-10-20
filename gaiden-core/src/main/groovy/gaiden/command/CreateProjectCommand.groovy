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
import gaiden.util.PathUtils
import groovy.io.FileType
import groovy.transform.CompileStatic
import org.apache.commons.cli.CommandLine
import org.springframework.stereotype.Component

import java.nio.file.FileSystems
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

    @Override
    void execute(CommandLine commandLine) {
        if (!commandLine.args) {
            throw new GaidenException(getMessage('command.create.project.name.required.error', [fullUsage]))
        }

        def projectDirectory = gaidenConfig.projectDirectory.resolve(commandLine.args.first())
        if (Files.exists(projectDirectory)) {
            throw new GaidenException(getMessage('command.create.project.already.exists.error', [projectDirectory, fullUsage]))
        }

        PathUtils.copyFiles(gaidenConfig.applicationInitialProjectTemplateDirectory, projectDirectory)
        PathUtils.copyFiles(gaidenConfig.applicationWrapperDirectory, projectDirectory)

        if ("posix" in FileSystems.default.supportedFileAttributeViews()) {
            projectDirectory.eachFileMatch(FileType.FILES, ~/gaidenw(.bat)?/) { Path gaidenw ->
                Files.setPosixFilePermissions(gaidenw, PosixFilePermissions.fromString("rwxr-xr-x"))
            }
        }
        println getMessage("command.create.project.success.message", [projectDirectory])
    }
}
