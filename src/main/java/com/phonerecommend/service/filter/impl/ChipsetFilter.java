package com.phonerecommend.service.filter.impl;

import com.phonerecommend.model.Phone;
import com.phonerecommend.service.filter.AbstractPhoneFilter;

import java.util.Arrays;
import java.util.List;

/**
 * Bộ lọc điện thoại theo loại chipset
 */
public class ChipsetFilter extends AbstractPhoneFilter {

    public enum ChipsetType {
        SNAPDRAGON("Snapdragon", "snapdragon"),
        APPLE_A("Apple A Series", "a14", "a15", "a16", "a17", "a18", "apple"),
        EXYNOS("Exynos", "exynos"),
        MEDIATEK_HELIO("MediaTek Helio", "helio"),
        MEDIATEK_DIMENSITY("MediaTek Dimensity", "dimensity");

        private final String displayName;
        private final List<String> keywords;

        ChipsetType(String displayName, String... keywords) {
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

    private final List<ChipsetType> chipsetTypes;

    /**
     * Constructor với danh sách loại chipset
     * @param chipsets Các loại chipset cần lọc
     */
    public ChipsetFilter(ChipsetType... chipsets) {
        super("chipset", buildDescription(chipsets));
        this.chipsetTypes = Arrays.asList(chipsets);
    }

    private static String buildDescription(ChipsetType[] chipsets) {
        if (chipsets.length == 0) {
            return "Lọc theo chipset";
        }

        StringBuilder sb = new StringBuilder("Chipset: ");
        for (int i = 0; i < chipsets.length; i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(chipsets[i].getDisplayName());
        }
        return sb.toString();
    }

    @Override
    protected boolean isPhoneMatched(Phone phone) {
        String chipsetInfo = phone.getDescription().getChipset();
        if (chipsetInfo == null || chipsetInfo.isEmpty()) {
            return false;
        }

        chipsetInfo = chipsetInfo.toLowerCase();

        // Kiểm tra xem chipset có khớp với bất kỳ loại nào đã chọn không
        for (ChipsetType chipsetType : chipsetTypes) {
            for (String keyword : chipsetType.getKeywords()) {
                if (chipsetInfo.contains(keyword.toLowerCase())) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Tạo bộ lọc Snapdragon
     */
    public static ChipsetFilter createSnapdragonFilter() {
        return new ChipsetFilter(ChipsetType.SNAPDRAGON);
    }

    /**
     * Tạo bộ lọc Apple A Series
     */
    public static ChipsetFilter createAppleFilter() {
        return new ChipsetFilter(ChipsetType.APPLE_A);
    }

    /**
     * Tạo bộ lọc Exynos
     */
    public static ChipsetFilter createExynosFilter() {
        return new ChipsetFilter(ChipsetType.EXYNOS);
    }

    /**
     * Tạo bộ lọc MediaTek Helio
     */
    public static ChipsetFilter createMediaTekHelioFilter() {
        return new ChipsetFilter(ChipsetType.MEDIATEK_HELIO);
    }

    /**
     * Tạo bộ lọc MediaTek Dimensity
     */
    public static ChipsetFilter createMediaTekDimensityFilter() {
        return new ChipsetFilter(ChipsetType.MEDIATEK_DIMENSITY);
    }
}