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

import org.pegdown.Printer
import org.pegdown.ast.ExpImageNode
import org.pegdown.ast.TextNode
import spock.lang.Specification

class GaidenToHtmlSerializerSpec extends Specification {

    def "'printImageTag' should print a img element"() {
        setup:
        def url = "TEST_URL"
        def alt = "TEST_ALT"

        and:
        def imageRenderer = Mock(ImageRenderer)
        1 * imageRenderer.render(url, alt) >> new ImageRenderer.ImageElement(src: url, alt: alt)

        and:
        def printer = new Printer()

        and:
        def expImageNode = new ExpImageNode("", url, new TextNode(alt))
        def serializer = new GaidenToHtmlSerializer(null, imageRenderer, printer)

        when:
        serializer.printImageTag(expImageNode, url)

        then:
        printer.sb.toString() == '<img src="TEST_URL" alt="TEST_ALT"/>'
    }

}
