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
import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode

/**
 * A reference of page.
 *
 * @author Kazuki YAMAMOTO
 */
@CompileStatic
class PageReference {

    /**
     * The relative path from the pages directory.
     * <p>
     * The path doesn't contain a fragment(#).
     */
    String path

    /** The fragment of path */
    String fragment

    @CompileStatic(TypeCheckingMode.SKIP)
    PageReference(String url) {
        (path, fragment) = url.split("#") as List
        fragment = fragment ? "#${fragment}" : ""
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

    /**
     * Checks the path is an absolute path.
     *
     * @return {@code true} if the path is an absolute path
     */
    boolean isAbsolutePath() {
        !path.startsWith("/")
    }

    /**
     * Checks the extension of filename is a page source extension.
     *
     * @return {@code true} if the extension of filename is a page source extension
     * @see SourceCollector#PAGE_SOURCE_EXTENSIONS
     */
    boolean isPageSourceExtension() {
        !extension || extension in SourceCollector.PAGE_SOURCE_EXTENSIONS
    }

    /**
     * Converts this page reference into a page reference which has a full path.
     *
     * @param rootDirectory the root directory of page sources
     * @param baseDirectory the directory of base
     * @return the page reference which has a full path
     */
    PageReference toFullPathPageReference(File rootDirectory, File baseDirectory) {
        assert isAbsolutePath()
        new PageReference(FileUtils.getRelativePathForDirectoryToFile(rootDirectory, new File(baseDirectory, path)) + fragment)
    }
}
