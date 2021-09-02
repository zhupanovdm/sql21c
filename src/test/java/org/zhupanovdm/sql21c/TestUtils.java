package org.zhupanovdm.sql21c;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class TestUtils {
    private static final String RESOURCE_FOLDER = "src/test/resources";

    public static String resource(String name) {
        try {
            return Files.readString(Path.of(RESOURCE_FOLDER, name));
        } catch (IOException e) {
            throw new IllegalStateException("Cannot load resource", e);
        }
    }

}
