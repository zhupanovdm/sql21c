package com.zhupanovdm.sql21c.transform;

import com.github.vertical_blank.sqlformatter.SqlFormatter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class TestUtils {
    private static final String RESOURCE_FOLDER = "src/test/resources";

    public static Path resourcePath(String name) {
        return Path.of(RESOURCE_FOLDER, name);
    }

    public static String resource(String name) {
        try {
            return Files.readString(resourcePath(name));
        } catch (IOException e) {
            throw new IllegalStateException("Cannot load resource", e);
        }
    }

    public static String statement(Object stmt) {
        return SqlFormatter.format(stmt.toString());
    }
}
