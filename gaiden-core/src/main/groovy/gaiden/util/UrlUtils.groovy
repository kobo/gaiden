package gaiden.util

import groovy.transform.CompileStatic

@CompileStatic
class UrlUtils {

    /**
     * Checks the path is URL.
     *
     * @return {@code true} if the path is URL
     */
    static boolean isUrl(String url) {
        try {
            new URL(url)
        } catch (MalformedURLException e) {
            return false
        }
        true
    }
}
