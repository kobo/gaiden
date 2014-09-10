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

@CompileStatic
class Filter {

    String name

    Closure before
    Closure after
    Closure afterTemplate

    String doBefore(String text) {
        if (before) {
            return before.call(text)
        }
        return text
    }

    String doAfter(String text) {
        if (after) {
            return after.call(text)
        }
        return text
    }

    String doAfterTemplate(String text) {
        if (afterTemplate) {
            return afterTemplate.call(text)
        }
        return text
    }
}
