package gaiden

import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode

@CompileStatic
class FilterBuilder {

    private Map<String, Closure> filters = [:]

    Filter build(Closure closure) {
        closure.delegate = this
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        closure.call()
        return new ClosureFilter(filters: filters)
    }

    @CompileStatic(TypeCheckingMode.SKIP)
    def methodMissing(String name, args) {
        if (!Filter.methods.collect { it.name }.contains(name)) {
            return
        }

        if (!args || !(args[0] instanceof Closure)) {
            return
        }

        filters[name] = args[0]
    }

    private static class ClosureFilter implements Filter {
        private Map<String, Closure> filters

        @Override
        String before(String text) {
            if (filters.before) {
                return filters.before.call(text)
            }
            return text
        }

        @Override
        String after(String text) {
            if (filters.after) {
                return filters.after.call(text)
            }
            return text
        }

        @Override
        String afterTemplate(String text) {
            if (filters.afterTemplate) {
                return filters.afterTemplate.call(text)
            }
            return text
        }
    }
}
