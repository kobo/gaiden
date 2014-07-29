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
import gaiden.context.PageBuildContext
import spock.lang.Specification

class DocumentBuilderSpec extends Specification {

    def "'build' should builds document"() {
        setup:
            def documentSource = new DocumentSource()
            documentSource.pageSources = [
                new PageSource(path: "source1.md", content: "# markdown1"),
                new PageSource(path: "source2.md", content: "# markdown2"),
            ]
            def context = new BuildContext(documentSource: documentSource)

        and:
            def pageBuilder = Mock(PageBuilder)
            def tocBuilder = Mock(TocBuilder)

        and:
            def builder = new DocumentBuilder()
            builder.tocBuilder = tocBuilder
            builder.pageBuilder = pageBuilder

        when:
            def document = builder.build(context)

        then:
            1 * tocBuilder.build(_ as BuildContext)
            2 * pageBuilder.build(_ as PageBuildContext, _ as PageSource)

        and:
            document
    }

}
