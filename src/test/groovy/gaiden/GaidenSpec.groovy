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

import gaiden.context.BuildContext
import gaiden.message.MessageSource
import spock.lang.AutoCleanup
import spock.lang.Specification

abstract class GaidenSpec extends Specification {

    def savedSystemOut
    def savedSystemErr
    def savedSystemSecurityManager

    @AutoCleanup("delete")
    def tocFile = File.createTempFile("toc", "groovy")

    def setup() {
        saveSystemProperties()
        setupMessageSource()
        setupConfig()
    }

    def cleanup() {
        restoreSystemProperties()
    }

    GaidenConfig getConfig() {
        Holders.config
    }

    BuildContext createBuildContext(List<Map> pageSourceMaps) {
        def pageSources = pageSourceMaps.collect { new PageSource(it) }
        def documentSource = new DocumentSource(pageSources: pageSources)
        new BuildContext(documentSource: documentSource)
    }

    private setupConfig() {
        def configFile = new File("src/test/resources/config/ValidConfig.groovy")
        assert configFile.exists()
        Holders.config = new GaidenConfigLoader().load(configFile)
        Holders.config.tocFilePath = tocFile.canonicalPath
    }

    private saveSystemProperties() {
        savedSystemOut = System.out
        savedSystemErr = System.err
        savedSystemSecurityManager = System.securityManager
    }

    private void restoreSystemProperties() {
        System.out = savedSystemOut
        System.err = savedSystemErr
        System.securityManager = savedSystemSecurityManager
    }

    private setupMessageSource() {
        Holders.messageSource = new MessageSource()
    }
}
