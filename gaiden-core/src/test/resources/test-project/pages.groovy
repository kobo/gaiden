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
