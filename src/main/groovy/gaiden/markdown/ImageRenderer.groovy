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
import gaiden.util.FileUtils
import groovy.transform.CompileStatic

/**
 * A Renderer for image node.
 *
 * @author Hideki IGARASHI
 * @author Kazuki YAMAMOTO
 */
@CompileStatic
class ImageRenderer {

    private PageSource pageSource
    private File staticDirectory
    private MessageSource messageSource

    ImageRenderer(PageSource pageSource, File staticDirectory, MessageSource messageSource) {
        this.pageSource = pageSource
        this.staticDirectory = staticDirectory
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
        if (imagePath =~ /^\w+:\/\//) {  // URL
            return new ImageElement(src: imagePath, alt: alt)
        }

        def relativeImagePath = getRelativePathForImageFileToOutputPageFile(imagePath)
        new ImageElement(src: relativeImagePath, alt: alt)
    }

    private String getRelativePathForImageFileToOutputPageFile(String imagePath) {
        def staticOutputPageFile = new File(staticDirectory, pageSource.outputPath)

        if (imagePath.startsWith("/")) {
            def resourceFile = new File(staticDirectory, imagePath)
            checkFileExists(resourceFile, imagePath)
            return FileUtils.getRelativePathForFileToFile(staticOutputPageFile, resourceFile)
        } else {
            def resourceFile = new File(staticOutputPageFile.parentFile, imagePath)
            checkFileExists(resourceFile, imagePath)
            return imagePath
        }
    }

    private void checkFileExists(File resourceFile, String imagePath) {
        if (!resourceFile.canonicalFile.exists()) {
            // It is required to normalize path such as '/path/to/../images/sample.png'.
            System.err.println("WARNING: " + messageSource.getMessage("image.reference.not.exists.message", [imagePath, pageSource.path]))
        }
    }

}
