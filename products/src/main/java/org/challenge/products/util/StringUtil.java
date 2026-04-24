package org.challenge.products.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public final class StringUtil {

    private StringUtil() {

    }

    public static String readString(InputStream inputStream) {
        try {
            String string = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            return string.isEmpty() ? null : string;
        } catch (IOException e) {
            throw new RuntimeException("Error reading input stream: " + e.getMessage(), e);
        }
    }


}
