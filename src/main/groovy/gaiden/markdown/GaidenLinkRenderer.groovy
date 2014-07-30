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

import gaiden.PageReferenceFactory
import gaiden.PageSource
import gaiden.SourceCollector
import gaiden.context.PageBuildContext
import gaiden.message.MessageSource
import gaiden.util.UrlUtils
import groovy.transform.CompileStatic
import org.apache.commons.io.FilenameUtils
import org.pegdown.LinkRenderer
import org.pegdown.ast.ExpLinkNode
import org.pegdown.ast.RefLinkNode

import java.nio.file.Path
import java.nio.file.Paths

import static org.pegdown.FastEncoder.*

/**
 * A Renderer for link node.
 *
 * @author Kazuki YAMAMOTO
 * @author Hideki IGARASHI
 */
@CompileStatic
class GaidenLinkRenderer extends LinkRenderer {

    private PageBuildContext context
    private PageSource pageSource
    private Path pagesDirectory
    private MessageSource messageSource
    private PageReferenceFactory pageReferenceFactory

    GaidenLinkRenderer(PageBuildContext context, PageSource pageSource, Path pagesDirectory, MessageSource messageSource, PageReferenceFactory pageReferenceFactory) {
        this.context = context
        this.pageSource = pageSource
        this.pagesDirectory = pagesDirectory
        this.messageSource = messageSource
        this.pageReferenceFactory = pageReferenceFactory
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
        if (UrlUtils.isUrl(url)) {
            return createRendering(url, title, text)
        }

        def pageReference = pageReferenceFactory.get(Paths.get(url), pageSource)
        def targetPageSource = context.documentSource.findPageSource(pageReference)
        if (!targetPageSource) {
            if (isPageSourceExtension(pageReference.path.toString())) {
                System.err.println("WARNING: " + messageSource.getMessage("output.page.reference.not.exists.message", [url, pageSource.path]))
            }

            return createRendering(url, title, text)
        }

        def relativePath = pageSource.outputPath.parent.relativize(targetPageSource.outputPath).toString()
        return createRendering("${relativePath}${pageReference.hash}", title, text)
    }

    private LinkRenderer.Rendering createRendering(String url, String title, String text) {
        LinkRenderer.Rendering rendering = new LinkRenderer.Rendering(url, text)
        return title ? rendering.withAttribute("title", encode(title)) : rendering
    }

    /**
     * Checks the extension of filename is a page source extension.
     *
     * @return {@code true} if the extension of filename is a page source extension
     * @see SourceCollector#PAGE_SOURCE_EXTENSIONS
     */
    private static boolean isPageSourceExtension(String path) {
        def extension = FilenameUtils.getExtension(path)

        !extension || extension in SourceCollector.PAGE_SOURCE_EXTENSIONS
    }
}
