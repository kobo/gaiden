package org.pegdown.ast;

public class GaidenExpImageNode extends ExpImageNode {
    private SpecialAttributesNode specialAttributesNode;

    public GaidenExpImageNode(SpecialAttributesNode specialAttributesNode, String title, String url, Node child) {
        super(title, url, child);
        this.specialAttributesNode = specialAttributesNode;
    }

    public SpecialAttributesNode getSpecialAttributesNode() {
        return specialAttributesNode;
    }
}
