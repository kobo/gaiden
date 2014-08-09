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

import gaiden.GaidenConfig
import gaiden.Page
import groovy.transform.CompileStatic
import org.pegdown.LinkRenderer
import org.pegdown.ToHtmlSerializer
import org.pegdown.ast.HeaderNode

/**
 * A Serializer for rendering nodes to HTML.
 *
 * @author Hideki IGARASHI
 * @author Kazuki YAMAMOTO
 */
@CompileStatic
class GaidenToHtmlSerializer extends ToHtmlSerializer {

    private GaidenConfig gaidenConfig
    private ImageRenderer imageRenderer
    private Page page

    GaidenToHtmlSerializer(GaidenConfig gaidenConfig, LinkRenderer linkRenderer, ImageRenderer imageRenderer, Page page) {
        super(linkRenderer)
        this.gaidenConfig = gaidenConfig
        this.imageRenderer = imageRenderer
        this.page = page
    }

    @Override
    void visit(HeaderNode headerNode) {
        def header = page.headers.find { it.headerNode == headerNode }
        def tag = "h${header.level}".toString()
        printer.print('<').print(tag)
        printAttributes([id: header.hash])
        printer.print('>')
        if (gaidenConfig.numbering && header.numbers && header.level <= gaidenConfig.numberingDepth) {
            printer.print("<span class=\"number\">${header.number}</span>".toString())
        }
        visitChildren(headerNode)
        printer.print('<').print('/').print(tag).print('>');
    }

    @Override
    protected void printImageTag(LinkRenderer.Rendering rendering) {
        def image = imageRenderer.render(rendering.href, rendering.text)
        printer.print("<img")
        printAttributes(src: image.src, alt: image.alt)
        printAttributes(rendering.attributes.collectEntries { LinkRenderer.Attribute attribute ->
            [(attribute.name): attribute.value]
        })
        printer.print("/>")
    }

    private void printAttributes(Map<String, String> attributes) {
        attributes.each {
            printer.print(' ').print(it.key).print('=').print('"').print(it.value).print('"')
        }
    }
}
