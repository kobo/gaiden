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
        def renderer = new ImageRenderer("/path/output/test.html", new File("outputDirectory"))

        when:
        def rendering = renderer.render("/path/img/test.png", "TEST_ALT")

        then:
        rendering.src == "../img/test.png"
        rendering.alt == "TEST_ALT"
    }

    def "'render' should not replace an image path if doesn't start with a slash"() {
        setup:
        def renderer = new ImageRenderer("/path/output/test.html", new File("outputDirectory"))

        when:
        def rendering = renderer.render("path/img/test.png", "TEST_ALT")

        then:
        rendering.src == "path/img/test.png"
        rendering.alt == "TEST_ALT"
    }

}
