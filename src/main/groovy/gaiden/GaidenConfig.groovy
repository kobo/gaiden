/*
 * Copyright 2013 the original author or authors
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

package gaiden

/**
 * The Gaiden configuration.
 *
 * @author Kazuki YAMAMOTO
 * @author Hideki IGARASHI
 */
@Singleton
class GaidenConfig {

    /** The base title of page */
    String title

    /** The path of template file */
    String templatePath

    /** The path of TOC file */
    String tocPath

    /** The path of TOC output file */
    String tocOutputPath

    /** The directory of page source files */
    String pagesDirectory

    /** The directory of static files */
    String staticDirectory

    /** The directory to be outputted a document */
    String outputDirectory

    /** Gets the {@link #templatePath} as {@link File} */
    File getTemplatePathFile() {
        new File(templatePath)
    }

    /** Gets the {@link #tocPath} as {@link File} */
    File getTocPathFile() {
        new File(tocPath)
    }

    /** Gets the {@link #pagesDirectory} as {@link File} */
    File getPagesDirectoryFile() {
        new File(pagesDirectory)
    }

    /** Gets the {@link #staticDirectory} as {@link File} */
    File getStaticDirectoryFile() {
        new File(staticDirectory)
    }

    /** Gets the {@link #outputDirectory} as {@link File} */
    File getOutputDirectoryFile() {
        new File(outputDirectory)
    }

}
