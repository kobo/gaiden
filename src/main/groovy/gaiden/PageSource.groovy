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
 * A holder for a content of source and the meta information.
 *
 * @author Hideki IGARASHI
 * @author Kazuki YAMAMOTO
 */
class PageSource {

    private static final String OUTPUT_EXTENSION = "html"

    /** A relative path from the pages directory */
    String path

    /** A content of source */
    String content

    /**
     * Gets a output page path.
     *
     * @return a output page path
     */
    String getOutputPagePath() {
        FileUtils.replaceExtension(path, OUTPUT_EXTENSION)
    }

}
