package gaiden

class PagesParserSpec extends GaidenSpec {

    def "parse a content of pages.groovy"() {
        given:
            def parser = new PagesParser()
            parser.gaidenConfig = gaidenConfig

        when:
            def pageEntries = parser.parse '''
"chapter1.md"
"chapter2.md" {
    "chapter2/chapter2-1.md"
    "chapter2/chapter2-2.md"(author: "John")
}
"chapter3.md"(author: "John")
"chapter4.md"(author: "John") {
    "chapter4/chapter4-1.md"
    "chapter4/chapter4-2.md"(author: "John")
}
'''

        then:
            assertPageEntries pageEntries, [
                [path: "chapter1.md", metadata: [:]],
                [path: "chapter2.md", metadata: [:]],
                [path: "chapter2/chapter2-1.md", metadata: [:]],
                [path: "chapter2/chapter2-2.md", metadata: [author: "John"]],
                [path: "chapter3.md", metadata: [author: "John"]],
                [path: "chapter4.md", metadata: [author: "John"]],
                [path: "chapter4/chapter4-1.md", metadata: [:]],
                [path: "chapter4/chapter4-2.md", metadata: [author: "John"]]
            ]
    }

    private void assertPageEntries(List<PageEntry> actual, List<Map> expected) {
        def entries = actual.collect {
            [
                path    : gaidenConfig.projectDirectory.relativize(it.path).toString(),
                metadata: it.metadata,
            ]
        }
        assert entries == expected
    }
}
