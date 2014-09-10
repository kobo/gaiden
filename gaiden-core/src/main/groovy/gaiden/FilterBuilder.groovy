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

package gaiden

import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode

@CompileStatic
class FilterBuilder {

    private List<Filter> filters = []

    List<Filter> build(Closure closure) {
        closure.delegate = this
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        closure.call()
        filters
    }

    @CompileStatic(TypeCheckingMode.SKIP)
    def methodMissing(String name, args) {
        if (!args || !(args[0] instanceof Closure)) {
            return
        }

        def filterBlock = args[0] as Closure
        def filter = new Filter(name: name)
        filterBlock.delegate = filter
        filterBlock.resolveStrategy = Closure.DELEGATE_FIRST
        filterBlock.call()

        filters << filter
    }
}
