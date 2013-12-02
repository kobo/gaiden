package gaiden

import gaiden.context.PageBuildContext
import gaiden.util.FileUtils

/**
 * Builder for binding to be passed to a template engine.
 *
 * @author Kazuki YAMAMOTO
 */
class BindingBuilder {

    private static final String EMPTY_STRING = ""

    private String title
    private String tocOutputFilePath

    private String content
    private String sourcePath
    private String outputPath

    private PageBuildContext pageBuildContext

    BindingBuilder(String title = Holders.config.title, String tocOutputFilePath = Holders.config.tocOutputFilePath) {
        this.title = title
        this.tocOutputFilePath = tocOutputFilePath
    }

    /**
     * Builds binding.
     *
     * @return binding
     */
    Map<String, Object> build() {
        [
            title: title,
            content: content,
            tocPath: tocPath,
            resource: resourceMethod,
            prevLink: prevLink,
            nextLink: nextLink,
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
    BindingBuilder setOutputPath(String outputPath) {
        this.outputPath = outputPath
        this
    }

    /**
     * Sets the page path.
     */
    BindingBuilder setSourcePath(String sourcePath) {
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

    private String getPrevLink() {
        if (!sourcePath || !pageBuildContext) {
            return EMPTY_STRING
        }

        def previousTocNode = pageBuildContext.toc.findTocNode(new PageReference(sourcePath))?.previous
        if (!previousTocNode) {
            return EMPTY_STRING
        }

        if (!previousTocNode.pageSource) {
            return "<< $previousTocNode.title".encodeAsHtml()
        }

        def relativePath = FileUtils.getRelativePathForFileToFile(outputPath, previousTocNode.pageSource.outputPath)

        def sb = new StringBuilder()
        sb << "<a href='${relativePath + previousTocNode.pageReference.fragment}' class='prev'>"
        sb << "<< $previousTocNode.title".encodeAsHtml()
        sb << "</a>"
        return sb.toString()
    }

    private String getNextLink() {
        if (!sourcePath || !pageBuildContext) {
            return EMPTY_STRING
        }

        def nextTocNode = pageBuildContext.toc.findTocNode(new PageReference(sourcePath))?.next
        if (!nextTocNode) {
            return EMPTY_STRING
        }

        if (!nextTocNode.pageSource) {
            return "$nextTocNode.title >>".encodeAsHtml()
        }

        def relativePath = FileUtils.getRelativePathForFileToFile(outputPath, nextTocNode.pageSource.outputPath)

        def sb = new StringBuilder()
        sb << "<a href='${relativePath + nextTocNode.pageReference.fragment}' class='next'>"
        sb << "$nextTocNode.title >>".encodeAsHtml()
        sb << "</a>"
        return sb.toString()
    }

    private Closure getResourceMethod() {
        return { String resourceFilePath ->
            if (!resourceFilePath.startsWith("/")) {
                return resourceFilePath
            }
            FileUtils.getRelativePathForFileToFile(outputPath, resourceFilePath)
        }
    }

    private String getTocPath() {
        FileUtils.getRelativePathForFileToFile(outputPath, tocOutputFilePath)
    }

}
