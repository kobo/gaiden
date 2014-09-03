/*
 * Copyright 2014 the original author or authors
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

import gaiden.Header
import gaiden.PageReference
import groovy.transform.CompileStatic
import org.apache.commons.codec.digest.DigestUtils
import org.pegdown.LinkRenderer
import org.pegdown.ToHtmlSerializer
import org.pegdown.ast.GaidenHeaderNode
import org.pegdown.ast.HeaderNode
import org.pegdown.ast.Node
import org.pegdown.ast.RootNode

@CompileStatic
class HeaderParser extends ToHtmlSerializer {

    private RootNode rootNode
    private PageReference pageReference
    private List<Header> headers = []

    HeaderParser(RootNode rootNode, PageReference pageReference) {
        super(new LinkRenderer())
        this.rootNode = rootNode
        this.pageReference = pageReference
    }

    List<Header> getHeaders() {
        rootNode.accept(this)
        return headers
    }

    @Override
    void visit(HeaderNode headerNode) {
        def title = new HeaderTextSerializer(headerNode).text
        def hash = getHash(title, headerNode)
        headers << new Header(
            title: title,
            level: baseLevel + headerNode.level,
            hash: hash,
            headerNode: headerNode,
            firstOnPage: headers.size() == 0
        )
    }

    private int getBaseLevel() {
        pageReference?.baseLevel ?: 0
    }

    private String getHash(String title, HeaderNode headerNode) {
        if (headerNode instanceof GaidenHeaderNode) {
            def gaidenHeaderNode = headerNode as GaidenHeaderNode
            if (gaidenHeaderNode.specialAttributes?.id) {
                return gaidenHeaderNode.specialAttributes?.id
            }
        }

        createHash(title, headerNode)
    }

    private String createHash(String title, HeaderNode headerNode) {
        def hash = title.replaceAll("&.+?;", "").collect { String c ->
            if (c ==~ /[a-zA-Z0-9\-_]/) {
                return c
            }
            if (c == " ") {
                return "-"
            }
            return ""
        }.join("").toLowerCase()

        if (hash && !headers.find { Header header -> header.hash == hash }) {
            return hash
        }

        def parentHeaders = getParentHeaders(headerNode.level)
        def parentHash = parentHeaders.collect { it.hash }.join("")
        "id-${DigestUtils.sha1Hex(parentHash + title)}"
    }

    private List<Header> getParentHeaders(int level) {
        def currentLevel = level + baseLevel
        def parentHeaders = []
        headers.reverse().each { Header header ->
            if (header.level < currentLevel) {
                parentHeaders << header
                currentLevel = header.level
            }
        }
        parentHeaders
    }

    static class HeaderTextSerializer extends ToHtmlSerializer {

        private HeaderNode headerNode

        HeaderTextSerializer(HeaderNode headerNode) {
            super(new LinkRenderer())
            this.headerNode = headerNode
        }

        String getText() {
            headerNode.children.each { Node node ->
                node.accept(this)
            }
            printer.string.replaceAll(/<.+?>/, "")
        }
    }
}
