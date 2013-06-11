package gaiden

import spock.lang.Specification

class SourceCollectorSpec extends Specification {

    SourceCollector collector = new SourceCollector()

    def "dummy"() {
        when:
        collector.collect()

        then:
        true
    }
}
