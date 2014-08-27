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

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class GaidenWrapper {

    private static Path GAIDEN_CACHE_DIR = Paths.get(System.getProperty("user.home"), ".gaiden");

    public static void main(String[] args) throws IOException, NoSuchMethodException, ClassNotFoundException, InvocationTargetException, IllegalAccessException {
        new GaidenWrapper().run(args);
    }

    private Properties buildProperties;
    private Properties gaidenWrapperProperties;

    private void run(String[] args) throws IOException {
        loadBuildProperties();
        lodGaidenWrapperProperties();

        Path gaidenHome = getGaidenHome();
        System.setProperty("app.home", gaidenHome.toString());

        URLClassLoader classLoader = createClassLoader(gaidenHome);
        invoke(classLoader, "gaiden.GaidenMain", "main", args, String[].class);
    }

    private Path getGaidenHome() throws IOException {
        Path gaidenHome = GAIDEN_CACHE_DIR.resolve("wrapper").resolve("gaiden-" + getVersion());

        if (Files.notExists(gaidenHome)) {
            download(gaidenHome);
        }

        return gaidenHome;
    }

    private void download(Path gaidenHome) throws IOException {
        Path zipFile = getDestZip();
        try {
            DownloadUtils.download(getDistributionUrl(), zipFile);
            ZipUtils.extract(zipFile, gaidenHome.getParent());
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(gaidenHome.resolve("bin"))) {
                for (Path file : stream) {
                    Files.setPosixFilePermissions(file, PosixFilePermissions.fromString("rwxr-xr-x"));
                }
            }
        } finally {
            Files.deleteIfExists(zipFile);
        }
    }

    private URLClassLoader createClassLoader(Path gaidenHome) throws IOException {
        List<URL> jars = new ArrayList<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(gaidenHome.resolve("lib"))) {
            for (Path jar : stream) {
                jars.add(jar.toUri().toURL());
            }
        }
        return new URLClassLoader(jars.toArray(new URL[jars.size()]));
    }

    private void invoke(ClassLoader classLoader, String className, String methodName, String[] args, Class<?>... parameterTypes) {
        try {
            Thread.currentThread().setContextClassLoader(classLoader);

            Class<?> loadClass = classLoader.loadClass(className);
            Method mainMethod = loadClass.getMethod(methodName, parameterTypes);

            mainMethod.invoke(null, new Object[]{args});
        } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private String getVersion() {
        return buildProperties.getProperty("version");
    }

    private Path getDestZip() {
        return GAIDEN_CACHE_DIR.resolve("wrapper").resolve("gaiden-" + getVersion() + ".zip");
    }

    private URL getDistributionUrl() throws MalformedURLException {
        return new URL(gaidenWrapperProperties.getProperty("distributionUrl"));
    }

    private void loadBuildProperties() {
        buildProperties = new Properties();
        try (InputStream in = GaidenWrapper.class.getResourceAsStream("/build-receipt.properties")) {
            buildProperties.load(in);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void lodGaidenWrapperProperties() {
        gaidenWrapperProperties = new Properties();
        try {
            Path wrapperProperties = Paths.get(System.getProperty("app.home"), "wrapper/gaiden-wrapper.properties");
            if (Files.exists(wrapperProperties)) {
                gaidenWrapperProperties.load(Files.newInputStream(wrapperProperties));
                return;
            }

            try (InputStream in = GaidenWrapper.class.getResourceAsStream("gaiden-wrapper.properties")) {
                gaidenWrapperProperties.load(in);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
