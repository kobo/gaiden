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

    private static final String NORMALIZED_SEPARATOR = "/"

    /**
     * Replaces the extension of filename.
     *
     * @param filename the filename
     * @param extension the new extension
     * @return the replaced filename
     */
    static String replaceExtension(String filename, String extension) {
        filename.replaceFirst(/\.[^.]+$/, ".${extension}")
    }

    /**
     * Removes the extension of filename.
     *
     * @param filename
     * @return the removed filename
     */
    static String removeExtension(String filename) {
        filename.replaceFirst(/\.[^.]+$/, "")
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
        def normalizedPath = normalize(directory.canonicalPath)
        if (normalizedPath.endsWith(NORMALIZED_SEPARATOR)) {
            return normalizedPath
        }
        normalizedPath + NORMALIZED_SEPARATOR
    }

    private static normalize(String path) {
        if (File.separator == NORMALIZED_SEPARATOR) {
            return path
        }
        path.replaceAll("\\\\", NORMALIZED_SEPARATOR)
    }

    private static String getRelativePath(String from, String to) {
        def commonBaseDir = getCommonBaseDirectory(from, to)
        def replacedFrom = from.replaceFirst(commonBaseDir, '')
        def replacedTo = to.replaceFirst(commonBaseDir, '')
        def baseDirectory = getBaseDirectory(replacedFrom)
        baseDirectory.replaceAll("[^$NORMALIZED_SEPARATOR]+$NORMALIZED_SEPARATOR", "..$NORMALIZED_SEPARATOR") + replacedTo
    }

    private static String getCommonBaseDirectory(String from, String to) {
        def result = []
        def splitPathToken = { getBaseDirectory(it).split(NORMALIZED_SEPARATOR) }
        def fromTokens = splitPathToken(from)
        def toTokens = splitPathToken(to)
        for (int i : 0..<[fromTokens, toTokens]*.size().min()) {
            if (fromTokens[i] != toTokens[i]) break
            result << fromTokens[i]
        }
        result << ''
        result.join(NORMALIZED_SEPARATOR) ?: NORMALIZED_SEPARATOR
    }

    private static String getBaseDirectory(String path) {
        path.replaceFirst("[^$NORMALIZED_SEPARATOR]*\$", '')
    }

}
