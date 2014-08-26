package org.pegdown.ast;

public class GaidenVerbatimNode extends VerbatimNode {

    private SpecialAttributesNode specialAttributesNode;

    public GaidenVerbatimNode(String text, SpecialAttributesNode specialAttributesNode) {
        super(text, null);
        this.specialAttributesNode = specialAttributesNode;
    }

    public GaidenVerbatimNode(String text) {
        super(text);
    }

    public SpecialAttributesNode getSpecialAttributesNode() {
        return specialAttributesNode;
    }
}
