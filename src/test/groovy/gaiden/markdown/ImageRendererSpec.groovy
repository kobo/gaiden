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

import spock.lang.Specification

class ImageRendererSpec extends Specification {

    def "'render' should return a rendering object which has replaced image path"() {
        setup:
        def renderer = new ImageRenderer(pagePath)

        when:
        def rendering = renderer.render(imageUrl, "TEST_ALT")

        then:
        rendering.src == imageSrc
        rendering.alt == "TEST_ALT"

        where:
        pagePath         | imageUrl         | imageSrc
        "test.html"      | "/img/test.png"  | "img/test.png"
        "test.html"      | "/test.png"      | "test.png"
        "path/test.html" | "/img/test.png"  | "../img/test.png"
        "path/test.html" | "/test.png"      | "../test.png"
        "path/test.html" | "/path/test.png" | "test.png"
    }

    def "'render' should not replace an image path if doesn't start with a slash"() {
        setup:
        def renderer = new ImageRenderer("test.html")

        when:
        def rendering = renderer.render("img/test.png", "TEST_ALT")

        then:
        rendering.src == "img/test.png"
        rendering.alt == "TEST_ALT"
    }

}
