package org.keycloak.themelocaleadder;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Properties;

public class PropertiesHelper {
    static Properties loadPropertiesFromPath(Path filePath) {
        Properties properties = new Properties();

        try (FileInputStream fis = new FileInputStream(filePath.toFile())) {
            // Load the .properties file from the given file path
            properties.load(fis);
        } catch (IOException e) {
            System.err.println("Error loading properties file from path: " + filePath + ". Details: " + e.getMessage());
        }

        return properties;
    }
}
