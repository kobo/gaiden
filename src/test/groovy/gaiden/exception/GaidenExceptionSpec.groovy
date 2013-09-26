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

package gaiden.exception

import gaiden.Holders
import gaiden.message.MessageSource
import spock.lang.Specification

class GaidenExceptionSpec extends Specification {

    def "'getMessage' should get message"() {
        setup:
        def savedMessageSource = Holders.messageSource
        Holders.messageSource = new MessageSource("message.test_messages")

        when:
        def e = new GaidenException("simple")

        then:
        e.message == "simple message"

        cleanup:
        Holders.messageSource = savedMessageSource
    }

}
