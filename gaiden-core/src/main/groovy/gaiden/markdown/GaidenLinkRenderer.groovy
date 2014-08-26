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

import gaiden.Document
import gaiden.Page
import gaiden.SourceCollector
import gaiden.message.MessageSource
import gaiden.util.UrlUtils
import groovy.transform.CompileStatic
import org.apache.commons.io.FilenameUtils
import org.pegdown.LinkRenderer
import org.pegdown.ast.ExpImageNode
import org.pegdown.ast.ExpLinkNode
import org.pegdown.ast.GaidenExpImageNode
import org.pegdown.ast.GaidenExpLinkNode
import org.pegdown.ast.RefLinkNode
import org.pegdown.ast.SpecialAttributesNode

import java.nio.file.Files

import static org.pegdown.FastEncoder.*

/**
 * A Renderer for link node.
 *
 * @author Kazuki YAMAMOTO
 * @author Hideki IGARASHI
 */
@CompileStatic
class GaidenLinkRenderer extends LinkRenderer {

    Page page
    Document document
    MessageSource messageSource

    @Override
    LinkRenderer.Rendering render(ExpLinkNode node, String text) {
        def rendering = this.render(node.url, node.title, text)
        if (node instanceof GaidenExpLinkNode) {
            withSpecialAttributes(rendering, node.specialAttributesNode)
        }
        rendering
    }

    @Override
    LinkRenderer.Rendering render(ExpImageNode node, String text) {
        def rendering = super.render(node, text)
        if (node instanceof GaidenExpImageNode) {
            withSpecialAttributes(rendering, node.specialAttributesNode)
        }
        rendering
    }

    @Override
    LinkRenderer.Rendering render(RefLinkNode node, String url, String title, String text) {
        this.render(url, title, text)
    }

    protected void withSpecialAttributes(LinkRenderer.Rendering rendering, SpecialAttributesNode specialAttributesNode) {
        if (specialAttributesNode.id) {
            rendering.withAttribute("id", specialAttributesNode.id)
        }
        if (specialAttributesNode.classes) {
            rendering.withAttribute("class", specialAttributesNode.classes.join(" "))
        }
    }

    protected LinkRenderer.Rendering render(String url, String title, String text) {
        if (UrlUtils.isUrl(url)) {
            return createRendering(url, title, text)
        }

        def parts = url.split("#")
        def path = parts[0]
        if (!(FilenameUtils.getExtension(path) in SourceCollector.PAGE_SOURCE_EXTENSIONS)) {
            return createRendering(url, title, text)
        }

        def filePath = page.source.path.parent.resolve(path)
        if (Files.notExists(filePath)) {
            System.err.println("WARNING: " + messageSource.getMessage("output.page.reference.not.exists.message", [url, page.source.path]))
            return createRendering(url, title, text)
        }

        def destPage = document.pages.find {
            Files.isSameFile(it.source.path, filePath)
        }

        return createRendering("${page.relativize(destPage)}${parts.size() > 1 ? "#${parts[1]}" : ""}", title, text)
    }

    private LinkRenderer.Rendering createRendering(String url, String title, String text) {
        LinkRenderer.Rendering rendering = new LinkRenderer.Rendering(url, text)
        return title ? rendering.withAttribute("title", encode(title)) : rendering
    }
}
