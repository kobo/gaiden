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

import java.text.MessageFormat

/**
 * MessageSource is message resolver which support
 * for the parameterization and internationalization.
 *
 * @author Kazuki YAMAMOTO
 * @author Hideki IGARASHI
 */
class MessageSource {

    private ResourceBundle resource

    MessageSource(String bundleName = "messages") {
        resource = ResourceBundle.getBundle(bundleName)
    }

    /**
     * Gets a message for the given key.
     *
     * @param key the key for message
     * @param arguments the list of objects to be bound into the message
     * @return resolved message, {@code null} if can not resolve the message
     */
    String getMessage(String key, List<Object> arguments = []) {
        try {
            MessageFormat.format(resource.getString(key), arguments as Object[])
        } catch (MissingResourceException e) {
            // NOOP
            null
        }
    }

}
