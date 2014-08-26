package gaiden

import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode

@CompileStatic
class FilterBuilder {

    private List<Filter> filters = []

    List<Filter> build(Closure closure) {
        closure.delegate = this
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        closure.call()
        filters
    }

    @CompileStatic(TypeCheckingMode.SKIP)
    def methodMissing(String name, args) {
        if (!args || !(args[0] instanceof Closure)) {
            return
        }

        def filterBlock = args[0] as Closure
        def filter = new Filter(name: name)
        filterBlock.delegate = filter
        filterBlock.resolveStrategy = Closure.DELEGATE_FIRST
        filterBlock.call()

        filters << filter
    }
}
