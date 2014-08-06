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

package gaiden.message

import spock.lang.Specification

class MessageSourceSpec extends Specification {

    def "'getMessage' should return message"() {
        given:
            def messageSource = new MessageSource("message.test_messages")

        expect:
            messageSource.getMessage(key, args) == expected

        where:
            key        | args           | expected
            "simple"   | []             | "simple message"
            "argument" | []             | "{0} {1} argument message"
            "argument" | ["foo", "bar"] | "foo bar argument message"
            "argument" | null           | "{0} {1} argument message"
    }
}
