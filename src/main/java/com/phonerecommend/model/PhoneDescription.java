package com.phonerecommend.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Lớp chứa mô tả chi tiết của điện thoại - FIXED
 */
public class PhoneDescription {
    private Map<String, String> attributes;

    public PhoneDescription() {
        this.attributes = new HashMap<>();
    }

    public void setAttribute(String key, String value) {
        if (attributes == null) {
            attributes = new HashMap<>();
        }
        attributes.put(key, value);
    }

    public String getAttribute(String key) {
        if (attributes == null) {
            return "";
        }
        return attributes.getOrDefault(key, "");
    }

    public Map<String, String> getAllAttributes() {
        if (attributes == null) {
            return new HashMap<>();
        }
        return new HashMap<>(attributes);
    }

    // Các phương thức tiện ích để truy cập thuộc tính phổ biến
    public String getScreenSize() {
        return getAttribute("Kích thước màn hình");
    }

    public String getScreenTechnology() {
        return getAttribute("Công nghệ màn hình");
    }

    public String getScreenResolution() {
        return getAttribute("Độ phân giải màn hình");
    }

    public String getRefreshRate() {
        return getAttribute("Tần số quét");
    }

    public String getRearCamera() {
        return getAttribute("Camera sau");
    }

    public String getFrontCamera() {
        return getAttribute("Camera trước");
    }

    public String getChipset() {
        return getAttribute("Chipset");
    }

    public String getRam() {
        return getAttribute("Dung lượng RAM");
    }

    public String getStorage() {
        return getAttribute("Bộ nhớ trong");
    }

    public String getBattery() {
        return getAttribute("Pin");
    }

    public String getSpecialFeatures() {
        return getAttribute("Tính năng đặc biệt");
    }

    public String getCameraFeatures() {
        return getAttribute("Tính năng camera");
    }

    public String getNetworkSupport() {
        return getAttribute("Hỗ trợ mạng");
    }

    public String getTechnologiesAndUtilities() {
        return getAttribute("Công nghệ - Tiện ích");
    }

    // Thêm method debug
    public void debugPrint() {
        System.out.println("=== DEBUG PHONE DESCRIPTION ===");
        System.out.println("Attributes map size: " + (attributes != null ? attributes.size() : "NULL"));
        if (attributes != null && !attributes.isEmpty()) {
            for (Map.Entry<String, String> entry : attributes.entrySet()) {
                System.out.println("Key: '" + entry.getKey() + "' -> Value: '" +
                        (entry.getValue() != null ? entry.getValue().substring(0, Math.min(50, entry.getValue().length())) + "..." : "NULL") + "'");
            }
        }
        System.out.println("===============================");
    }
}