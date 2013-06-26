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

"first-1"(title: "First 1")
"first-2"(title: "First 2")

second(title: "Second") {
    "second-1"(title: "Second 1")
    "second-2"(title: "Second 2")
    "second-3"(title: "Second 3")

    third(title: "Third") {
        "third-1"(title: "Third 1")
        "third-2"(title: "Third 2")
    }
}

