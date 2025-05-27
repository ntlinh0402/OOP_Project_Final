package com.phonerecommend.service.filter.impl;

import com.phonerecommend.model.Phone;
import com.phonerecommend.service.filter.AbstractPhoneFilter;

/**
 * Lọc điện thoại theo nhu cầu chơi game
 */
public class GamingFilter extends AbstractPhoneFilter {

    public GamingFilter() {
        super("gaming", "Điện thoại phù hợp chơi game");
    }

    @Override
    protected boolean isPhoneMatched(Phone phone) {
        // Kiểm tra tính năng tối ưu game
        String techFeatures = phone.getDescription().getTechnologiesAndUtilities();
        return techFeatures != null &&
                (techFeatures.contains("Tối ưu game") || techFeatures.contains("Game Booster"));
    }
}