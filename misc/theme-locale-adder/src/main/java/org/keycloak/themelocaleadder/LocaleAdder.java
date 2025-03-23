package org.keycloak.themelocaleadder;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

public class LocaleAdder {

    private static final String THEME_PROPERTIES = "theme.properties";
    private static final String DEFAULT_LOCALE = "en";

    private Path absolutePath;

    public LocaleAdder(Path absolutePath) {
        Objects.requireNonNull(absolutePath);
        this.absolutePath = absolutePath;
    }

    public void doItsThing() {
        // Go through provided directory. If its more than on, do it in for each.
        List<Path> directories = Collections.emptyList();
        try {
            directories = getDirectories(absolutePath);
            System.out.println(directories);
        } catch (IOException ignore) {
        }
        directories.forEach(directory -> {
            System.out.println(directory);
            var path = Paths.get(String.format("%s/%s", directory.toAbsolutePath(), "messages"));
            List<String> locales = Collections.emptyList();
            try {
                locales = getLocales(getFiles(path));
                System.out.println(locales);
            } catch (IOException ignore) {
            }
            locales.add(DEFAULT_LOCALE);
            locales.sort(String::compareTo);
            Properties props = new Properties();
            props.put("locales", String.join(",", locales));
            var filePath = Paths.get(String.format("%s/%s", directory.toAbsolutePath(), THEME_PROPERTIES));
            try (FileOutputStream fos = new FileOutputStream(filePath.toFile())) {
                props.store(fos, "Configuration Settings"); // Add an optional comment
                System.out.println("Properties saved to: " + filePath);
            } catch (IOException e) {
                System.err.println("Error saving properties to file: " + e.getMessage());
            }
        });
    }

    private static List<Path> getDirectories(Path path) throws IOException {
        // Ensure the given path exists and is a directory
        if (!Files.exists(path) || !Files.isDirectory(path)) {
            throw new IllegalArgumentException("Path does not exist or is not a directory: " + path);
        }

        List<Path> directories = new ArrayList<>();

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(path)) {
            for (Path entry : stream) {
                if (Files.isDirectory(entry)) {
                    directories.add(entry);
                }
            }
        }
        return directories;
    }
    private static List<Path> getFiles(Path path) throws IOException {
        if (!Files.exists(path) || !Files.isDirectory(path)) {
            throw new IllegalArgumentException("Path does not exist or is a directory: " + path);
        }

        List<Path> files = new ArrayList<>();

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(path)) {
            for (Path entry : stream) {
                if (Files.isRegularFile(entry)) { // Check if it's a file
                    files.add(entry);
                }
            }
        }

        return files;
    }

    private static List<String> getLocales(List<Path> propertyFiles) {
        return propertyFiles.stream()
                .filter(file -> file.getFileName().toString().endsWith(".properties"))
                .map(file -> file.getFileName().toString())
                .map(fileName -> fileName.substring(fileName.lastIndexOf('_') + 1, fileName.lastIndexOf('.')))
                .collect(java.util.stream.Collectors.toList());

    }
}


