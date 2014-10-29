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

package gaiden.command

import gaiden.exception.GaidenException
import gaiden.util.PathUtils
import groovy.transform.CompileStatic
import org.apache.commons.cli.CommandLine
import org.springframework.stereotype.Component

import java.nio.file.Files

@Component
@CompileStatic
class InstallThemeCommand extends AbstractCommand {

    final String name = "install-theme"

    final boolean onlyGaidenProject = true

    @Override
    void execute(CommandLine commandLine) {
        if (!commandLine.args) {
            throw new GaidenException(getMessage('command.install.theme.name.required.error', [fullUsage]))
        }

        def theme = commandLine.args.first()
        def themeDirectory = gaidenConfig.getApplicationThemeDirectory(theme)
        if (Files.notExists(themeDirectory)) {
            throw new GaidenException(getMessage('command.install.theme.not.found.error', [theme, fullUsage]))
        }

        PathUtils.copyFiles(themeDirectory, gaidenConfig.getProjectThemeDirectory(theme), true)
        println getMessage("command.install.theme.success.message", [gaidenConfig.getProjectThemeDirectory(theme)])
    }

    @Override
    String getFullUsage() {
        "${super.getFullUsage()}\n${getMessage('command.install.theme.themes.message', [gaidenConfig.installedThemes.join(",")])}"
    }
}