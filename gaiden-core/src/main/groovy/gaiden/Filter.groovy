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

class Filter {

    String name

    Closure before
    Closure after
    Closure afterTemplate

    String doBefore(String text, GaidenConfig gaidenConfig, PageSource pageSource) {
        if (before) {
            switch (before.parameterTypes.size()) {
                case 1:
                    return before.call(text)
                case 2:
                    return before.call(text, gaidenConfig)
                case 3:
                    return before.call(text, gaidenConfig, pageSource)
                default:
                    throw new IllegalArgumentException("A parameter type of closure for filter is invalid: ${before.parameterTypes}")
            }
        }
        return text
    }

    String doAfter(String text, GaidenConfig gaidenConfig, Page page, Document document) {
        if (after) {
            switch (after.parameterTypes.size()) {
                case 1:
                    return after.call(text)
                case 2:
                    return after.call(text, gaidenConfig)
                case 3:
                    return after.call(text, gaidenConfig, page)
                case 4:
                    return after.call(text, gaidenConfig, page, document)
                default:
                    throw new IllegalArgumentException("A parameter type of closure for filter is invalid: ${after.parameterTypes}")
            }
        }
        return text
    }

    String doAfterTemplate(String text, GaidenConfig gaidenConfig, Page page, Document document) {
        if (afterTemplate) {
            switch (afterTemplate.parameterTypes.size()) {
                case 1:
                    return afterTemplate.call(text)
                case 2:
                    return afterTemplate.call(text, gaidenConfig)
                case 3:
                    return afterTemplate.call(text, gaidenConfig, page)
                case 4:
                    return afterTemplate.call(text, gaidenConfig, page, document)
                default:
                    throw new IllegalArgumentException("A parameter type of closure for filter is invalid: ${afterTemplate.parameterTypes}")
            }
        }
        return text
    }
}
