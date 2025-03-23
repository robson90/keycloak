/*
 * Copyright 2025 Red Hat, Inc. and/or its affiliates
 * and other contributors as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.keycloak.themelocaleadder;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import java.util.Properties;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LocaleAdderTest {

    private static final String KEYCLOAK_SUB_FOLDER_PATH = "theme.base";
    private static final String THEME_PROPERTIES = "theme.properties";
    private static final String LOCALE_KEY = "locales";
    private static final String TEST_FOLDER = "src/test/resources/copied-test-folder";

    @BeforeAll
    static void beforeAll() {
        Path sourceFolder = Paths.get("src/test/resources/input-tests");
        Path destinationFolder = Paths.get(TEST_FOLDER);

        try {
            copyFolder(sourceFolder, destinationFolder);
            System.out.println("Folder copied successfully!");
        } catch (IOException e) {
            System.err.println("Failed to copy folder: " + e.getMessage());
        }
    }


    @Nested
    class MostBasicTest {
        private final String TEST_FOLDER = "most-basic-test";
        private final String TYPE_OF_THEME = "account";

        @Test
        void test() {
            var inputPath = getInputPath(TEST_FOLDER);
            var localeAdder = new LocaleAdder(inputPath);
            localeAdder.doItsThing();

            // Load theme.properties
            Properties props = loadPathProperties(TEST_FOLDER, TYPE_OF_THEME);
            assertTrue(props.containsKey(LOCALE_KEY));
            assertThat(props.get(LOCALE_KEY), equalTo("ar,ca,en"));
        }
    }
    @Nested
    class TwoFoldersTest {
        private static final String TEST_FOLDER = "test-two-folders";

        @BeforeAll
        static void beforeAll() {
            var inputPath = getInputPath(TEST_FOLDER);
            var localeAdder = new LocaleAdder(inputPath);
            localeAdder.doItsThing();
        }

        @Test
        void testAccount() {
            // Load theme.properties
            Properties props = loadPathProperties(TEST_FOLDER, "account");
            assertTrue(props.containsKey(LOCALE_KEY));
            assertThat(props.get(LOCALE_KEY), equalTo("en,ro,ru"));
        }
        @Test
        void testAdmin() {
            // Load theme.properties
            Properties props = loadPathProperties(TEST_FOLDER, "admin");
            assertTrue(props.containsKey(LOCALE_KEY));
            assertThat(props.get(LOCALE_KEY), equalTo("en,sl,uk"));
        }
    }

    private static Path getInputPath(String testFolder) {
        return Paths.get(String.format("%s/%s/%s", TEST_FOLDER, testFolder, KEYCLOAK_SUB_FOLDER_PATH)).toAbsolutePath();
    }

    private static Properties loadPathProperties(String testFolder, String typeOfTheme) {
        var path = Paths.get(String.format("%s/%s/%s/%s/%s", TEST_FOLDER, testFolder, KEYCLOAK_SUB_FOLDER_PATH, typeOfTheme, THEME_PROPERTIES));
        return PropertiesHelper.loadPropertiesFromPath(path);
    }



    private static void copyFolder(Path source, Path destination) throws IOException {
        // Traverse the directory and copy each file and folder
        Files.walkFileTree(source, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                // Create the directory in the destination if it doesn't exist
                Path targetDir = destination.resolve(source.relativize(dir));
                Files.createDirectories(targetDir);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                // Copy each file to the target directory
                Path targetFile = destination.resolve(source.relativize(file));
                Files.copy(file, targetFile, StandardCopyOption.REPLACE_EXISTING);
                return FileVisitResult.CONTINUE;
            }
        });
    }


}
