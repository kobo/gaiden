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

/**
 * An exception for Gaiden.
 *
 * @author Kazuki YAMAMOTO
 * @author Hideki IGARASHI
 */
class GaidenException extends RuntimeException {

    final String key
    final List<Object> arguments

    GaidenException(String key, List<Object> arguments = []) {
        super(key)
        this.key = key
        this.arguments = arguments
    }

    GaidenException(String key, Throwable cause) {
        super(key, cause)
        this.key = key
    }

    GaidenException(String key, List<Object> arguments, Throwable cause) {
        super(key, cause)
        this.key = key
        this.arguments = arguments
    }

    @Override
    String getMessage() {
        return "ERROR: " + Holders.getMessage(key, arguments)
    }

}
