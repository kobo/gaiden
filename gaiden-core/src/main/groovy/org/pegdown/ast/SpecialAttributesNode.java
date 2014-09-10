/*
 * Copyright 2014 the original author or authors
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
