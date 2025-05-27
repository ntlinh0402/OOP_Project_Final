package com.phonerecommend.service.filter.impl;

import com.phonerecommend.model.Phone;
import com.phonerecommend.service.filter.AbstractPhoneFilter;

import java.util.Arrays;
import java.util.List;

/**
 * Bộ lọc điện thoại theo hãng - Fixed version
 */
public class BrandFilter extends AbstractPhoneFilter {
    private final List<String> brandNames;

    /**
     * Constructor với danh sách tên hãng
     * @param brandNames Tên các hãng cần lọc
     */
    public BrandFilter(String... brandNames) {
        super("brand", "Lọc theo hãng: " + String.join(", ", brandNames));
        this.brandNames = Arrays.asList(brandNames);
    }

    @Override
    protected boolean isPhoneMatched(Phone phone) {
        String phoneName = phone.getName();
        if (phoneName == null) return false;

        // Convert to lowercase để so sánh không phân biệt chữ hoa/thường
        phoneName = phoneName.toLowerCase();

        for (String brand : brandNames) {
            // Cả brand name cũng convert lowercase
            if (phoneName.contains(brand.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Phương thức factory tạo bộ lọc Samsung
     */
    public static BrandFilter createSamsungFilter() {
        return new BrandFilter("samsung", "galaxy");
    }

    /**
     * Phương thức factory tạo bộ lọc Apple - FIX CHỖ NÀY
     */
    public static BrandFilter createAppleFilter() {
        return new BrandFilter("iphone", "apple");  // iPhone là keyword chính
    }

    /**
     * Phương thức factory tạo bộ lọc Oppo
     */
    public static BrandFilter createOppoFilter() {
        return new BrandFilter("oppo");
    }

    /**
     * Phương thức factory tạo bộ lọc Xiaomi
     */
    public static BrandFilter createXiaomiFilter() {
        return new BrandFilter("xiaomi", "redmi", "poco");
    }

    /**
     * Phương thức factory tạo bộ lọc Vivo
     */
    public static BrandFilter createVivoFilter() {
        return new BrandFilter("vivo");
    }
}