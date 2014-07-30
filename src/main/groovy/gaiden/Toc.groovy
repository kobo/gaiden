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

import groovy.transform.CompileStatic

/**
 * A table of contents.
 *
 * @author Hideki IGARASHI
 * @author Kazuki YAMAMOTO
 */
@CompileStatic
class Toc {

    /** A root node of TOC */
    TocNode root

    /** A list of TOC node */
    List<TocNode> tocNodes

    /** A relative path from the output directory */
    String path

    /** A output content */
    String content

    /**
     * Finds TOC node which matches the specified page reference.
     *
     * @param pageReference the page reference
     * @return TOC node found
     */
    TocNode findTocNode(PageReference pageReference) {
        tocNodes.find { it.pageSource?.matches(pageReference) }
    }

}
