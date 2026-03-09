package com.wft.core.utilities;

public class ConverterUtils {
    public static String toCamelCase(String snake) {
        StringBuilder result = new StringBuilder();
        boolean upper = false;

        for (char c : snake.toCharArray()) {
            if (c == '_') {
                upper = true;
            } else {
                result.append(upper ? Character.toUpperCase(c) : c);
                upper = false;
            }
        }
        return result.toString();
    }
}