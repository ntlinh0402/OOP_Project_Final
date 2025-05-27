package com.phonerecommend.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Test to read and display raw content of JSON file
 */
public class ReadRawJsonTest {

    public static void main(String[] args) {
        System.out.println("=== TEST READ RAW CONTENT OF JSON FILE ===\n");

        // Find JSON file
        String[] possiblePaths = {
                "data/phones.json",
                "src/main/resources/data/phones.json",
                "phones.json"
        };

        String foundPath = null;
        File foundFile = null;

        for (String path : possiblePaths) {
            File file = new File(path);
            if (file.exists() && file.length() > 0) {
                foundPath = path;
                foundFile = file;
                break;
            }
        }

        if (foundPath == null) {
            System.out.println("ERROR: CANNOT FIND VALID JSON FILE");
            System.out.println("\nFiles checked:");
            for (String path : possiblePaths) {
                File file = new File(path);
                System.out.printf("  %s - %s%n", path,
                        file.exists() ?
                                (file.length() > 0 ? "Exists (" + file.length() + " bytes)" : "Exists but empty")
                                : "Does not exist");
            }
            return;
        }

        // Display file information
        System.out.println("FOUND file: " + foundPath);
        System.out.println("  Full path: " + foundFile.getAbsolutePath());
        System.out.println("  Size: " + foundFile.length() + " bytes");
        System.out.println("  Readable: " + foundFile.canRead());
        System.out.println();

        // Read and display content
        try {
            System.out.println("=== JSON FILE CONTENT ===");
            String content = readFileContent(foundFile);

            if (content.isEmpty()) {
                System.out.println("ERROR: FILE IS EMPTY!");
                return;
            }

            System.out.println("Content length: " + content.length() + " characters");
            System.out.println();

            // Display first 500 characters
            if (content.length() > 500) {
                System.out.println("--- FIRST 500 CHARACTERS ---");
                System.out.println(content.substring(0, 500) + "...");
                System.out.println();
            } else {
                System.out.println("--- FULL CONTENT ---");
                System.out.println(content);
                System.out.println();
            }

            // Analyze JSON structure
            analyzeJsonStructure(content);

        } catch (IOException e) {
            System.err.println("ERROR READING FILE: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Read entire file content
     */
    private static String readFileContent(File file) throws IOException {
        StringBuilder content = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        }

        return content.toString();
    }

    /**
     * Analyze basic JSON structure
     */
    private static void analyzeJsonStructure(String content) {
        System.out.println("=== JSON STRUCTURE ANALYSIS ===");

        // Check basic format
        content = content.trim();
        boolean isArray = content.startsWith("[") && content.endsWith("]");
        boolean isObject = content.startsWith("{") && content.endsWith("}");

        if (isArray) {
            System.out.println("Format: JSON Array (starts with [ and ends with ])");
        } else if (isObject) {
            System.out.println("Format: JSON Object (starts with { and ends with })");
        } else {
            System.out.println("ERROR: Invalid format! JSON must start with [ or {");
            return;
        }

        // Count important characters
        int openBraces = countChar(content, '{');
        int closeBraces = countChar(content, '}');
        int openBrackets = countChar(content, '[');
        int closeBrackets = countChar(content, ']');
        int quotes = countChar(content, '"');

        System.out.println("Important character counts:");
        System.out.println("  { (open object): " + openBraces);
        System.out.println("  } (close object): " + closeBraces);
        System.out.println("  [ (open array): " + openBrackets);
        System.out.println("  ] (close array): " + closeBrackets);
        System.out.println("  \" (quotes): " + quotes);

        // Check balance
        boolean balanced = (openBraces == closeBraces) && (openBrackets == closeBrackets);
        System.out.println("Bracket balance: " + (balanced ? "BALANCED" : "NOT BALANCED"));

        // Estimate number of objects if it's an array
        if (isArray && balanced) {
            // Count objects in array (estimate by counting { at first level)
            int estimatedObjects = countTopLevelObjects(content);
            System.out.println("Estimated objects in array: " + estimatedObjects);
        }

        System.out.println();
    }

    /**
     * Count character occurrences
     */
    private static int countChar(String content, char c) {
        int count = 0;
        for (char ch : content.toCharArray()) {
            if (ch == c) count++;
        }
        return count;
    }

    /**
     * Count objects at first level of array
     */
    private static int countTopLevelObjects(String content) {
        int count = 0;
        int level = 0;
        boolean inString = false;
        boolean escaped = false;

        for (int i = 0; i < content.length(); i++) {
            char c = content.charAt(i);

            if (escaped) {
                escaped = false;
                continue;
            }

            if (c == '\\') {
                escaped = true;
                continue;
            }

            if (c == '"') {
                inString = !inString;
                continue;
            }

            if (!inString) {
                if (c == '{') {
                    if (level == 1) { // Level 1 means we're inside the main array
                        count++;
                    }
                    level++;
                } else if (c == '}') {
                    level--;
                }
            }
        }

        return count;
    }
}