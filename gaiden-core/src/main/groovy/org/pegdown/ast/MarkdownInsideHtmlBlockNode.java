package org.pegdown.ast;

import java.util.List;

public class MarkdownInsideHtmlBlockNode extends SuperNode {
    private String beginTag;
    private String endTag;

    public MarkdownInsideHtmlBlockNode(String beginTag, List<Node> children, String endTag) {
        super(children);
        this.beginTag = beginTag;
        this.endTag = endTag;
    }

    public String getBeginTag() {
        return beginTag;
    }

    public String getEndTag() {
        return endTag;
    }
}
