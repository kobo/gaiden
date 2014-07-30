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

import gaiden.context.PageBuildContext
import groovy.transform.CompileStatic

import java.nio.file.Path
import java.nio.file.Paths

import static org.apache.commons.lang3.StringEscapeUtils.*

/**
 * Builder for binding to be passed to a template engine.
 *
 * @author Kazuki YAMAMOTO
 */
@CompileStatic
class BindingBuilder {

    private static final String EMPTY_STRING = ""

    private String title
    private Path tocOutputFilePath

    private Path outputDirectory

    private String content
    private Path sourcePath
    private Path outputPath

    private PageBuildContext pageBuildContext

    private PageReferenceFactory pageReferenceFactory

    BindingBuilder(String title, Path outputDirectory, Path tocOutputFilePath, PageReferenceFactory pageReferenceFactory) {
        this.title = title
        this.outputDirectory = outputDirectory
        this.tocOutputFilePath = tocOutputFilePath
        this.pageReferenceFactory = pageReferenceFactory
    }

    /**
     * Builds binding.
     *
     * @return binding
     */
    Map<String, Object> build() {
        [
            title   : title,
            content : content,
            tocPath : tocPath,
            resource: resourceMethod,
            prevPage: prevPage,
            nextPage: nextPage,
        ]
    }

    /**
     * Sets the content.
     */
    BindingBuilder setContent(String content) {
        this.content = content
        this
    }

    /**
     * Sets the output path.
     */
    BindingBuilder setOutputPath(Path outputPath) {
        this.outputPath = outputPath
        this
    }

    /**
     * Sets the page path.
     */
    BindingBuilder setSourcePath(Path sourcePath) {
        this.sourcePath = sourcePath
        this
    }

    /**
     * Sets the page build context.
     */
    BindingBuilder setPageBuildContext(PageBuildContext pageBuildContext) {
        this.pageBuildContext = pageBuildContext
        this
    }

    private Map getPrevPage() {
        if (!sourcePath || !pageBuildContext) {
            return [
                path : EMPTY_STRING,
                title: EMPTY_STRING,
            ]
        }

        def previousTocNode = pageBuildContext.toc.findTocNode(sourcePath)?.previous
        if (!previousTocNode) {
            return [
                path : EMPTY_STRING,
                title: EMPTY_STRING,
            ]
        }

        if (!previousTocNode.pageSource) {
            return [
                path : EMPTY_STRING,
                title: escapeHtml4(previousTocNode.title),
            ]
        }

        def relativePath = outputPath.parent.relativize(previousTocNode.pageSource.outputPath)
        return [
            path : relativePath.toString() + previousTocNode.pageReference.hash,
            title: escapeHtml4(previousTocNode.title)
        ]
    }

    private Map getNextPage() {
        if (!sourcePath || !pageBuildContext) {
            return [
                path : EMPTY_STRING,
                title: EMPTY_STRING,
            ]
        }

        def nextTocNode = pageBuildContext.toc.findTocNode(sourcePath)?.next
        if (!nextTocNode) {
            return [
                path : EMPTY_STRING,
                title: EMPTY_STRING,
            ]
        }

        if (!nextTocNode.pageSource) {
            return [
                path : EMPTY_STRING,
                title: escapeHtml4(nextTocNode.title),
            ]
        }

        def relativePath = outputPath.parent.relativize(nextTocNode.pageSource.outputPath)
        return [
            path : relativePath.toString() + nextTocNode.pageReference.hash,
            title: escapeHtml4(nextTocNode.title)
        ]
    }

    private Closure getResourceMethod() {
        return { String resourceFilePath ->
            def resourceFile = Paths.get(resourceFilePath)
            if (!resourceFile.absolute) {
                return resourceFilePath
            }

            def src = outputDirectory.resolve(outputPath)
            def dest = outputDirectory.resolve(resourceFile.toString().substring(1))

            return src.parent.relativize(dest).toString()
        }
    }

    private String getTocPath() {
        outputPath.parent.relativize(tocOutputFilePath).toString()
    }
}
