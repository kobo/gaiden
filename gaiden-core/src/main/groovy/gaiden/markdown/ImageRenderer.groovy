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

import gaiden.Page
import gaiden.message.MessageSource
import gaiden.util.UrlUtils
import groovy.transform.CompileStatic

import java.nio.file.Files
import java.nio.file.LinkOption
import java.nio.file.Path

/**
 * A Renderer for image node.
 *
 * @author Hideki IGARASHI
 * @author Kazuki YAMAMOTO
 */
@CompileStatic
class ImageRenderer {

    private Page page
    private Path outputDirectory
    private MessageSource messageSource

    ImageRenderer(Page page, Path outputDirectory, MessageSource messageSource) {
        this.page = page
        this.outputDirectory = outputDirectory
        this.messageSource = messageSource
    }

    /**
     * Simple model class for rendering an img element.
     */
    static class ImageElement {
        String src
        String alt
    }

    /**
     * Returns a rendering object which has an image path replaced with an appropriate relative path.
     * <p>
     * An original image path is used without a replacement if it doesn't start with a slash or it is URL.
     * <p>
     * Also outputs a warning message when an image file doesn't exist.
     *
     * @param imagePath the path of image
     * @param alt the alternate text
     * @return {@link ImageElement}'s instance
     */
    ImageElement render(String imagePath, String alt) {
        if (UrlUtils.isUrl(imagePath) || imagePath.startsWith('/')) {
            return new ImageElement(src: imagePath, alt: alt)
        }

        def image = page.source.path.parent.resolve(imagePath)
        if (Files.notExists(image)) {
            // It is required to normalize path such as '/path/to/../images/sample.png'.
            System.err.println("WARNING: " + messageSource.getMessage("image.reference.not.exists.message", [imagePath, page.source.path]))
            return new ImageElement(src: imagePath, alt: alt)
        }
        return new ImageElement(src: page.source.path.parent.relativize(image.toRealPath(LinkOption.NOFOLLOW_LINKS)).toString(), alt: alt)
    }
}
