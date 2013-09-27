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

/**
 * The 'create-project' command.
 *
 * @author Hideki IGARASHI
 * @author Kazuki YAMAMOTO
 */
class CreateProject implements GaidenCommand {

    private String appHome
    private String outputDirectoryPath

    final boolean onlyGaidenProject = false

    CreateProject(String appHome = System.properties["app.home"], String outputDirectoryPath = ".") {
        this.appHome = appHome
        this.outputDirectoryPath = outputDirectoryPath
    }

    /**
     * Executes creating project.
     */
    @Override
    void execute(List args = []) {
        if (args.empty) {
            throw new GaidenException("command.create.project.name.required.error")
        }

        def projectDirectory = new File(outputDirectoryPath, args.first() as String)
        if (projectDirectory.exists()) {
            throw new GaidenException("command.create.project.already.exists.error", [projectDirectory.name])
        }

        new AntBuilder().copy(toDir: projectDirectory) {
            fileset(dir: "$appHome/template", defaultexcludes: "no")
        }
    }

}
