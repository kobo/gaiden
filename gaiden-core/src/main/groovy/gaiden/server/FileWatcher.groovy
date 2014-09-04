package gaiden.server

import groovy.transform.CompileStatic
import org.apache.commons.io.filefilter.FileFileFilter
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor
import org.apache.commons.io.monitor.FileAlterationObserver

import java.nio.file.Path
import java.util.concurrent.LinkedBlockingQueue

@CompileStatic
class FileWatcher {

    private LinkedBlockingQueue changedFiles = []
    private LinkedBlockingQueue createdFiles = []
    private LinkedBlockingQueue deletedFiles = []
    private FileAlterationObserver observer

    FileWatcher(Path path, List<Path> excludes = []) {
        observer = new FileAlterationObserver(path.toFile(), new FileFileFilter() {
            @Override
            boolean accept(File file) {
                excludes.every { Path exclude ->
                    !file.toString().startsWith(exclude.toString())
                }
            }
        })
        observer.addListener(new FileAlterationListenerAdaptor() {
            @Override
            void onFileChange(File file) { changedFiles << file }

            @Override
            void onFileCreate(File file) { createdFiles << file }

            @Override
            void onFileDelete(File file) { deletedFiles << file }
        })
        observer.initialize()
    }

    /**
     * Start watching target a file or a directory.
     * Use a thread to return without blocking.
     * So JVM process cannot shutdown because this thread which loops infinitely is not daemon.
     */
    void watch(Closure callback) {
        Thread.start {
            while (true) {
                observer.checkAndNotify()
                sleep(1000)
                if (changedFiles || createdFiles || deletedFiles) {
                    callback.call(changedFiles.toList(), createdFiles.toList(), deletedFiles.toList())
                    changedFiles.clear()
                    createdFiles.clear()
                    deletedFiles.clear()
                }
            }
        }
    }
}
