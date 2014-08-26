package org.pegdown.ast;

public class GaidenReferenceNode extends ReferenceNode {
    private SpecialAttributesNode specialAttributesNode;

    public GaidenReferenceNode(Node child) {
        super(child);
    }

    public SpecialAttributesNode getSpecialAttributesNode() {
        return specialAttributesNode;
    }

    public boolean setSpecialAttributesNode(SpecialAttributesNode specialAttributesNode) {
        this.specialAttributesNode = specialAttributesNode;
        return true;
    }
}
