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

"README.md"

"chapter2/README.md"(tags: ["tag1", "tag2"]) {
    "chapter2/section1.md"(title: "custom title", tags: ["tag1"])
}

"chapter3/README.md" {
    "chapter3/section1.md"(layout: "simple")
}

"unnumbered.md"(numbering: false)
"specified-document-toc-depth.md"(documentTocDepth: 1)
"specified-page-toc-depth.md"(pageTocDepth: 1)

"home.md"(hidden: true)
"filter.md"(hidden: true, layout: "filter")
"nav-holder.md"(hidden: true, layout: "render")
"extension.md"(hidden: true, layout: "extension")

"non-existent.md"
