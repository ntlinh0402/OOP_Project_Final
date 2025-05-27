package com.phonerecommend.service.filter.impl;

import com.phonerecommend.model.Phone;
import com.phonerecommend.service.filter.AbstractPhoneFilter;

/**
 * Bộ lọc theo dung lượng RAM - Fixed version
 */
public class RamCapacityFilter extends AbstractPhoneFilter {
    private final int minRam;
    private final int maxRam;

    /**
     * Constructor với khoảng RAM
     * @param minRam RAM tối thiểu (GB)
     * @param maxRam RAM tối đa (GB), nếu <= 0 thì không giới hạn
     */
    public RamCapacityFilter(int minRam, int maxRam) {
        super("ram_capacity", buildDescription(minRam, maxRam));
        this.minRam = minRam;
        this.maxRam = maxRam;
    }

    private static String buildDescription(int minRam, int maxRam) {
        if (maxRam <= 0) {
            return "RAM từ " + minRam + "GB trở lên";
        } else if (minRam == maxRam) {
            return "RAM " + minRam + "GB";
        } else {
            return "RAM " + minRam + "GB-" + maxRam + "GB";
        }
    }

    @Override
    protected boolean isPhoneMatched(Phone phone) {
        // FIXED: Sử dụng key chính xác từ JSON
        String ramInfo = phone.getDescription().getAttribute("Dung lượng RAM");
        if (ramInfo == null || ramInfo.isEmpty()) {
            System.out.println("DEBUG: " + phone.getName() + " - RAM info not found");
            return false;
        }

        try {
            // Debug: In ra giá trị RAM
            System.out.println("DEBUG: " + phone.getName() + " - RAM: " + ramInfo);

            // Trích xuất dung lượng RAM từ chuỗi (vd: "6 GB", "12 GB")
            String numericPart = ramInfo.replaceAll("[^0-9]", "");
            if (numericPart.isEmpty()) {
                return false;
            }

            int ram = Integer.parseInt(numericPart);
            System.out.println("DEBUG: Parsed RAM: " + ram + "GB");

            boolean satisfiesMinRam = ram >= minRam;
            boolean satisfiesMaxRam = maxRam <= 0 || ram <= maxRam;

            return satisfiesMinRam && satisfiesMaxRam;
        } catch (NumberFormatException e) {
            System.out.println("DEBUG: Error parsing RAM for " + phone.getName() + ": " + ramInfo);
            return false;
        }
    }

    /**
     * Phương thức factory tạo bộ lọc RAM 4-6GB
     */
    public static RamCapacityFilter create4To6GBFilter() {
        return new RamCapacityFilter(4, 6);
    }

    /**
     * Phương thức factory tạo bộ lọc RAM 8GB
     */
    public static RamCapacityFilter create8GBFilter() {
        return new RamCapacityFilter(8, 8);
    }

    /**
     * Phương thức factory tạo bộ lọc RAM 8-12GB
     */
    public static RamCapacityFilter create8To12GBFilter() {
        return new RamCapacityFilter(8, 12);
    }

    /**
     * Phương thức factory tạo bộ lọc RAM từ 12GB trở lên
     */
    public static RamCapacityFilter create12GBPlusFilter() {
        return new RamCapacityFilter(12, 0);
    }
}