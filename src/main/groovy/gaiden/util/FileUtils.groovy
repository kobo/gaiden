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

package gaiden.util

/**
 * File manipulation utilities.
 *
 * @author Hideki IGARASHI
 * @author Kazuki YAMAMOTO
 */
class FileUtils {

    /**
     * Replaces the extension of filename.
     *
     * @param filename the filename
     * @param extension the new extension
     * @return the replaced filename
     */
    private static final String SEPARATOR = "/"

    static String replaceExtension(String filename, String extension) {
        filename.replaceFirst(/\.[^.]+$/, ".${extension}")
    }

    /**
     * Gets the relative path of the base file from the target file.
     *
     * @param from the base file
     * @param to the target file
     * @return the relative path
     */
    static String getRelativePathForFileToFile(File from, File to) {
        getRelativePath(getFilePath(from), getFilePath(to))
    }

    /**
     * Gets the relative path of the base directory from the target file.
     *
     * @param from the base directory
     * @param to the target file
     * @return the relative path
     */
    static String getRelativePathForDirectoryToFile(File from, File to) {
        getRelativePath(getDirectoryPath(from), getFilePath(to))
    }

    private static String getFilePath(File file) {
        normalize(file.canonicalPath)
    }

    private static String getDirectoryPath(File directory) {
        normalize(directory.canonicalPath) + SEPARATOR
    }

    private static normalize(String path) {
        if (File.separator == SEPARATOR) {
            return path
        }
        path.replaceAll("\\\\", SEPARATOR)
    }

    private static String getRelativePath(String from, String to) {
        def commonBaseDir = getCommonBaseDirectory(from, to)
        def replacedFrom = from.replaceFirst(commonBaseDir, '')
        def replacedTo = to.replaceFirst(commonBaseDir, '')
        def baseDirectory = getBaseDirectory(replacedFrom)
        baseDirectory.replaceAll("[^/]+/", "../") + replacedTo
    }

    private static String getCommonBaseDirectory(String from, String to) {
        def result = []
        def splitPathToken = { getBaseDirectory(it).split(SEPARATOR) }
        def fromTokens = splitPathToken(from)
        def toTokens = splitPathToken(to)
        for (int i : 0..<[fromTokens, toTokens]*.size().min()) {
            if (fromTokens[i] != toTokens[i]) break
            result << fromTokens[i]
        }
        result << ''
        result.join(SEPARATOR)
    }

    private static String getBaseDirectory(String path) {
        path.replaceFirst("[^/]*\$", '')
    }

}
