package com.phonerecommend.test;

import com.phonerecommend.model.Phone;
import com.phonerecommend.model.PhoneDescription;
import com.phonerecommend.repository.PhoneRepository;
import com.phonerecommend.repository.RepositoryFactory;
import com.phonerecommend.service.filter.impl.CameraFeatureFilter;
import com.phonerecommend.service.filter.impl.CameraFeatureFilter.CameraFeature;

import java.util.List;

/**
 * Class để test camera features filtering
 */
public class CameraFeatureTest {

    public static void main(String[] args) {
        // Khởi tạo repository
        RepositoryFactory.setRepositoryType(RepositoryFactory.RepositoryType.LOCAL_JSON);
        RepositoryFactory.setLocalJsonPath("data/phones.json");
        PhoneRepository repository = RepositoryFactory.getPhoneRepository();

        // Lấy tất cả điện thoại
        List<Phone> phones = repository.getAllPhones();

        System.out.println("=== KIỂM TRA DỮ LIỆU CAMERA CỦA CÁC ĐIỆN THOẠI ===\n");

        // In ra thông tin camera của một vài điện thoại đầu
        for (int i = 0; i < Math.min(3, phones.size()); i++) {
            Phone phone = phones.get(i);
            printPhoneCameraInfo(phone);
        }

        System.out.println("\n=== TEST FILTER CHỤP XÓA PHÔNG ===");
        CameraFeatureFilter portraitFilter = new CameraFeatureFilter(CameraFeature.PORTRAIT);
        testFilterWithAllPhones(phones, portraitFilter, "Chụp xóa phông");

        System.out.println("\n=== TEST FILTER QUAY VIDEO 4K ===");
        CameraFeatureFilter video4kFilter = new CameraFeatureFilter(CameraFeature.VIDEO_4K);
        testFilterWithAllPhones(phones, video4kFilter, "Quay video 4K");

        System.out.println("\n=== TEST FILTER CHỤP ĐÊM ===");
        CameraFeatureFilter nightFilter = new CameraFeatureFilter(CameraFeature.NIGHT_MODE);
        testFilterWithAllPhones(phones, nightFilter, "Chụp đêm");

        System.out.println("\n=== TEST FILTER KẾT HỢP (CHỤP XÓA PHÔNG + 4K) ===");
        CameraFeatureFilter combinedFilter = new CameraFeatureFilter(
                CameraFeature.PORTRAIT,
                CameraFeature.VIDEO_4K
        );
        testFilterWithAllPhones(phones, combinedFilter, "Chụp xóa phông + 4K");
    }

    private static void printPhoneCameraInfo(Phone phone) {
        System.out.println("📱 " + phone.getName());
        System.out.println("Camera sau: " + phone.getDescription().getRearCamera());
        System.out.println("Camera trước: " + phone.getDescription().getFrontCamera());
        System.out.println("Tính năng camera: " + phone.getDescription().getCameraFeatures());
        System.out.println("Quay video: " + phone.getDescription().getAttribute("Quay video"));
        System.out.println("Tính năng đặc biệt: " + phone.getDescription().getSpecialFeatures());
        System.out.println("---");
    }

    private static void testFilterWithAllPhones(List<Phone> phones, CameraFeatureFilter filter, String filterName) {
        System.out.println("Đang test filter: " + filterName);
        System.out.println("Total phones: " + phones.size());

        List<Phone> filteredPhones = filter.filter(phones);
        System.out.println("Phones matching filter: " + filteredPhones.size());

        if (!filteredPhones.isEmpty()) {
            System.out.println("Các điện thoại thỏa mãn:");
            for (Phone phone : filteredPhones) {
                System.out.println("✓ " + phone.getName());
            }
        } else {
            System.out.println("❌ Không có điện thoại nào thỏa mãn điều kiện");
        }
        System.out.println();
    }

    /**
     * Test một điện thoại cụ thể với chi tiết debug
     */
    public static void testSpecificPhone() {
        // Tạo dữ liệu điện thoại mẫu để test
        Phone iPhone = new Phone();
        iPhone.setName("iPhone 16 Pro Max Test");

        PhoneDescription desc = new PhoneDescription();
        desc.setAttribute("Camera sau", "Camera chính: 48MP, chụp xóa phông, chống rung quang học");
        desc.setAttribute("Tính năng camera", "Deep Fusion, HDR thông minh, Chế độ Ban Đêm, Chụp ảnh toàn cảnh");
        desc.setAttribute("Quay video", "4K@60fps, 4K@120fps, 1080p@240fps");
        desc.setAttribute("Camera trước", "12MP, TrueDepth");

        iPhone.setDescription(desc);

        // Test với nhiều features
        CameraFeatureFilter.testPhoneFeatures(iPhone,
                CameraFeature.PORTRAIT,    // Có: "chụp xóa phông"
                CameraFeature.VIDEO_4K,    // Có: "4K"
                CameraFeature.NIGHT_MODE,  // Có: "Chế độ Ban Đêm"
                CameraFeature.STABILIZATION, // Có: "chống rung"
                CameraFeature.MACRO        // Không có: macro
        );
    }
}