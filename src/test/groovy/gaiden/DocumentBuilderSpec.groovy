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

import spock.lang.Specification

class DocumentBuilderSpec extends Specification {

    def "'build' should builds document"() {
        setup:
        def documentSource = new DocumentSource()
        documentSource.pageSources = [
            new PageSource(path: "source1.md", content: "# markdown1"),
            new PageSource(path: "source2.md", content: "# markdown2"),
        ]

        and:
        def builder = new DocumentBuilder()

        when:
        def document = builder.build(documentSource)

        then:
        document.pages.size() == 2

        and:
        document.pages*.path as Set == ["source1.html", "source2.html"] as Set

        and:
        document.pages*.content as Set == ["""
            |<html>
            |<head>
            |<title>Gaiden</title>
            |</head>
            |<body>
            |<h1>markdown1</h1>
            |</body>
            |</html>
            |""".stripMargin(),
            """
            |<html>
            |<head>
            |<title>Gaiden</title>
            |</head>
            |<body>
            |<h1>markdown2</h1>
            |</body>
            |</html>
            |""".stripMargin(),
        ] as Set
    }

}
