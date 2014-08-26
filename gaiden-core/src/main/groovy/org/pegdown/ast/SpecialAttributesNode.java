package org.pegdown.ast;

import org.apache.commons.lang3.StringUtils;
import org.parboiled.common.ImmutableList;

import java.util.ArrayList;
import java.util.List;

public class SpecialAttributesNode extends AbstractNode {
    private String id;
    private List<String> classes = new ArrayList<>();

    public String getId() {
        return id;
    }

    public boolean setId(String id) {
        this.id = id;
        return true;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    @Override
    public List<Node> getChildren() {
        return ImmutableList.of();
    }

    public List<String> getClasses() {
        return classes;
    }

    public boolean addClass(String clazz) {
        if (StringUtils.isNotEmpty(clazz)) {
            classes.add(clazz);
        }
        return true;
    }
}
