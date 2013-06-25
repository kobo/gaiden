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
        def builder = new DocumentBuilder(
            new File("src/test/resources/templates/simple-template.html"),
            new File("src/test/resources/toc/test-toc.groovy"),
            [title: "Gaiden"]
        )

        when:
        def document = builder.build(documentSource)

        then:
        document.pages.size() == 2

        and:
        document.pages*.path as Set == ["source1.html", "source2.html"] as Set

        and:
        document.pages*.content as Set == [
            """<html>
              |<head>
              |    <title>Gaiden</title>
              |</head>
              |<body>
              |<h1>markdown1</h1>
              |</body>
              |</html>
              |""".stripMargin(),
            """<html>
              |<head>
              |    <title>Gaiden</title>
              |</head>
              |<body>
              |<h1>markdown2</h1>
              |</body>
              |</html>
              |""".stripMargin(),
        ] as Set

        and:
        document.toc.content == """<html>
                                  |<head>
                                  |    <title>Gaiden</title>
                                  |</head>
                                  |<body>
                                  |<h1>Table of contents</h1>
                                  |<ul>
                                  |    <li>
                                  |        <a href="first-1">First 1</a>
                                  |    </li>
                                  |    <li>
                                  |        <a href="first-2">First 2</a>
                                  |    </li>
                                  |    <li>
                                  |        <a href="second">Second</a>
                                  |        <ul>
                                  |            <li>
                                  |                <a href="second-1">Second 1</a>
                                  |            </li>
                                  |            <li>
                                  |                <a href="second-2">Second 2</a>
                                  |            </li>
                                  |            <li>
                                  |                <a href="second-3">Second 3</a>
                                  |            </li>
                                  |            <li>
                                  |                <a href="third">Third</a>
                                  |                <ul>
                                  |                    <li>
                                  |                        <a href="third-1">Third 1</a>
                                  |                    </li>
                                  |                    <li>
                                  |                        <a href="third-2">Third 2</a>
                                  |                    </li>
                                  |                </ul>
                                  |            </li>
                                  |        </ul>
                                  |    </li>
                                  |</ul>
                                  |</body>
                                  |</html>
                                  |""".stripMargin()
    }

}
