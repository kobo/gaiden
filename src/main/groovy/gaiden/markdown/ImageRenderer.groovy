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

import gaiden.util.FileUtils

/**
 * A Renderer for image node.
 *
 * @author Hideki IGARASHI
 * @author Kazuki YAMAMOTO
 */
class ImageRenderer {

    private String outputPagePath

    ImageRenderer(String outputPagePath) {
        this.outputPagePath = outputPagePath
    }

    /**
     * Simple model class for rendering an img element.
     */
    static class ImageElement {
        String src
        String alt
    }

    /**
     * Returns a rendering object which has a image path replaced with an appropriate relative path.
     * <p>
     * An original image path is used without a replacement if it doesn't start with a slash.
     *
     * @param url the path of image
     * @param alt the alternate text
     * @return {@link ImageElement}'s instance
     */
    ImageElement render(String url, String alt) {
        if (!url.startsWith("/")) {
            return new ImageElement(src: url, alt: alt)
        }

        def outputPageFile = new File("/", outputPagePath)
        def resourceFile = new File("/", url)
        def src = FileUtils.getRelativePathForFileToFile(outputPageFile, resourceFile)

        new ImageElement(src: src, alt: alt)
    }

}
