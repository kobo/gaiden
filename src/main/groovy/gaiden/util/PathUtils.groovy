package gaiden.util

import groovy.io.FileType
import groovy.transform.CompileStatic
import org.apache.commons.io.FilenameUtils

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

import static java.nio.file.StandardCopyOption.*

/**
 * {@link Path} utilities.
 *
 * @author Kazuki YAMAMOTO
 */
@CompileStatic
class PathUtils {

    static String getExtension(Path path) {
        FilenameUtils.getExtension(path.toString())
    }

    /**
     * Replaces the extension of filename.
     *
     * @param filename the filename
     * @param extension the new extension
     * @return the replaced filename
     */
    static Path replaceExtension(Path path, String extension) {
        Paths.get("${FilenameUtils.removeExtension(path.toString())}.${extension}")
    }

    /**
     * Removes the extension of filename.
     *
     * @param filename
     * @return the removed filename
     */
    static Path removeExtension(Path path) {
        Paths.get(FilenameUtils.removeExtension(path.toString()))
    }

    static void copyFiles(Path src, Path dest) {
        src.eachFileRecurse(FileType.FILES) { Path srcFile ->
            def destFile = dest.resolve(src.relativize(srcFile))
            if (Files.notExists(destFile.parent)) {
                Files.createDirectories(destFile.parent)
            }
            Files.copy(srcFile, destFile, REPLACE_EXISTING)
        }
    }
}
