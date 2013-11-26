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

package gaiden

import gaiden.util.FileUtils

/**
 * A reference of page.
 *
 * @author Kazuki YAMAMOTO
 */
class PageReference {

    /**
     * The relative path from the pages directory.
     * <p>
     * The path doesn't contain a fragment(#).
     */
    String path

    /** The fragment of path */
    String fragment

    PageReference(String url) {
        def index = url.lastIndexOf("#")
        if (index == -1) {
            path = url
            fragment = ""
        } else {
            path = url.substring(0, index)
            fragment = url.substring(index)
        }
    }

    /**
     * Returns the abstract relative path from the pages directory.
     * <p>
     * The pass doesn't contain a file extension.
     *
     * @return the abstract relative path
     */
    String getAbstractPath() {
        FileUtils.removeExtension(path)
    }

    /**
     * Returns the extension of filename.
     *
     * @return the extension of filename
     */
    String getExtension() {
        FileUtils.getExtension(new File(path).name)
    }

    /**
     * Checks the path is URL.
     *
     * @return {@code true} if the path is URL
     */
    boolean isUrl() {
        try {
            new URL(path)
        } catch (MalformedURLException e) {
            return false
        }
        true
    }
}
