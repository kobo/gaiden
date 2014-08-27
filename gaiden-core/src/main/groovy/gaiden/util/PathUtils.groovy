package gaiden.util

import groovy.io.FileType
import groovy.transform.CompileStatic
import groovy.transform.stc.ClosureParams
import groovy.transform.stc.SimpleType
import org.apache.commons.io.FilenameUtils

import java.nio.file.FileVisitResult
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.SimpleFileVisitor
import java.nio.file.attribute.BasicFileAttributes
import java.nio.file.attribute.PosixFilePermission

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

    static List<Path> list(Path path, FileType fileType = FileType.ANY) {
        def paths = []
        path.eachFile(fileType) { paths << it }
        paths
    }

    static void copyFile(Path src, Path dest) {
        if (Files.notExists(dest.parent)) {
            Files.createDirectories(dest.parent)
        }
        Files.copy(src, dest, REPLACE_EXISTING)
    }

    static void copyFiles(Path src, Path dest) {
        if (Files.notExists(src)) {
            return
        }

        src.eachFileRecurse(FileType.FILES) { Path srcFile ->
            def destFile = dest.resolve(src.relativize(srcFile))
            copyFile(srcFile, destFile)
        }
    }

    static void eachFileRecurse(Path path, List<Path> skipDirectories = [],
                                @ClosureParams(value = SimpleType.class, options = "java.nio.file.Path") Closure closure) {
        checkDir(path)
        Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
            @Override
            FileVisitResult preVisitDirectory(Path directory, BasicFileAttributes attributes) throws IOException {
                skipDirectories.contains(directory) ? FileVisitResult.SKIP_SUBTREE : FileVisitResult.CONTINUE
            }

            @Override
            FileVisitResult visitFile(Path file, BasicFileAttributes attributes) throws IOException {
                closure.call(file)
                FileVisitResult.CONTINUE
            }
        })
    }

    private static void checkDir(Path self) throws FileNotFoundException, IllegalArgumentException {
        if (!Files.exists(self))
            throw new FileNotFoundException(self.toAbsolutePath().toString())
        if (!Files.isDirectory(self))
            throw new IllegalArgumentException("The provided Path object is not a directory: " + self.toAbsolutePath())
    }
}
