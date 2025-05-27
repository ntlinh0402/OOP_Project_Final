package org.example.util;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Simple JSON parser utility class
 */
public class SimpleJSON {

    /**
     * Parse JSON array from string
     * @param jsonString JSON string
     * @return List of objects
     */
    public static List<Object> parseJSONArray(String jsonString) {
        try {
            String trimmed = jsonString.trim();
            if (!trimmed.startsWith("[") || !trimmed.endsWith("]")) {
                throw new IllegalArgumentException("Invalid JSON array format");
            }

            List<Object> result = new ArrayList<>();
            String content = trimmed.substring(1, trimmed.length() - 1).trim();

            if (content.isEmpty()) {
                return result;
            }

            // Simple parse for nested objects
            int depth = 0;
            int start = 0;

            for (int i = 0; i < content.length(); i++) {
                char c = content.charAt(i);
                if (c == '{') {
                    depth++;
                } else if (c == '}') {
                    depth--;
                    if (depth == 0) {
                        String objectStr = content.substring(start, i + 1);
                        result.add(parseJSONObject(objectStr));

                        // Skip comma and whitespace
                        start = i + 1;
                        while (start < content.length() &&
                                (content.charAt(start) == ',' ||
                                        Character.isWhitespace(content.charAt(start)))) {
                            start++;
                        }
                        i = start - 1; // Will be incremented by loop
                    }
                }
            }

            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Parse JSON object from string
     * @param jsonString JSON object string
     * @return Map representing the object
     */
    public static Map<String, Object> parseJSONObject(String jsonString) {
        Map<String, Object> result = new HashMap<>();

        try {
            String trimmed = jsonString.trim();
            if (!trimmed.startsWith("{") || !trimmed.endsWith("}")) {
                return result;
            }

            String content = trimmed.substring(1, trimmed.length() - 1);

            // Pattern to match key-value pairs
            Pattern pattern = Pattern.compile("\"([^\"]+)\"\\s*:\\s*(.+?)(?=,\\s*\"|\\s*})");
            Matcher matcher = pattern.matcher(content);

            while (matcher.find()) {
                String key = matcher.group(1);
                String valueStr = matcher.group(2).trim();

                Object value = parseValue(valueStr);
                result.put(key, value);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * Parse a JSON value (string, number, boolean, object, array)
     */
    private static Object parseValue(String valueStr) {
        valueStr = valueStr.trim();

        // Handle different value types
        if (valueStr.startsWith("\"") && valueStr.endsWith("\"")) {
            // String value
            return valueStr.substring(1, valueStr.length() - 1);
        } else if (valueStr.equals("true") || valueStr.equals("false")) {
            // Boolean value
            return Boolean.parseBoolean(valueStr);
        } else if (valueStr.equals("null")) {
            // Null value
            return null;
        } else if (valueStr.startsWith("{")) {
            // Nested object
            return parseJSONObject(valueStr);
        } else if (valueStr.startsWith("[")) {
            // Array
            return parseJSONArray(valueStr);
        } else {
            // Try to parse as number
            try {
                if (valueStr.contains(".")) {
                    return Double.parseDouble(valueStr);
                } else {
                    return Long.parseLong(valueStr);
                }
            } catch (NumberFormatException e) {
                // If not a number, return as string
                return valueStr;
            }
        }
    }

    /**
     * Convert object to JSON string
     * @param obj Object to convert
     * @return JSON string
     */
    public static String toJSON(Object obj) {
        if (obj == null) {
            return "null";
        } else if (obj instanceof String) {
            return "\"" + escapeString((String) obj) + "\"";
        } else if (obj instanceof Number) {
            return obj.toString();
        } else if (obj instanceof Boolean) {
            return obj.toString();
        } else if (obj instanceof List) {
            List<?> list = (List<?>) obj;
            StringBuilder sb = new StringBuilder("[");
            for (int i = 0; i < list.size(); i++) {
                if (i > 0) sb.append(",");
                sb.append(toJSON(list.get(i)));
            }
            sb.append("]");
            return sb.toString();
        } else if (obj instanceof Map) {
            Map<?, ?> map = (Map<?, ?>) obj;
            StringBuilder sb = new StringBuilder("{");
            boolean first = true;
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                if (!first) sb.append(",");
                sb.append("\"").append(entry.getKey()).append("\":");
                sb.append(toJSON(entry.getValue()));
                first = false;
            }
            sb.append("}");
            return sb.toString();
        } else {
            return "\"" + obj.toString() + "\"";
        }
    }

    /**
     * Escape special characters in string
     */
    private static String escapeString(String str) {
        return str.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}