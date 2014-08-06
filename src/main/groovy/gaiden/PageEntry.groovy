package gaiden

import groovy.transform.Canonical
import groovy.transform.CompileStatic

import java.nio.file.Path

@CompileStatic
@Canonical
class PageEntry {
    Path path
    Map<String, String> metadata
}
