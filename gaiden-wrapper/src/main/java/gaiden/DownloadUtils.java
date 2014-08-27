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

package gaiden;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;

public class DownloadUtils {

    private static final int BUFFER_SIZE = 10000;
    private static final int CHUNK_SIZE = 100000;

    public static void download(URL url, Path destFile) throws IOException {
        System.out.println("Downloading: " + url.toString());

        if (Files.notExists(destFile.getParent())) {
            Files.createDirectories(destFile.getParent());
        }

        try (
            CloseableHttpURLConnection connection = new CloseableHttpURLConnection((HttpURLConnection) url.openConnection());
            InputStream in = new BufferedInputStream(connection.getOriginal().getInputStream());
            OutputStream out = Files.newOutputStream(destFile)
        ) {
            long contentLength = getContentLength(connection);

            byte[] buffer = new byte[BUFFER_SIZE];
            int read;
            long offset = 0;
            long progressCounter = 0;

            printStatus(contentLength, offset);

            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
                offset += read;
                progressCounter += read;
                if (progressCounter / CHUNK_SIZE > 0) {
                    progressCounter = progressCounter - CHUNK_SIZE;
                    printStatus(contentLength, offset);
                }
            }

            printStatus(contentLength, offset);
        }
    }

    private static void printStatus(long contentLength, long offset) {
        if (contentLength == -1) {
            return;
        }

        double percent = ((double) offset) / contentLength;
        System.out.printf("%3d%%[%-60s]%8s/%s\r", (int) (percent * 100), getProgressBar(60, percent), getReadableSize(offset), getReadableSize(contentLength));
    }

    private static String getReadableSize(long bytes) {
        if (bytes <= 0) {
            return "0";
        }
        String[] units = new String[]{" B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(bytes) / Math.log10(1024));
        return (new DecimalFormat("0.0").format(bytes / Math.pow(1024, digitGroups))) + units[digitGroups];
    }

    private static String getProgressBar(int max, double percent) {
        int count = (int) (max * percent);
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i < count; i++) {
            sb.append('=');
        }
        sb.append('>');
        return sb.toString();
    }

    private static long getContentLength(CloseableHttpURLConnection closeableHttpURLConnection) {
        String contentLengthValue = closeableHttpURLConnection.connection.getHeaderField("Content-Length");
        if (contentLengthValue == null) {
            return -1;
        }
        return Long.parseLong(contentLengthValue);
    }

    static class CloseableHttpURLConnection implements Closeable {

        private HttpURLConnection connection;

        CloseableHttpURLConnection(HttpURLConnection connection) {
            this.connection = connection;
        }

        @Override
        public void close() throws IOException {
            connection.disconnect();
        }

        HttpURLConnection getOriginal() {
            return this.connection;
        }
    }
}
