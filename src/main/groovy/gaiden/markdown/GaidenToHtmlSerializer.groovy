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

import groovy.transform.CompileStatic
import org.pegdown.LinkRenderer
import org.pegdown.Printer
import org.pegdown.ToHtmlSerializer
import org.pegdown.ast.SuperNode

/**
 * A Serializer for rendering nodes to HTML.
 *
 * @author Hideki IGARASHI
 * @author Kazuki YAMAMOTO
 */
@CompileStatic
class GaidenToHtmlSerializer extends ToHtmlSerializer {

    private ImageRenderer imageRenderer

    GaidenToHtmlSerializer(LinkRenderer linkRenderer, ImageRenderer imageRenderer) {
        super(linkRenderer)
        this.imageRenderer = imageRenderer
    }

    GaidenToHtmlSerializer(LinkRenderer linkRenderer, ImageRenderer imageRenderer, Printer printer) {
        this(linkRenderer, imageRenderer)
        this.printer = printer
    }

    protected void printImageTag(SuperNode imageNode, String url) {
        def rendering = imageRenderer.render(url, printChildrenToString(imageNode))
        printer
            .print('<img src="')
            .print(rendering.src)
            .print('" alt="')
            .printEncoded(rendering.alt)
            .print('"/>')
    }

}
