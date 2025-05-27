package com.phonerecommend.test;

import com.phonerecommend.model.Phone;
import com.phonerecommend.repository.PhoneRepository;
import com.phonerecommend.repository.RepositoryFactory;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * Test to debug attributes in description
 */
class DebugAttributesTest {

    public static void main(String[] args) {
        System.out.println("=== DEBUG ATTRIBUTES TEST ===\n");

        try {
            // Setup repository
            RepositoryFactory.setRepositoryType(RepositoryFactory.RepositoryType.LOCAL_JSON);
            PhoneRepository repo = RepositoryFactory.getPhoneRepository();
            List<Phone> phones = repo.getAllPhones();

            System.out.println("Number of phones: " + phones.size());

            if (phones.isEmpty()) {
                System.out.println("No phones found!");
                return;
            }

            // Collect all unique attribute keys
            Set<String> allKeys = new TreeSet<>(); // TreeSet to sort alphabetically

            for (Phone phone : phones) {
                Map<String, String> attributes = phone.getDescription().getAllAttributes();
                allKeys.addAll(attributes.keySet());
            }

            System.out.println("\n=== ALL UNIQUE ATTRIBUTE KEYS ===");
            System.out.println("Total unique keys: " + allKeys.size());
            System.out.println();

            int count = 1;
            for (String key : allKeys) {
                System.out.printf("%2d. \"%s\"%n", count++, key);
            }

            // Show attributes for each phone
            System.out.println("\n=== ATTRIBUTES BY PHONE ===");
            for (int i = 0; i < phones.size(); i++) {
                Phone phone = phones.get(i);
                System.out.println("\n" + (i+1) + ". " + phone.getName());

                Map<String, String> attributes = phone.getDescription().getAllAttributes();
                System.out.println("   Attribute count: " + attributes.size());

                // Show some specific attributes we care about
                String[] importantKeys = {
                        "Dung lượng RAM", "Dung luong RAM", "RAM",
                        "Bộ nhớ trong", "Bo nho trong", "Storage",
                        "Pin", "Battery",
                        "Chipset", "Chip",
                        "Tính năng đặc biệt", "Tinh nang dac biet",
                        "Camera sau", "Camera truoc"
                };

                for (String key : importantKeys) {
                    String value = attributes.get(key);
                    if (value != null) {
                        System.out.printf("   ✓ %s: %s%n", key,
                                value.length() > 50 ? value.substring(0, 50) + "..." : value);
                    }
                }
            }

            // Test specific filters
            System.out.println("\n=== TESTING SPECIFIC FILTERS ===");

            // Test RAM filter
            for (Phone phone : phones) {
                String ram = phone.getDescription().getAttribute("Dung lượng RAM");
                System.out.println(phone.getName() + " - RAM: " + ram);
            }

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}