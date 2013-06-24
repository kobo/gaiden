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

import gaiden.config.DefaultConfig

class ConfigLoader {

    File configFile

    ConfigLoader(File configFile) {
        this.configFile = configFile
    }

    void load() {
        def config = loadConfig()
        bindConfigurations(config)
    }

    private void bindConfigurations(ConfigObject config) {
        def gaidenConfig = GaidenConfig.instance
        config.each { String key, value ->
            gaidenConfig[key] = value
        }
    }

    private ConfigObject loadConfig() {
        def slurper = new ConfigSlurper()

        def config = slurper.parse(DefaultConfig)
        if (configFile.exists()) {
            config.merge(slurper.parse(configFile.toURI().toURL()))
        }
        config
    }

}
