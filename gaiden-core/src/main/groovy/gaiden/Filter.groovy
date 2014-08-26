package gaiden

import groovy.transform.CompileStatic

@CompileStatic
class Filter {

    String name

    Closure before
    Closure after
    Closure afterTemplate

    String doBefore(String text) {
        if (before) {
            return before.call(text)
        }
        return text
    }

    String doAfter(String text) {
        if (after) {
            return after.call(text)
        }
        return text
    }

    String doAfterTemplate(String text) {
        if (afterTemplate) {
            return afterTemplate.call(text)
        }
        return text
    }
}
