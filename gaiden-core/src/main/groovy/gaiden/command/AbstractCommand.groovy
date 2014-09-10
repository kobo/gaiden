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
import gaiden.message.MessageSource
import groovy.transform.CompileStatic
import org.apache.commons.cli.BasicParser
import org.apache.commons.cli.CommandLine
import org.apache.commons.cli.HelpFormatter
import org.apache.commons.cli.Options
import org.apache.commons.cli.ParseException
import org.apache.commons.lang3.BooleanUtils
import org.springframework.beans.factory.annotation.Autowired

import java.nio.file.Files

@CompileStatic
abstract class AbstractCommand implements GaidenCommand, UsageAwareCommand {

    @Autowired
    GaidenConfig gaidenConfig

    @Autowired
    MessageSource messageSource

    @Override
    String getDescription() {
        getMessage("command.${name}.description")
    }

    @Override
    String getUsage() {
        getMessage("command.${name}.usage")
    }

    @Override
    String getFullUsage() {
        def formatter = new HelpFormatter()
        formatter.syntaxPrefix = getMessage('usage', [''])
        def writer = new StringWriter()
        formatter.printHelp(new PrintWriter(writer), HelpFormatter.DEFAULT_WIDTH, usage, null, options, HelpFormatter.DEFAULT_LEFT_PAD, HelpFormatter.DEFAULT_DESC_PAD, null, true)
        return writer.toString()
    }

    void execute(List<String> arguments = []) {
        def parser = new BasicParser()
        try {
            def commandLine = parser.parse(options, arguments as String[])
            if (commandLine.hasOption('non-interactive')) {
                gaidenConfig.interactive = false
            }

            if (!continues()) {
                return
            }

            execute(commandLine)
        } catch (ParseException e) {
            throw new GaidenException(fullUsage, e)
        }
    }

    protected Options getOptions() {
        return new Options().addOption(null, 'non-interactive', false, 'Non-Interactive Mode')
    }

    protected String getMessage(String code, List<?> args = []) {
        messageSource.getMessage(code, args)
    }

    abstract void execute(CommandLine commandLine)

    /**
     * Should a command be executed in the Gaiden project directory?
     *
     * @return True if a command should be executed in the Gaiden project directory
     */
    abstract boolean isOnlyGaidenProject()

    private boolean continues() {
        if (!onlyGaidenProject || Files.exists(gaidenConfig.gaidenConfigFile) || !gaidenConfig.interactive) {
            return true
        }

        print messageSource.getMessage("not.gaiden.project.confirmation.message", [gaidenConfig.gaidenConfigFile])
        return BooleanUtils.toBoolean(new Scanner(System.in).nextLine())
    }
}
