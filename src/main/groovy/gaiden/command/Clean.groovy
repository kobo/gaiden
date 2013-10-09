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

import gaiden.Holders

/**
 * The 'clean' command.
 *
 * @author Hideki IGARASHI
 * @author Kazuki YAMAMOTO
 */
class Clean implements GaidenCommand {

    private File targetDirectory

    final boolean onlyGaidenProject = true

    Clean(targetDirectory = Holders.config.outputDirectory) {
        this.targetDirectory = targetDirectory
    }

    /**
     * Executes cleaning.
     */
    @Override
    void execute(List args = []) {
        new AntBuilder().delete(dir: targetDirectory)
    }

}
