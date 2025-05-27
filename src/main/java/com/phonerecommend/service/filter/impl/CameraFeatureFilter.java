package com.phonerecommend.service.filter.impl;

import com.phonerecommend.model.Phone;
import com.phonerecommend.service.filter.AbstractPhoneFilter;

import java.util.Arrays;
import java.util.List;

/**
 * Bộ lọc điện thoại theo tính năng camera với debug logging
 */
public class CameraFeatureFilter extends AbstractPhoneFilter {

    public enum CameraFeature {
        PORTRAIT("Chụp xóa phông", "xoa phong", "bokeh", "portrait", "xóa phông", "blur", "portrait mode"),
        WIDE_ANGLE("Chụp góc rộng", "goc rong", "ultrawide", "wide", "góc rộng", "ultra wide", "wide angle"),
        VIDEO_4K("Quay video 4K", "4K", "4k", "ultra hd"),
        STABILIZATION("Chống rung", "chong rung", "OIS", "chống rung", "optical stabilization", "image stabilization"),
        ZOOM("Chụp zoom xa", "zoom", "telephoto", "tele", "zoom xa"),
        NIGHT_MODE("Chụp đêm", "chup dem", "night mode", "chụp đêm", "night", "low light", "ban dem"),
        MACRO("Chụp macro", "macro", "close up"),
        AI_CAMERA("Camera AI", "AI", "ai camera", "trí tuệ nhân tạo", "artificial intelligence"),
        MOTION_PHOTO("Chụp ảnh chuyển động", "chuyen dong", "motion", "chuyển động", "motion photo", "live photo");

        private final String description;
        private final List<String> keywords;

        CameraFeature(String description, String... keywords) {
            this.description = description;
            this.keywords = Arrays.asList(keywords);
        }

        public String getDescription() {
            return description;
        }

        public List<String> getKeywords() {
            return keywords;
        }
    }

    private final List<CameraFeature> features;
    private static final boolean DEBUG_MODE = true; // Bật/tắt debug

    public CameraFeatureFilter(CameraFeature... features) {
        super("camera_features", buildDescription(features));
        this.features = Arrays.asList(features);

        if (DEBUG_MODE) {
            System.out.println("=== KHỞI TẠO CAMERA FILTER ===");
            System.out.println("Các tính năng cần tìm:");
            for (CameraFeature feature : features) {
                System.out.println("- " + feature.getDescription() + ": " + feature.getKeywords());
            }
            System.out.println("===========================");
        }
    }

    private static String buildDescription(CameraFeature[] features) {
        if (features.length == 0) {
            return "Lọc theo tính năng camera";
        }

        StringBuilder sb = new StringBuilder("Tính năng camera: ");
        for (int i = 0; i < features.length; i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(features[i].getDescription());
        }
        return sb.toString();
    }

    @Override
    protected boolean isPhoneMatched(Phone phone) {
        if (DEBUG_MODE) {
            System.out.println("\n--- KIỂM TRA: " + phone.getName() + " ---");
        }

        String cameraInfo = buildCameraInfo(phone);

        if (DEBUG_MODE) {
            System.out.println("Thông tin camera đã chuẩn hóa: " + cameraInfo);
        }

        // Phải thỏa mãn TẤT CẢ các tính năng được chọn
        for (CameraFeature feature : features) {
            boolean hasFeature = hasFeature(cameraInfo, feature);

            if (DEBUG_MODE) {
                System.out.println("Tính năng '" + feature.getDescription() + "': " +
                        (hasFeature ? "✓ CÓ" : "✗ KHÔNG"));
            }

            if (!hasFeature) {
                if (DEBUG_MODE) {
                    System.out.println("→ LOẠI BỎ: Thiếu tính năng " + feature.getDescription());
                }
                return false;
            }
        }

        if (DEBUG_MODE) {
            System.out.println("→ ✓ THỎA MÃN TẤT CẢ ĐIỀU KIỆN");
        }
        return true;
    }

    /**
     * Kết hợp tất cả thông tin camera thành một chuỗi để tìm kiếm
     */
    private String buildCameraInfo(Phone phone) {
        StringBuilder cameraInfo = new StringBuilder();

        // Thêm thông tin camera sau
        String rearCamera = phone.getDescription().getRearCamera();
        if (rearCamera != null && !rearCamera.trim().isEmpty()) {
            cameraInfo.append(rearCamera).append(" ");
            if (DEBUG_MODE) {
                System.out.println("Camera sau: " + rearCamera);
            }
        }

        // Thêm tính năng camera
        String cameraFeatures = phone.getDescription().getCameraFeatures();
        if (cameraFeatures != null && !cameraFeatures.trim().isEmpty()) {
            cameraInfo.append(cameraFeatures).append(" ");
            if (DEBUG_MODE) {
                System.out.println("Tính năng camera: " + cameraFeatures);
            }
        }

        // Thêm thông tin quay video
        String videoInfo = phone.getDescription().getAttribute("Quay video");
        if (videoInfo != null && !videoInfo.trim().isEmpty()) {
            cameraInfo.append(videoInfo).append(" ");
            if (DEBUG_MODE) {
                System.out.println("Quay video: " + videoInfo);
            }
        }

        // Thêm camera trước
        String frontCamera = phone.getDescription().getFrontCamera();
        if (frontCamera != null && !frontCamera.trim().isEmpty()) {
            cameraInfo.append(frontCamera).append(" ");
            if (DEBUG_MODE) {
                System.out.println("Camera trước: " + frontCamera);
            }
        }

        // Thêm các thông tin khác có thể chứa tính năng camera
        String tinhNangDacBiet = phone.getDescription().getSpecialFeatures();
        if (tinhNangDacBiet != null && !tinhNangDacBiet.trim().isEmpty()) {
            cameraInfo.append(tinhNangDacBiet).append(" ");
            if (DEBUG_MODE) {
                System.out.println("Tính năng đặc biệt: " + tinhNangDacBiet);
            }
        }

        return normalizeText(cameraInfo.toString());
    }

    /**
     * Chuẩn hóa text để tìm kiếm dễ dàng hơn
     */
    private String normalizeText(String text) {
        if (text == null || text.trim().isEmpty()) return "";

        String normalized = text.toLowerCase()
                .replace("ă", "a").replace("â", "a").replace("á", "a")
                .replace("à", "a").replace("ả", "a").replace("ã", "a").replace("ạ", "a")
                .replace("ê", "e").replace("é", "e").replace("è", "e")
                .replace("ẻ", "e").replace("ẽ", "e").replace("ẹ", "e")
                .replace("ô", "o").replace("ơ", "o").replace("ó", "o")
                .replace("ò", "o").replace("ỏ", "o").replace("õ", "o").replace("ọ", "o")
                .replace("ư", "u").replace("ú", "u").replace("ù", "u")
                .replace("ủ", "u").replace("ũ", "u").replace("ụ", "u")
                .replace("í", "i").replace("ì", "i").replace("ỉ", "i")
                .replace("ĩ", "i").replace("ị", "i")
                .replace("ý", "y").replace("ỳ", "y").replace("ỷ", "y")
                .replace("ỹ", "y").replace("ỵ", "y")
                .replace("đ", "d")
                .trim();

        return normalized;
    }

    /**
     * Kiểm tra xem có tính năng camera trong thông tin không
     */
    private boolean hasFeature(String cameraInfo, CameraFeature feature) {
        if (DEBUG_MODE) {
            System.out.println("  Đang tìm keywords cho '" + feature.getDescription() + "':");
        }

        for (String keyword : feature.getKeywords()) {
            String normalizedKeyword = normalizeText(keyword);

            if (DEBUG_MODE) {
                System.out.println("    - Tìm '" + keyword + "' (chuẩn hóa: '" + normalizedKeyword + "')");
            }

            if (cameraInfo.contains(normalizedKeyword)) {
                if (DEBUG_MODE) {
                    System.out.println("      → ✓ TÌM THẤY!");
                }
                return true;
            }
        }

        if (DEBUG_MODE) {
            System.out.println("      → ✗ Không tìm thấy keyword nào");
        }
        return false;
    }

    /**
     * Static method để test một điện thoại cụ thể
     */
    public static void testPhoneFeatures(Phone phone, CameraFeature... features) {
        System.out.println("\n==================== TEST PHONE ====================");
        System.out.println("Điện thoại: " + phone.getName());
        System.out.println("==================================================");

        CameraFeatureFilter filter = new CameraFeatureFilter(features);
        boolean result = filter.isPhoneMatched(phone);

        System.out.println("==================================================");
        System.out.println("KẾT QUẢ CUỐI CÙNG: " + (result ? "THỎA MÃN" : "KHÔNG THỎA MÃN"));
        System.out.println("==================================================\n");
    }

    /**
     * Tắt debug mode
     */
    public static void setDebugMode(boolean enabled) {
        // Bạn có thể thay đổi giá trị DEBUG_MODE ở đây nếu muốn control động
    }
}