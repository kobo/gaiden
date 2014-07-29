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

import gaiden.DocumentBuilder
import gaiden.DocumentWriter
import gaiden.SourceCollector
import spock.lang.Specification

class BuildCommandSpec extends Specification {

    def "'execute' should build a document"() {
        setup:
            def sourceCollector = Mock(SourceCollector)
            def documentBuilder = Mock(DocumentBuilder)
            def documentWriter = Mock(DocumentWriter)

        and:
            def command = new BuildCommand()
            command.sourceCollector = sourceCollector
            command.documentBuilder = documentBuilder
            command.documentWriter = documentWriter

        when:
            command.execute([], null)

        then:
            1 * sourceCollector.collect()
            1 * documentBuilder.build(_)
            1 * documentWriter.write(_)
    }

}
