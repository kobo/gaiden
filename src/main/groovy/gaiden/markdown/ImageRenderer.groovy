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

import gaiden.PageSource
import gaiden.message.MessageSource
import gaiden.util.UrlUtils
import groovy.transform.CompileStatic

import java.nio.file.Files
import java.nio.file.LinkOption
import java.nio.file.Path
import java.nio.file.Paths

/**
 * A Renderer for image node.
 *
 * @author Hideki IGARASHI
 * @author Kazuki YAMAMOTO
 */
@CompileStatic
class ImageRenderer {

    private PageSource pageSource
    private Path staticDirectory
    private Path outputDirectory
    private MessageSource messageSource

    ImageRenderer(PageSource pageSource, Path staticDirectory, Path outputDirectory, MessageSource messageSource) {
        this.pageSource = pageSource
        this.staticDirectory = staticDirectory
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
        if (UrlUtils.isUrl(imagePath)) {  // URL
            return new ImageElement(src: imagePath, alt: alt)
        }

        def relativeImagePath = getRelativeImagePath(imagePath)
        new ImageElement(src: relativeImagePath, alt: alt)
    }

    private String getRelativeImagePath(String imagePath) {
        def imageFile = getImageFile(imagePath)

        if (Files.notExists(imageFile)) {
            // It is required to normalize path such as '/path/to/../images/sample.png'.
            System.err.println("WARNING: " + messageSource.getMessage("image.reference.not.exists.message", [imagePath, pageSource.path]))
            return imagePath
        }

        pageSource.outputPath.parent.relativize(outputDirectory.resolve(staticDirectory.relativize(imageFile.toRealPath(LinkOption.NOFOLLOW_LINKS)))).toString()
    }

    private Path getImageFile(String imagePath) {
        def path = Paths.get(imagePath)
        if (path.absolute) {
            return staticDirectory.resolve(imagePath.substring(1))
        } else {
            return staticDirectory.resolve(outputDirectory.relativize(pageSource.outputPath.parent).resolve(imagePath))
        }
    }
}
