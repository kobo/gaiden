/*
 * Copyright 2013 the original author or authors
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

package gaiden

import groovy.transform.CompileStatic

import java.nio.file.Path

@CompileStatic
class Extension {

    String name

    Path assetsDirectory

    ConfigObject configObject

    List<Filter> filters = Collections.emptyList()

    void setConfigObject(ConfigObject configObject) {
        this.configObject = configObject
        if (configObject.containsKey('filters') && configObject.get('filters') instanceof Closure) {
            filters = new FilterBuilder().build(configObject.get('filters') as Closure)
        }
    }

    def propertyMissing(String name) {
        configObject.get(name)
    }
}
