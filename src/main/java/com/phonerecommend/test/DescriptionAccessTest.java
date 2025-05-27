package com.phonerecommend.test;

import com.phonerecommend.model.Phone;
import com.phonerecommend.model.PhoneDescription;
import com.phonerecommend.repository.PhoneRepository;
import com.phonerecommend.repository.RepositoryFactory;

import java.util.List;
import java.util.Map;

/**
 * Test đơn giản để kiểm tra xem có truy cập được vào description không
 */
public class DescriptionAccessTest {

    public static void main(String[] args) {
        // Khởi tạo repository
        RepositoryFactory.setRepositoryType(RepositoryFactory.RepositoryType.LOCAL_JSON);
        RepositoryFactory.setLocalJsonPath("data/phones.json");
        PhoneRepository repository = RepositoryFactory.getPhoneRepository();

        // Lấy tất cả điện thoại
        List<Phone> phones = repository.getAllPhones();

        System.out.println("=== KIỂM TRA TRUY CẬP DESCRIPTION ===");
        System.out.println("Tổng số điện thoại: " + phones.size());

        if (phones.isEmpty()) {
            System.out.println("❌ KHÔNG CÓ ĐIỆN THOẠI NÀO! Kiểm tra file JSON.");
            return;
        }

        // Test với điện thoại đầu tiên
        Phone firstPhone = phones.get(0);
        System.out.println("\n📱 Điện thoại test: " + firstPhone.getName());

        // Kiểm tra description có null không
        PhoneDescription desc = firstPhone.getDescription();
        if (desc == null) {
            System.out.println("❌ DESCRIPTION LÀ NULL!");
            return;
        }

        System.out.println("✓ Description không null");

        // Kiểm tra tất cả attributes
        Map<String, String> allAttrs = desc.getAllAttributes();
        System.out.println("Tổng số attributes: " + allAttrs.size());

        // In ra các keys có sẵn
        System.out.println("\n=== TẤT CẢ KEYS TRONG DESCRIPTION ===");
        for (String key : allAttrs.keySet()) {
            System.out.println("Key: '" + key + "'");
        }

        // Kiểm tra cụ thể các key camera
        System.out.println("\n=== KIỂM TRA KEYS CAMERA ===");
        testKey(desc, "Camera sau");
        testKey(desc, "Tính năng camera");
        testKey(desc, "Camera trước");
        testKey(desc, "Quay video");

        // Thử với methods của PhoneDescription
        System.out.println("\n=== KIỂM TRA METHODS CỦA PHONEDESCRIPTION ===");
        System.out.println("getRearCamera(): " + desc.getRearCamera());
        System.out.println("getCameraFeatures(): " + desc.getCameraFeatures());
        System.out.println("getFrontCamera(): " + desc.getFrontCamera());

        // Test với nhiều điện thoại
        System.out.println("\n=== KIỂM TRA VỚI 3 ĐIỆN THOẠI ĐẦU ===");
        for (int i = 0; i < Math.min(3, phones.size()); i++) {
            Phone phone = phones.get(i);
            System.out.println("\n" + (i+1) + ". " + phone.getName());

            String cameraRear = phone.getDescription().getAttribute("Camera sau");
            String cameraFeature = phone.getDescription().getAttribute("Tính năng camera");

            System.out.println("   Camera sau: " + (cameraRear != null ? "CÓ DỮ LIỆU" : "NULL/TRỐNG"));
            System.out.println("   Tính năng camera: " + (cameraFeature != null ? "CÓ DỮ LIỆU" : "NULL/TRỐNG"));

            if (cameraRear != null) {
                System.out.println("   Camera sau (50 ký tự đầu): " +
                        cameraRear.substring(0, Math.min(50, cameraRear.length())) + "...");
            }
        }
    }

    private static void testKey(PhoneDescription desc, String key) {
        String value = desc.getAttribute(key);
        System.out.println("Key '" + key + "': " +
                (value != null && !value.trim().isEmpty() ? "CÓ DỮ LIỆU" : "TRỐNG/NULL"));
        if (value != null && !value.trim().isEmpty()) {
            System.out.println("  Nội dung (50 ký tự đầu): " +
                    value.substring(0, Math.min(50, value.length())) + "...");
        }
    }
}