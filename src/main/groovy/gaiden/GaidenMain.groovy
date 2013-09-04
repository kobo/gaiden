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

package gaiden

import gaiden.command.GaidenBuild
import gaiden.command.GaidenClean
import gaiden.command.GaidenCommand

/**
 * A command line to execute Gaiden.
 *
 * @author Hideki IGARASHI
 * @author Kazuki YAMAMOTO
 */
class GaidenMain {

    static final String USAGE_MESSAGE = """\
Usage: gaiden <command>

   commands:
       build  Build pages from source pages
       clean  Clean the build directory
"""

    private static final String CONFIG_FILE_NAME = "GaidenConfig.groovy"

    /**
     * A main command line interface.
     *
     * @param args all command line args
     */
    static void main(String... args) {
        new GaidenMain().run(args)
    }

    void run(String... args) {
        def gaidenConfig = new File(CONFIG_FILE_NAME)
        if (!gaidenConfig.exists()) {
            System.err.println("ERROR: Not a Gaiden project (Cannot find $CONFIG_FILE_NAME)")
            System.exit(1)
        }
        if (!args) {
            usage()
            System.exit(1)
        }

        Holders.config = new GaidenConfigLoader().load(gaidenConfig)

        executeCommand(args.first())
    }

    void executeCommand(String commandName) {
        GaidenCommand command = createCommand(commandName)
        command.execute()
    }

    private GaidenCommand createCommand(String commandName) {
        switch (commandName) {
            case "build":
                return new GaidenBuild()
            case "clean":
                return new GaidenClean()
            default:
                return [execute: {-> usage() }] as GaidenCommand
        }
    }

    private void usage() {
        println USAGE_MESSAGE
    }

}
