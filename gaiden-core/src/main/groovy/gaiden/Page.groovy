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
import org.pegdown.ast.RootNode

import java.nio.file.Path

/**
 * A holder for a content and the meta information.
 *
 * @author Hideki IGARASHI
 * @author Kazuki YAMAMOTO
 */
@CompileStatic
class Page {

    /** the page source */
    PageSource source

    /** the headers of content */
    List<Header> headers

    /** the AST of content */
    RootNode contentNode

    /** The metadata of page */
    Map<String, Object> metadata

    /** The output path of page */
    Path outputPath

    String getTitle() {
        if (metadata.title) {
            return metadata.title
        }
        return headers ? headers.first().title : ""
    }

    List<Integer> getNumbers() {
        if (!headers) {
            return Collections.emptyList()
        }
        return headers.first().numbers
    }

    Path relativize(Page page) {
        relativize(page.outputPath)
    }

    Path relativize(Path path) {
        this.outputPath.parent.relativize(path)
    }
}
