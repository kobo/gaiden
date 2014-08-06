package gaiden

import gaiden.util.PathUtils
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import java.nio.file.Files
import java.nio.file.LinkOption
import java.nio.file.Path
import java.nio.file.Paths

@Component
@CompileStatic
class PageReferenceFactory {

    @Autowired
    GaidenConfig gaidenConfig

    PageReference get(Path path, PageSource basePageSource = null) {
        return new PageReference(
            path: getPath(path, basePageSource),
            hash: getHash(path),
        )
    }

    private Path getPath(Path original, PageSource basePageSource) {
        def path = removeHash(original)

        if (PathUtils.getExtension(path) && !(PathUtils.getExtension(path) in SourceCollector.PAGE_SOURCE_EXTENSIONS)) {
            return gaidenConfig.outputDirectory.resolve(path.absolute ? Paths.get(path.toString().substring(1)) : path)
        }

        if (path.absolute) {
            return getCanonicalPath(gaidenConfig.pagesDirectory.resolve(path.toString().substring(1)))
        }

        if (!basePageSource) {
            return getCanonicalPath(gaidenConfig.pagesDirectory.resolve(path))
        }

        return getCanonicalPath(basePageSource.path.resolveSibling(path))
    }

    private static Path getCanonicalPath(Path path) {
        if (Files.exists(path)) {
            return path.toRealPath(LinkOption.NOFOLLOW_LINKS)
        }

        def found = SourceCollector.PAGE_SOURCE_EXTENSIONS.collect { String extension ->
            Paths.get("${path.toString()}.${extension}")
        }.find { Path pathWithExtension ->
            Files.exists(pathWithExtension)
        }
        if (found) {
            return found.toRealPath(LinkOption.NOFOLLOW_LINKS)
        }
        return path
    }

    private static String getHash(Path path) {
        def filename = path.fileName.toString()
        def hashIndex = filename.lastIndexOf("#")

        if (hashIndex == -1) {
            return ""
        }
        filename.substring(hashIndex)
    }

    private static Path removeHash(Path path) {
        def filename = path.fileName.toString()
        def hashIndex = filename.lastIndexOf("#")

        if (hashIndex == -1) {
            return path
        }
        return path.resolveSibling(filename.substring(0, hashIndex))
    }
}
