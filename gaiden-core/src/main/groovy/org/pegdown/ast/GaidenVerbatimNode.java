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
