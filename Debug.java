import java.io.*;
import java.nio.file.*;

public class Debug {
    public static void main(String[] args) {
        System.out.println("=== DEBUG FILE CHECK ===");

        // Check current directory
        System.out.println("Current directory: " + System.getProperty("user.dir"));

        // Check multiple paths
        String[] paths = {
                "data/phones.json",
                "src/main/resources/data/phones.json",
                "phones.json",
                "../data/phones.json"
        };

        for (String path : paths) {
            File f = new File(path);
            System.out.println("\nPath: " + path);
            System.out.println("  Absolute: " + f.getAbsolutePath());
            System.out.println("  Exists: " + f.exists());
            System.out.println("  Size: " + (f.exists() ? f.length() + " bytes" : "N/A"));
            System.out.println("  Can read: " + (f.exists() ? f.canRead() : "N/A"));

            if (f.exists() && f.length() > 0) {
                try {
                    // Read first few characters
                    String content = new String(Files.readAllBytes(f.toPath()));
                    System.out.println("  First 100 chars: " + content.substring(0, Math.min(100, content.length())));
                    System.out.println("  Last 100 chars: " + content.substring(Math.max(0, content.length()-100)));

                    // Check if valid JSON array
                    String trimmed = content.trim();
                    System.out.println("  Starts with [: " + trimmed.startsWith("["));
                    System.out.println("  Ends with ]: " + trimmed.endsWith("]"));

                } catch (Exception e) {
                    System.out.println("  ERROR reading: " + e.getMessage());
                }
            }
        }
    }
}