package com.phonerecommend.test;

import com.phonerecommend.model.Phone;
import com.phonerecommend.model.PhoneDescription;
import com.phonerecommend.repository.PhoneRepository;
import com.phonerecommend.repository.RepositoryFactory;

import java.util.List;

/**
 * Test chi tiết để debug vấn đề description
 */
public class DetailedDescriptionTest {

    public static void main(String[] args) {
        // Khởi tạo repository
        RepositoryFactory.setRepositoryType(RepositoryFactory.RepositoryType.LOCAL_JSON);
        RepositoryFactory.setLocalJsonPath("data/phones.json");
        PhoneRepository repository = RepositoryFactory.getPhoneRepository();

        List<Phone> phones = repository.getAllPhones();

        if (phones.isEmpty()) {
            System.out.println("❌ Không có điện thoại!");
            return;
        }

        // Test với điện thoại đầu tiên
        Phone phone = phones.get(0);
        System.out.println("=== PHONE: " + phone.getName() + " ===");

        // Debug description
        PhoneDescription desc = phone.getDescription();
        if (desc != null) {
            // Gọi method debug
            desc.debugPrint();

            // Test direct methods
            System.out.println("\n=== TEST DIRECT METHODS ===");
            System.out.println("getRearCamera(): '" + desc.getRearCamera() + "'");
            System.out.println("getCameraFeatures(): '" + desc.getCameraFeatures() + "'");
            System.out.println("getFrontCamera(): '" + desc.getFrontCamera() + "'");
            System.out.println("getChipset(): '" + desc.getChipset() + "'");
            System.out.println("getRam(): '" + desc.getRam() + "'");

            // Test getAttribute với các keys khác nhau
            System.out.println("\n=== TEST getAttribute ===");
            String[] testKeys = {
                    "Camera sau",
                    "Tính năng camera",
                    "Camera trước",
                    "Chipset",
                    "Dung lượng RAM",
                    "Bộ nhớ trong",
                    "Pin"
            };

            for (String key : testKeys) {
                String value = desc.getAttribute(key);
                System.out.println("getAttribute('" + key + "'): " +
                        (value != null && !value.isEmpty() ? "CÓ (" + value.length() + " chars)" : "TRỐNG"));
            }

            // Test manual creation để so sánh
            System.out.println("\n=== TEST MANUAL CREATION ===");
            PhoneDescription manualDesc = new PhoneDescription();
            manualDesc.setAttribute("Test Key", "Test Value");
            System.out.println("Manual desc attributes size: " + manualDesc.getAllAttributes().size());
            System.out.println("Manual desc get test: '" + manualDesc.getAttribute("Test Key") + "'");

        } else {
            System.out.println("❌ Description is null!");
        }

        // Test với CameraFeatureFilter đơn giản
        System.out.println("\n=== TEST SIMPLE CAMERA DETECTION ===");
        testSimpleCameraDetection(phone);
    }

    private static void testSimpleCameraDetection(Phone phone) {
        PhoneDescription desc = phone.getDescription();

        // Lấy raw data
        String cameraRear = desc.getAttribute("Camera sau");
        String cameraFeatures = desc.getAttribute("Tính năng camera");
        String video = desc.getAttribute("Quay video");

        System.out.println("Raw camera data:");
        System.out.println("1. Camera sau: " + (cameraRear != null ? cameraRear.length() + " chars" : "NULL"));
        System.out.println("2. Tính năng camera: " + (cameraFeatures != null ? cameraFeatures.length() + " chars" : "NULL"));
        System.out.println("3. Quay video: " + (video != null ? video.length() + " chars" : "NULL"));

        // Combine all
        String allCamera = (cameraRear != null ? cameraRear : "") + " " +
                (cameraFeatures != null ? cameraFeatures : "") + " " +
                (video != null ? video : "");

        System.out.println("Combined camera info: " + allCamera.length() + " chars");

        // Test simple searches
        String lowerAll = allCamera.toLowerCase();
        System.out.println("\n=== SIMPLE KEYWORD TESTS ===");
        System.out.println("Contains '4k': " + lowerAll.contains("4k"));
        System.out.println("Contains '4K': " + lowerAll.contains("4K"));
        System.out.println("Contains 'chân dung': " + lowerAll.contains("chân dung"));
        System.out.println("Contains 'chan dung': " + lowerAll.contains("chan dung"));
        System.out.println("Contains 'siêu rộng': " + lowerAll.contains("siêu rộng"));
        System.out.println("Contains 'sieu rong': " + lowerAll.contains("sieu rong"));

        // Print first 200 chars of combined text
        if (allCamera.length() > 0) {
            System.out.println("\nFirst 200 chars of combined camera info:");
            System.out.println("'" + allCamera.substring(0, Math.min(200, allCamera.length())) + "'");
        }
    }
}