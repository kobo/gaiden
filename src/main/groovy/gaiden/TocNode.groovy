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

/**
 * A node of TOC.
 *
 * @author Kazuki YAMAMOTO
 */
class TocNode {

    /** The path of the page source */
    String path
    /** The title of the page */
    String title

    /** The page source */
    PageSource pageSource

    /** The previous TOC node */
    TocNode previous
    /** The next TOC node */
    TocNode next
    /** The parent TOC node */
    TocNode parent

    /** The {@code List} of TOC nodes children */
    List<TocNode> children

    /**
     * Returns The page reference of the path.
     *
     * @return The page reference
     */
    PageReference getPageReference() {
        new PageReference(path)
    }
}
