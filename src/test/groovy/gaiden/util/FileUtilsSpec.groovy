package gaiden.util

import spock.lang.Specification

class FileUtilsSpec extends Specification {

    def "'getRelativePath' should return a relative path"() {
        expect:
        FileUtils.getRelativePath(baseFile, targetFile) == expected

        where:
        base        | target                       | expected
        '/path/to'  | '/path/to/target.txt'        | 'target.txt'
        '/path/to/' | '/path/to/target.txt'        | 'target.txt'
        '/path/to'  | '/path/to/sub/target.txt'    | 'sub/target.txt'
        '/path/to'  | '/path/to/sub/../target.txt' | 'target.txt'
        './dir'     | './dir/target.txt'           | 'target.txt'

        baseFile = new File(base)
        targetFile = new File(target)
    }

    def "'getRelativePath' should cause assertion error when target isn't into base"() {
        when:
        FileUtils.getRelativePath(new File('/path/to'), new File('/other/target.txt'))

        then:
        thrown(AssertionError)
    }

    def "'replaceExtension' should replace extension of target filename"() {
        expect:
        FileUtils.replaceExtension("/path/to/target.md", "html") == "/path/to/target.html"
    }

}
