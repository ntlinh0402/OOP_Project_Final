package com.phonerecommend.service.filter.impl;

import com.phonerecommend.model.Phone;
import com.phonerecommend.service.filter.AbstractPhoneFilter;

/**
 * Lọc điện thoại nhỏ gọn, dễ cầm
 */
public class CompactSizeFilter extends AbstractPhoneFilter {
    private static final double MAX_SCREEN_SIZE = 6.0; // inch
    private static final int MAX_WEIGHT = 200; // gram

    public CompactSizeFilter() {
        super("compact_size", "Điện thoại nhỏ gọn, dễ cầm");
    }

    @Override
    protected boolean isPhoneMatched(Phone phone) {
        return hasSmallScreen(phone) && hasLightWeight(phone);
    }

    private boolean hasSmallScreen(Phone phone) {
        String screenSizeInfo = phone.getDescription().getScreenSize();
        if (screenSizeInfo == null || screenSizeInfo.isEmpty()) {
            return false;
        }

        try {
            // Trích xuất kích thước màn hình từ chuỗi (vd: "6.1 inches")
            String numericPart = screenSizeInfo.replaceAll("[^0-9.]", "");
            double screenSize = Double.parseDouble(numericPart);
            return screenSize <= MAX_SCREEN_SIZE;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean hasLightWeight(Phone phone) {
        String weightInfo = phone.getDescription().getAttribute("Trọng lượng");
        if (weightInfo == null || weightInfo.isEmpty()) {
            return false;
        }

        try {
            // Trích xuất trọng lượng từ chuỗi (vd: "180 gram")
            String numericPart = weightInfo.replaceAll("[^0-9]", "");
            int weight = Integer.parseInt(numericPart);
            return weight <= MAX_WEIGHT;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}