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

import gaiden.PageReference
import gaiden.PageSource
import gaiden.context.PageBuildContext
import gaiden.message.MessageSource
import gaiden.util.FileUtils
import org.pegdown.LinkRenderer
import org.pegdown.ast.ExpLinkNode
import org.pegdown.ast.RefLinkNode

import static org.pegdown.FastEncoder.*

/**
 * A Renderer for link node.
 *
 * @author Kazuki YAMAMOTO
 * @author Hideki IGARASHI
 */
class GaidenLinkRenderer extends LinkRenderer {

    private PageBuildContext context
    private PageSource pageSource
    private File pagesDirectory
    private MessageSource messageSource

    GaidenLinkRenderer(PageBuildContext context, PageSource pageSource, File pagesDirectory, MessageSource messageSource) {
        this.context = context
        this.pageSource = pageSource
        this.pagesDirectory = pagesDirectory
        this.messageSource = messageSource
    }

    @Override
    LinkRenderer.Rendering render(ExpLinkNode node, String text) {
        this.render(node.url, node.title, text)
    }

    @Override
    LinkRenderer.Rendering render(RefLinkNode node, String url, String title, String text) {
        this.render(url, title, text)
    }

    protected LinkRenderer.Rendering render(String url, String title, String text) {
        def pageRef = new PageReference(url)

        if (pageRef.isUrl() || !pageRef.isPageSourceExtension()) {
            return createRendering(url, title, text)
        }

        def targetPageSource = findPageSource(pageRef)
        if (!targetPageSource) {
            System.err.println("WARNING: " + messageSource.getMessage("output.page.reference.not.exists.message", [url, pageSource.path]))
            return createRendering(url, title, text)
        }

        def relativeUrl = FileUtils.getRelativePathForFileToFile(pageSource.outputPath, targetPageSource.outputPath)
        return createRendering(relativeUrl + pageRef.fragment, title, text)
    }

    private PageSource findPageSource(PageReference pageRef) {
        if (pageRef.isAbsolutePath()) {
            def pageSourceParentDirectory = new File(pagesDirectory, pageSource.path).parentFile
            def fullPathPageRef = pageRef.toFullPathPageReference(pagesDirectory, pageSourceParentDirectory)
            context.documentSource.findPageSource(fullPathPageRef)
        } else {
            context.documentSource.findPageSource(pageRef)
        }
    }

    private LinkRenderer.Rendering createRendering(String url, String title, String text) {
        LinkRenderer.Rendering rendering = new LinkRenderer.Rendering(url, text)
        return title ? rendering.withAttribute("title", encode(title)) : rendering
    }

}
