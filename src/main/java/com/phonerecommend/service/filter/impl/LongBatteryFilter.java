package com.phonerecommend.service.filter.impl;

import com.phonerecommend.model.Phone;
import com.phonerecommend.service.filter.AbstractPhoneFilter;

/**
 * Lọc điện thoại theo pin trâu (>= 4000mAh)
 */
public class LongBatteryFilter extends AbstractPhoneFilter {
    private static final int MIN_BATTERY_CAPACITY = 4000;

    public LongBatteryFilter() {
        super("long_battery", "Pin trâu (>= 4000mAh)");
    }

    @Override
    protected boolean isPhoneMatched(Phone phone) {
        String batteryInfo = phone.getDescription().getAttribute("Pin");
        if (batteryInfo == null || batteryInfo.isEmpty()) {
            return false;
        }

        try {
            // Trích xuất dung lượng pin từ chuỗi (vd: "5000 mAh")
            String numericPart = batteryInfo.replaceAll("[^0-9]", "");
            int capacity = Integer.parseInt(numericPart);
            return capacity >= MIN_BATTERY_CAPACITY;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}