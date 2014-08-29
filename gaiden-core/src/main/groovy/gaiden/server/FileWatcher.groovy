package gaiden.server

import groovy.transform.CompileStatic
import org.apache.commons.io.filefilter.FileFileFilter
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor
import org.apache.commons.io.monitor.FileAlterationObserver

import java.nio.file.Path

@CompileStatic
class FileWatcher {

    void watch(Path path, List<Path> excludes, Closure closure) {
        def observer = new FileAlterationObserver(path.toFile(), new FileFileFilter() {
            @Override
            boolean accept(File file) {
                excludes.every { Path exclude ->
                    !file.toString().startsWith(exclude.toString())
                }
            }
        })

        def notified = false
        def notify = {
            if (notified) {
                return
            }
            notified = true
            closure.call()
        }

        observer.addListener(new FileAlterationListenerAdaptor() {
            @Override
            void onFileChange(File file) { notify() }

            @Override
            void onFileCreate(File file) { notify() }

            @Override
            void onFileDelete(File file) { notify() }
        })
        observer.initialize()

        while (true) {
            observer.checkAndNotify()
            notified = false
            sleep(1000)
        }
    }
}
