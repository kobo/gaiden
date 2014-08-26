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
import gaiden.message.MessageSource
import gaiden.util.PathUtils
import groovy.io.FileType
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import java.nio.file.Files
import java.nio.file.Path

@Component
@CompileStatic
class InstallThemeCommand extends AbstractCommand {

    final String name = "install-theme"

    final boolean onlyGaidenProject = true

    @Autowired
    MessageSource messageSource

    @Override
    void execute(List<String> arguments, OptionAccessor optionAccessor) {
        if (arguments.empty) {
            throw new GaidenException("command.install.theme.name.required.error", [gaidenConfig.installedThemes.join(",")])
        }

        def theme = arguments.first()
        def themeDirectory = gaidenConfig.getApplicationThemeDirectory(theme)
        if (Files.notExists(themeDirectory)) {
            throw new GaidenException("command.install.theme.not.found.error", [theme, gaidenConfig.installedThemes.join(",")])
        }

        def overwriteAll = false
        themeDirectory.eachFileRecurse(FileType.FILES) { Path src ->
            def relativePath = gaidenConfig.applicationThemesDirectory.relativize(src)
            def dest = gaidenConfig.projectThemesDirectory.resolve(relativePath)
            if (!overwriteAll && Files.exists(dest)) {
                def answer = getUserAnswer(relativePath)
                if (answer == "no") {
                    return
                } else if (answer == "all") {
                    overwriteAll = true
                }
            }
            PathUtils.copyFile(src, dest)
        }

        println messageSource.getMessage("command.install.theme.success.message", [gaidenConfig.getProjectThemeDirectory(theme)])
    }

    private String getUserAnswer(Path path) {
        print messageSource.getMessage("command.install.theme.overwrite.confirmation.message", [path])
        def scanner = new Scanner(System.in)
        while (true) {
            def answer = scanner.nextLine().toLowerCase()
            switch (answer) {
                case ["yes", "y"]:
                    return "yes"
                case ["no", "n"]:
                    return "no"
                case ["all", "a"]:
                    return "all"
                default:
                    print messageSource.getMessage("command.install.theme.overwrite.invalid.answer.error")
            }
        }
    }
}
