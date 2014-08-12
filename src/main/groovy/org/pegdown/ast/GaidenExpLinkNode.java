package org.pegdown.ast;

public class GaidenExpLinkNode extends ExpLinkNode {
    private SpecialAttributesNode specialAttributesNode;

    public GaidenExpLinkNode(SpecialAttributesNode specialAttributesNode, String title, String url, Node child) {
        super(title, url, child);
        this.specialAttributesNode = specialAttributesNode;
    }

    public SpecialAttributesNode getSpecialAttributesNode() {
        return specialAttributesNode;
    }
}
