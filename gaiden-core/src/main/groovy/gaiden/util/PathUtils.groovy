package gaiden.util

import gaiden.GaidenApplication
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

    static void copyFiles(Path src, Path dest, boolean interactive = false, List<Path> excludeDirectories = null, Closure fileFilter = null) {
        if (Files.notExists(src)) {
            return
        }

        Files.walkFileTree(src, new SimpleFileVisitor<Path>() {
            @Override
            FileVisitResult preVisitDirectory(Path directory, BasicFileAttributes attributes) {
                excludeDirectories?.contains(directory) ? FileVisitResult.SKIP_SUBTREE : FileVisitResult.CONTINUE
            }

            @Override
            FileVisitResult visitFile(Path srcFile, BasicFileAttributes attributes) {
                if (fileFilter && !fileFilter.call(srcFile)) {
                    return FileVisitResult.CONTINUE
                }

                def destFile = dest.resolve(src.relativize(srcFile))
                if (Files.exists(destFile) && interactive && GaidenApplication.config.interactive) {
                    def answer = overrites(destFile)
                    if (answer == Answer.NO) {
                        return FileVisitResult.CONTINUE
                    } else if (answer == Answer.ALL) {
                        GaidenApplication.config.interactive = false
                    }
                }

                copyFile(srcFile, destFile)
                return FileVisitResult.CONTINUE
            }
        })
    }

    private enum Answer {
        YES, NO, ALL
    }

    private static Answer overrites(Path path) {
        print GaidenApplication.getMessage("command.install.theme.overwrite.confirmation.message", [path])
        def scanner = new Scanner(System.in)
        while (true) {
            def answer = scanner.nextLine().toLowerCase()
            switch (answer) {
                case ["yes", "y", "true", "t", "on"]:
                    return Answer.YES
                case ["no", "n", "false", "f", "off"]:
                    return Answer.NO
                case ["all", "a"]:
                    return Answer.ALL
                default:
                    print GaidenApplication.getMessage("command.install.theme.overwrite.invalid.answer.error")
            }
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
