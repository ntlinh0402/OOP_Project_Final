package com.phonerecommend.test;

import java.io.File;

/**
 * Very simple debug test
 */
public class SimpleDebugTest {

    public static void main(String[] args) {
        System.out.println("=== SIMPLE DEBUG TEST ===");

        // Test 1: Check working directory
        System.out.println("\n1. Working Directory:");
        System.out.println("   " + System.getProperty("user.dir"));

        // Test 2: Check if possible file paths exist
        System.out.println("\n2. Check if files exist:");
        String[] paths = {
                "data/phones.json",
                "src/main/resources/data/phones.json",
                "phones.json",
                "resources/data/phones.json"
        };

        for (String path : paths) {
            File file = new File(path);
            boolean exists = file.exists();
            long size = exists ? file.length() : 0;
            System.out.printf("   %-40s %s%s%n",
                    path,
                    exists ? "EXISTS" : "NOT FOUND",
                    exists ? " (" + size + " bytes)" : "");
        }

        // Test 3: Check resources
        System.out.println("\n3. Check resources:");
        String[] resourcePaths = {
                "/data/phones.json",
                "/phones.json"
        };

        for (String resourcePath : resourcePaths) {
            try {
                var resource = SimpleDebugTest.class.getResource(resourcePath);
                if (resource != null) {
                    System.out.println("   " + resourcePath + " FOUND in resources");
                    System.out.println("      URL: " + resource.toString());
                } else {
                    System.out.println("   " + resourcePath + " NOT FOUND in resources");
                }
            } catch (Exception e) {
                System.out.println("   " + resourcePath + " ERROR: " + e.getMessage());
            }
        }

        // Test 4: Check write permissions
        System.out.println("\n4. Check write permissions:");
        try {
            File testDir = new File("data");
            if (!testDir.exists()) {
                boolean created = testDir.mkdirs();
                System.out.println("   Create 'data' directory: " + (created ? "SUCCESS" : "FAILED"));
            } else {
                System.out.println("   'data' directory already exists");
            }

            File testFile = new File("data/test.txt");
            if (testFile.createNewFile()) {
                System.out.println("   Create test file: SUCCESS");
                testFile.delete(); // Delete test file
            } else {
                System.out.println("   Create test file: FAILED");
            }
        } catch (Exception e) {
            System.out.println("   Write permission check error: " + e.getMessage());
        }

        // Test 5: Class path info
        System.out.println("\n5. Class path info:");
        String classPath = System.getProperty("java.class.path");
        System.out.println("   " + classPath);

        System.out.println("\n=== END DEBUG TEST ===");
    }
}