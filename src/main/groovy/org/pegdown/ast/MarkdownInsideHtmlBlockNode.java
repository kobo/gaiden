package org.pegdown.ast;

import java.util.List;

public class MarkdownInsideHtmlBlockNode extends SuperNode {
    private String tagName;
    private String startTag;

    public MarkdownInsideHtmlBlockNode(String tagName, String startTag, List<Node> children) {
        super(children);
        this.tagName = tagName;
        this.startTag = startTag;
    }

    public String getTagName() {
        return tagName;
    }

    public String getStartTag() {
        return startTag;
    }
}
