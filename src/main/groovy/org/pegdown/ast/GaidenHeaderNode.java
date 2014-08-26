package org.pegdown.ast;

import groovy.transform.CompileStatic;

@CompileStatic
public class GaidenHeaderNode extends HeaderNode {
    private SpecialAttributesNode specialAttributes;

    public GaidenHeaderNode(int level) {
        super(level);
    }

    public GaidenHeaderNode(int level, Node child) {
        super(level, child);
    }

    public boolean setSpecialAttributes(SpecialAttributesNode specialAttributes) {
        this.specialAttributes = specialAttributes;
        return true;
    }

    public SpecialAttributesNode getSpecialAttributes() {
        return specialAttributes;
    }
}
