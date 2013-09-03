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

import gaiden.Holders
import gaiden.util.FileUtils

/**
 * A Renderer for image node.
 *
 * @author Hideki IGARASHI
 * @author Kazuki YAMAMOTO
 */
class ImageRenderer {

    private String outputPath
    private File outputDirectory

    ImageRenderer(String outputPath, File outputDirectory = Holders.config.outputDirectoryFile) {
        this.outputPath = outputPath
        this.outputDirectory = outputDirectory
    }

    /**
     * Simple model class for rendering an img element.
     */
    static class Rendering {
        String src
        String alt
    }

    /**
     * Returns a rendering object which has replaced image path.
     * <p>
     * If a image path doesn't start with slash, then don't replace.
     *
     * @param url the path of image
     * @param alt the alternate text
     * @return {@link Rendering}'s instance
     */
    Rendering render(String url, String alt) {
        if (!url.startsWith("/")) {
            return new Rendering(src: url, alt: alt)
        }

        def outputFile = new File(outputDirectory, outputPath)
        def resourceFile = new File(outputDirectory, url)
        def src = FileUtils.getRelativePathForFileToFile(outputFile, resourceFile)

        new Rendering(src: src, alt: alt)
    }

}
