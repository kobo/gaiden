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

/**
 * The 'create-project' command.
 *
 * @author Hideki IGARASHI
 * @author Kazuki YAMAMOTO
 */
class GaidenCreateProject implements GaidenCommand {

    private List args
    private String appHome
    private String outputDirectory

    GaidenCreateProject(List args, String appHome = System.properties["app.home"], String outputDirectory = ".") {
        this.args = args
        this.appHome = appHome
        this.outputDirectory = outputDirectory
    }

    /**
     * Executes creating project.
     */
    @Override
    void execute() {
        if (args.empty) {
            System.err.println "ERROR: Project name is required"
            System.err.println "Usage: gaiden create-project <project name>"
            System.exit(1)
        }

        def projectDirectory = new File(outputDirectory, args.first() as String)
        if (projectDirectory.exists()) {
            System.err.println "ERROR: The Gaiden project already exists: ${projectDirectory.name}"
            System.exit(1)
        }

        new AntBuilder().copy(toDir: projectDirectory) {
            fileset(dir: "$appHome/template", defaultexcludes: "no")
        }
    }

    @Override
    boolean isOnlyGaidenProject() {
        false
    }

}
