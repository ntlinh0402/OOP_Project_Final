package com.phonerecommend.test;

import com.phonerecommend.repository.PhoneRepository;
import com.phonerecommend.repository.RepositoryFactory;
import com.phonerecommend.model.Phone;

import java.io.File;
import java.util.List;

/**
 * Test to read data from existing JSON file
 */
public class ReadExistingJsonTest {

    public static void main(String[] args) {
        System.out.println("=== TEST READ DATA FROM EXISTING JSON FILE ===\n");

        // Step 1: Find JSON file
        System.out.println("1. Looking for phones.json...");
        String foundPath = findExistingJsonFile();

        if (foundPath == null) {
            System.out.println("ERROR: Cannot find phones.json file");
            System.out.println("Please ensure the file exists at one of these locations:");
            System.out.println("  - data/phones.json");
            System.out.println("  - src/main/resources/data/phones.json");
            System.out.println("  - phones.json");
            return;
        }

        System.out.println("FOUND file: " + foundPath + "\n");

        // Step 2: Configure repository to read that file
        try {
            RepositoryFactory.reset(); // Reset to ensure re-reading
            RepositoryFactory.setRepositoryType(RepositoryFactory.RepositoryType.LOCAL_JSON);
            RepositoryFactory.setLocalJsonPath(foundPath);

            // Step 3: Get repository and read data
            System.out.println("2. Reading data from file...");
            PhoneRepository repository = RepositoryFactory.getPhoneRepository();
            List<Phone> phones = repository.getAllPhones();

            System.out.println("SUCCESS: Read completed!");
            System.out.println("Number of phones: " + phones.size() + "\n");

            // Step 4: Display data
            if (phones.isEmpty()) {
                System.out.println("WARNING: JSON file is empty or contains no valid data");
            } else {
                System.out.println("3. Phone list from file:");
                System.out.println("=" + "=".repeat(80));

                for (int i = 0; i < phones.size(); i++) {
                    Phone phone = phones.get(i);
                    System.out.printf("[%d] %s%n", i + 1, phone.getName());
                    System.out.printf("    Price: %s VND%n", String.format("%,d", (long)phone.getPrice()));

                    if (phone.getLink() != null) {
                        System.out.printf("    Link: %s%n", phone.getLink());
                    }

                    // Display some important specs
                    String chipset = phone.getDescription().getAttribute("Chipset");
                    String ram = phone.getDescription().getAttribute("Dung luong RAM");
                    if (ram == null) ram = phone.getDescription().getAttribute("Dung lượng RAM");
                    String storage = phone.getDescription().getAttribute("Bo nho trong");
                    if (storage == null) storage = phone.getDescription().getAttribute("Bộ nhớ trong");

                    if (chipset != null && !chipset.isEmpty()) {
                        System.out.printf("    Chipset: %s%n", chipset);
                    }
                    if (ram != null && !ram.isEmpty()) {
                        System.out.printf("    RAM: %s%n", ram);
                    }
                    if (storage != null && !storage.isEmpty()) {
                        System.out.printf("    Storage: %s%n", storage);
                    }

                    System.out.println(); // Empty line
                }

                System.out.println("=" + "=".repeat(80));
                System.out.println("SUCCESS: Data read successfully!");

                // Show total attributes count
                int totalAttributes = 0;
                for (Phone phone : phones) {
                    totalAttributes += phone.getDescription().getAllAttributes().size();
                }
                System.out.println("Total attributes loaded: " + totalAttributes);
            }

        } catch (Exception e) {
            System.err.println("ERROR READING DATA:");
            System.err.println("   " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Find existing JSON file
     */
    private static String findExistingJsonFile() {
        String[] possiblePaths = {
                "data/phones.json",
                "src/main/resources/data/phones.json",
                "phones.json",
                "resources/data/phones.json",
                "../data/phones.json",
                "./data/phones.json"
        };

        for (String path : possiblePaths) {
            File file = new File(path);
            System.out.println("   Checking: " + file.getAbsolutePath());

            if (file.exists() && file.isFile() && file.length() > 0) {
                System.out.println("   FOUND valid file: " + path + " (" + file.length() + " bytes)");
                return path;
            } else if (file.exists()) {
                System.out.println("   WARNING: File exists but empty: " + path);
            } else {
                System.out.println("   NOT FOUND: " + path);
            }
        }

        return null;
    }
}