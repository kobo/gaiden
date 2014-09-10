/*
 * Copyright 2014 the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
