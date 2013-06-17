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

import gaiden.Document
import gaiden.DocumentBuilder
import gaiden.DocumentSource
import gaiden.DocumentWriter
import gaiden.SourceCollector
/**
 * The 'build' command.
 *
 * @author Hideki IGARASHI
 * @author Kazuki YAMAMOTO
 */
class GaidenBuild {

    SourceCollector sourceCollector
    DocumentBuilder documentBuilder
    DocumentWriter documentWriter

    GaidenBuild() {
        this.sourceCollector = new SourceCollector()
        this.documentBuilder = new DocumentBuilder()
        this.documentWriter = new DocumentWriter()
    }

    /**
     * Executes building.
     */
    void execute() {
        DocumentSource documentSource = sourceCollector.collect()
        Document document = documentBuilder.build(documentSource)
        documentWriter.write(document)
    }

}
