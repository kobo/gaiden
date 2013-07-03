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
    static String replaceExtension(String filename, String extension) {
        filename.replaceAll(/\.[^.]+$/, ".${extension}")
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
        file.canonicalPath
    }

    private static String getDirectoryPath(File directory) {
        directory.canonicalPath + File.separator
    }

    private static String getRelativePath(String from, String to) {
        def (optimizedFrom, optimizedTo) = optimizeCommonBaseDirectory(from, to)
        getBaseDirectory(optimizedFrom).replaceAll("[^${File.separator}]+${File.separator}", "..${File.separator}") + optimizedTo
    }

    private static List<String> optimizeCommonBaseDirectory(String from, String to) {
        def commonBaseDir = getCommonBaseDirectory(from, to)
        [from, to].collect { it.replaceFirst(commonBaseDir, '') }
    }

    private static String getCommonBaseDirectory(String from, String to) {
        def result = []
        def splitPathToken = { getBaseDirectory(it).split(File.separator) }
        def fromTokens = splitPathToken(from)
        def toTokens = splitPathToken(to)
        for (int i : 0..<[fromTokens, toTokens]*.size().min()) {
            if (fromTokens[i] != toTokens[i]) break
            result << fromTokens[i]
        }
        result << ''
        result.join(File.separator)
    }

    private static String getBaseDirectory(path) {
        path.replaceFirst("[^${File.separator}]*\$", '')
    }

}
