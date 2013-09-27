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

package gaiden.markdown

import gaiden.Holders
import gaiden.message.MessageSource
import spock.lang.Specification

class ImageRendererSpec extends Specification {

    def savedSystemErr

    def setup() {
        savedSystemErr = System.err

        Holders.messageSource = new MessageSource()
    }

    def cleanup() {
        System.err = savedSystemErr
    }

    def "'render' should return a rendering object which has replaced image path"() {
        setup:
        def renderer = new ImageRenderer(pagePath, new File("non-existent-static"))

        and:
        def printStream = Mock(PrintStream)
        System.err = printStream

        when:
        def rendering = renderer.render(imagePath, "TEST_ALT")

        then:
        rendering.src == renderingSrc
        rendering.alt == "TEST_ALT"

        and: "output a warning message when a image file doesn't exist"
        1 * printStream.println("WARNING: '${imagePath}' in the '${pagePath}' refers to non-existent file")

        where:
        pagePath         | imagePath        | renderingSrc
        "test.html"      | "/img/test.png"  | "img/test.png"
        "test.html"      | "/test.png"      | "test.png"
        "path/test.html" | "/img/test.png"  | "../img/test.png"
        "path/test.html" | "/test.png"      | "../test.png"
        "path/test.html" | "/path/test.png" | "test.png"
    }

    def "'render' should not replace an image path if doesn't start with a slash"() {
        setup:
        def renderer = new ImageRenderer("test.html", new File("test/resources/static-files"))

        when:
        def rendering = renderer.render("img/test.png", "TEST_ALT")

        then:
        rendering.src == "img/test.png"
        rendering.alt == "TEST_ALT"
    }

    def "'render' should not output a warning message when a image file exists"() {
        setup:
        def renderer = new ImageRenderer("test.html", new File("src/test/resources/static-files"))

        and:
        def printStream = Mock(PrintStream)
        System.err = printStream

        when:
        def rendering = renderer.render(imagePath, "TEST_ALT")

        then:
        rendering.src == renderingSrc
        rendering.alt == "TEST_ALT"

        and:
        0 * printStream.println(_)

        where:
        imagePath           | renderingSrc
        "/images/dummy.png" | "images/dummy.png"
        "images/dummy.png"  | "images/dummy.png"
    }

    def "'render' should not output a warning message when a image path is URL"() {
        setup:
        def renderer = new ImageRenderer("test.html", new File("src/test/resources/static-files"))

        and:
        def printStream = Mock(PrintStream)
        System.err = printStream

        when:
        def rendering = renderer.render(imagePath, "TEST_ALT")

        then:
        rendering.src == imagePath
        rendering.alt == "TEST_ALT"

        and:
        0 * printStream.println(_)

        where:
        imagePath << [
            "http://www.example.com/test.png",
            "https://www.example.com/test.png",
            "ftp://www.example.com/test.png",
            "file:///path/to/test.png",
        ]
    }

}
