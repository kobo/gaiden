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
import gaiden.DocumentWriter
import gaiden.SourceCollector
import gaiden.context.BuildContext
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

/**
 * The 'build' command.
 *
 * @author Hideki IGARASHI
 * @author Kazuki YAMAMOTO
 */
@Component
@CompileStatic
class BuildCommand extends AbstractCommand {

    final String name = "build"

    final boolean onlyGaidenProject = true

    @Autowired
    SourceCollector sourceCollector

    @Autowired
    DocumentBuilder documentBuilder

    @Autowired
    DocumentWriter documentWriter

    /**
     * Executes building.
     */
    @Override
    void execute(List<String> arguments, OptionAccessor optionAccessor) {
        def documentSource = sourceCollector.collect()
        def context = new BuildContext(documentSource: documentSource)
        Document document = documentBuilder.build(context)
        documentWriter.write(document)
    }
}
