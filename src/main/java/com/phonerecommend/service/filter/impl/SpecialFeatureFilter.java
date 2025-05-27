package com.phonerecommend.service.filter.impl;

import com.phonerecommend.model.Phone;
import com.phonerecommend.service.filter.AbstractPhoneFilter;

import java.util.Arrays;
import java.util.List;

/**
 * Bộ lọc điện thoại theo tính năng đặc biệt - Fixed version
 */
public class SpecialFeatureFilter extends AbstractPhoneFilter {

    public enum SpecialFeature {
        SUPPORT_5G("Hỗ trợ 5G", "5g", "5G"),
        FINGERPRINT_SECURITY("Bảo mật vân tay", "vân tay", "fingerprint", "cảm biến vân tay"),
        FACE_RECOGNITION("Nhận diện khuôn mặt", "nhận diện khuôn mặt", "face id", "face recognition", "khuôn mặt"),
        WATER_RESISTANT("Kháng nước", "kháng nước", "ip67", "ip68", "chống nước"),
        DUST_RESISTANT("Kháng bụi", "kháng bụi", "ip65", "ip67", "ip68", "chống bụi"),
        AI_PHONE("Điện thoại AI", "ai", "trí tuệ nhân tạo", "điện thoại ai"),
        WIRELESS_CHARGING("Sạc không dây", "sạc không dây", "wireless charging", "magsafe"),
        STYLUS_PEN("Đi kèm bút cảm ứng", "bút cảm ứng", "s pen", "stylus");

        private final String displayName;
        private final List<String> keywords;

        SpecialFeature(String displayName, String... keywords) {
            this.displayName = displayName;
            this.keywords = Arrays.asList(keywords);
        }

        public String getDisplayName() {
            return displayName;
        }

        public List<String> getKeywords() {
            return keywords;
        }
    }

    private final List<SpecialFeature> features;

    /**
     * Constructor với danh sách tính năng đặc biệt
     * @param features Các tính năng đặc biệt cần lọc
     */
    public SpecialFeatureFilter(SpecialFeature... features) {
        super("special_features", buildDescription(features));
        this.features = Arrays.asList(features);
    }

    private static String buildDescription(SpecialFeature[] features) {
        if (features.length == 0) {
            return "Tính năng đặc biệt";
        }

        StringBuilder sb = new StringBuilder("Tính năng đặc biệt: ");
        for (int i = 0; i < features.length; i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(features[i].getDisplayName());
        }
        return sb.toString();
    }

    @Override
    protected boolean isPhoneMatched(Phone phone) {
        // FIXED: Kiểm tra trong nhiều attributes
        String specialFeatures = phone.getDescription().getAttribute("Tính năng đặc biệt");
        String techFeatures = phone.getDescription().getAttribute("Công nghệ - Tiện ích");
        String otherFeatures = phone.getDescription().getAttribute("Tiện ích khác");
        String waterDustRating = phone.getDescription().getAttribute("Chỉ số kháng nước, bụi");
        String chargingTech = phone.getDescription().getAttribute("Công nghệ sạc");

        // Combine tất cả features để tìm kiếm
        StringBuilder allFeatures = new StringBuilder();
        if (specialFeatures != null) allFeatures.append(specialFeatures).append(" ");
        if (techFeatures != null) allFeatures.append(techFeatures).append(" ");
        if (otherFeatures != null) allFeatures.append(otherFeatures).append(" ");
        if (waterDustRating != null) allFeatures.append(waterDustRating).append(" ");
        if (chargingTech != null) allFeatures.append(chargingTech).append(" ");

        if (allFeatures.length() == 0) {
            System.out.println("DEBUG: " + phone.getName() + " - No special features found");
            return false;
        }

        String featuresText = allFeatures.toString().toLowerCase();
        System.out.println("DEBUG: " + phone.getName() + " - Features: " + featuresText.substring(0, Math.min(100, featuresText.length())));

        // Phải thỏa mãn TẤT CẢ các tính năng được chọn
        for (SpecialFeature feature : features) {
            boolean hasFeature = false;

            for (String keyword : feature.getKeywords()) {
                if (featuresText.contains(keyword.toLowerCase())) {
                    hasFeature = true;
                    System.out.println("DEBUG: Found keyword '" + keyword + "' for " + feature.getDisplayName());
                    break;
                }
            }

            if (!hasFeature) {
                System.out.println("DEBUG: " + phone.getName() + " missing feature: " + feature.getDisplayName());
                return false; // Nếu không có một tính năng nào đó, trả về false
            }
        }

        return true; // Có tất cả các tính năng
    }

    /**
     * Tạo bộ lọc hỗ trợ 5G
     */
    public static SpecialFeatureFilter create5GFilter() {
        return new SpecialFeatureFilter(SpecialFeature.SUPPORT_5G);
    }

    /**
     * Tạo bộ lọc bảo mật vân tay
     */
    public static SpecialFeatureFilter createFingerprintFilter() {
        return new SpecialFeatureFilter(SpecialFeature.FINGERPRINT_SECURITY);
    }

    /**
     * Tạo bộ lọc nhận diện khuôn mặt
     */
    public static SpecialFeatureFilter createFaceRecognitionFilter() {
        return new SpecialFeatureFilter(SpecialFeature.FACE_RECOGNITION);
    }

    /**
     * Tạo bộ lọc kháng nước
     */
    public static SpecialFeatureFilter createWaterResistantFilter() {
        return new SpecialFeatureFilter(SpecialFeature.WATER_RESISTANT);
    }

    /**
     * Tạo bộ lọc kháng bụi
     */
    public static SpecialFeatureFilter createDustResistantFilter() {
        return new SpecialFeatureFilter(SpecialFeature.DUST_RESISTANT);
    }

    /**
     * Tạo bộ lọc điện thoại AI
     */
    public static SpecialFeatureFilter createAIPhoneFilter() {
        return new SpecialFeatureFilter(SpecialFeature.AI_PHONE);
    }

    /**
     * Tạo bộ lọc sạc không dây
     */
    public static SpecialFeatureFilter createWirelessChargingFilter() {
        return new SpecialFeatureFilter(SpecialFeature.WIRELESS_CHARGING);
    }

    /**
     * Tạo bộ lọc bút cảm ứng
     */
    public static SpecialFeatureFilter createStylusPenFilter() {
        return new SpecialFeatureFilter(SpecialFeature.STYLUS_PEN);
    }
}