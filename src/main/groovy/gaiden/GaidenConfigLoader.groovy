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

/**
 * Configuration loader of Gaiden.
 *
 * @author Hideki IGARASHI
 * @author Kazuki YAMAMOTO
 */
class GaidenConfigLoader {

    private static final String DEFAULT_CONFIG_PATH = "/DefaultConfig.groovy"

    /**
     * Loads configuration from file.
     *
     * @param configFile the configuration file
     * @return {@link GaidenConfig}'s instance which have the loaded values
     */
    GaidenConfig load(File configFile) {
        def config = initializeConfig(configFile)
        new GaidenConfig(config)
    }

    private ConfigObject initializeConfig(File configFile) {
        def slurper = new ConfigSlurper()

        def config = slurper.parse(GaidenConfigLoader.getResource(DEFAULT_CONFIG_PATH))
        if (configFile.exists()) {
            config.merge(slurper.parse(configFile.toURI().toURL()))
        }
        config
    }

}
