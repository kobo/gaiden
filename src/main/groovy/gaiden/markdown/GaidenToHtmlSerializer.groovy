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
import org.pegdown.VerbatimSerializer
import org.pegdown.ast.GaidenHeaderNode
import org.pegdown.ast.GaidenReferenceNode
import org.pegdown.ast.HeaderNode
import org.pegdown.ast.MarkdownInsideHtmlBlockNode
import org.pegdown.ast.RefImageNode
import org.pegdown.ast.RefLinkNode
import org.pegdown.ast.ReferenceNode
import org.pegdown.ast.SuperNode

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
        super(linkRenderer, [(VerbatimSerializer.DEFAULT): new GaidenVerbatimSerializer()] as Map<String, VerbatimSerializer>)
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
        if (headerNode instanceof GaidenHeaderNode) {
            def gaidenHeaderNode = headerNode as GaidenHeaderNode
            if (gaidenHeaderNode.specialAttributes?.classes) {
                printAttributes([class: gaidenHeaderNode.specialAttributes.classes.join(" ")])
            }
        }
        printer.print('>')
        if (gaidenConfig.numbering && header.numbers && header.level <= gaidenConfig.numberingDepth) {
            printer.print("<span class=\"number\">${header.number}</span>".toString())
        }
        visitChildren(headerNode)
        printer.print('<').print('/').print(tag).print('>');
    }

    @Override
    void visit(RefImageNode node) {
        String text = printChildrenToString(node)
        String key = node.referenceKey != null ? printChildrenToString(node.referenceKey) : text
        ReferenceNode refNode = references.get(normalize(key))
        if (refNode == null) { // "fake" reference image link
            printer.print("![").print(text).print(']')
            if (node.separatorSpace != null) {
                printer.print(node.separatorSpace).print('[')
                if (node.referenceKey != null) printer.print(key)
                printer.print(']')
            }
        } else {
            printImageTag(linkRenderer.render(node, refNode.getUrl(), refNode.getTitle(), text), refNode)
        }
    }

    @Override
    void visit(RefLinkNode node) {
        String text = printChildrenToString(node)
        String key = node.referenceKey != null ? printChildrenToString(node.referenceKey) : text
        ReferenceNode refNode = references.get(normalize(key))
        if (refNode == null) { // "fake" reference link
            printer.print('[').print(text).print(']')
            if (node.separatorSpace != null) {
                printer.print(node.separatorSpace).print('[')
                if (node.referenceKey != null) printer.print(key)
                printer.print(']')
            }
        } else {
            printLink(linkRenderer.render(node, refNode.getUrl(), refNode.getTitle(), text), refNode)
        }
    }

    @Override
    void visit(SuperNode node) {
        if (node instanceof MarkdownInsideHtmlBlockNode) {
            printMarkdownInsideHtmlBlock(node)
            return
        }
        super.visit(node)
    }

    private void printMarkdownInsideHtmlBlock(MarkdownInsideHtmlBlockNode node) {
        printer.print(node.beginTag.replaceAll(/ +markdown=(['"]?)(1|span|block)\1/, ""))
        visitChildren(node)
        printer.print(node.endTag)
    }

    @Override
    protected void printLink(LinkRenderer.Rendering rendering) {
        printLink(rendering, null)
    }

    protected void printLink(LinkRenderer.Rendering rendering, ReferenceNode referenceNode) {
        printer.print('<').print('a')
        printAttributes("href": rendering.href)
        printAttributes(rendering.attributes.collectEntries { LinkRenderer.Attribute attribute ->
            [(attribute.name): attribute.value]
        })
        if (referenceNode instanceof GaidenReferenceNode) {
            if (referenceNode.specialAttributesNode?.id) {
                printAttributes(id: referenceNode.specialAttributesNode.id)
            }
            if (referenceNode.specialAttributesNode?.classes) {
                printAttributes(class: referenceNode.specialAttributesNode.classes.join(" "))
            }
        }
        printer.print('>').print(rendering.text).print("</a>")
    }

    @Override
    protected void printImageTag(LinkRenderer.Rendering rendering) {
        printImageTag(rendering, null)
    }

    protected void printImageTag(LinkRenderer.Rendering rendering, ReferenceNode referenceNode) {
        def image = imageRenderer.render(rendering.href, rendering.text)
        printer.print("<img")
        printAttributes(src: image.src, alt: image.alt)
        printAttributes(rendering.attributes.collectEntries { LinkRenderer.Attribute attribute ->
            [(attribute.name): attribute.value]
        })
        if (referenceNode instanceof GaidenReferenceNode) {
            if (referenceNode.specialAttributesNode?.id) {
                printAttributes(id: referenceNode.specialAttributesNode.id)
            }
            if (referenceNode.specialAttributesNode?.classes) {
                printAttributes(class: referenceNode.specialAttributesNode.classes.join(" "))
            }
        }
        printer.print("/>")
    }

    private void printAttributes(Map<String, String> attributes) {
        attributes.each {
            printer.print(' ').print(it.key).print('=').print('"').print(it.value).print('"')
        }
    }
}
