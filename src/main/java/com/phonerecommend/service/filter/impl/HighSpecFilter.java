package com.phonerecommend.service.filter.impl;

import com.phonerecommend.model.Phone;
import com.phonerecommend.service.filter.AbstractPhoneFilter;

import java.util.Arrays;
import java.util.List;

/**
 * Lọc điện thoại theo tiêu chí cấu hình cao
 */
public class HighSpecFilter extends AbstractPhoneFilter {
    private static final List<String> HIGH_END_CHIPSETS = Arrays.asList(
            "snapdragon 8", "snapdragon 888", "snapdragon 865",
            "a14 bionic", "a15 bionic", "a16 bionic", "a17 pro", "a18 pro",
            "dimensity 9000", "helio g99", "exynos 2200", "exynos 2400"
    );

    private static final int MIN_RAM = 8; // GB
    private static final int MIN_STORAGE = 128; // GB

    public HighSpecFilter() {
        super("high_spec", "Điện thoại cấu hình cao");
    }

    @Override
    protected boolean isPhoneMatched(Phone phone) {
        boolean hasHighEndChip = checkHighEndChip(phone);
        boolean hasSufficientRam = checkSufficientRam(phone);
        boolean hasSufficientStorage = checkSufficientStorage(phone);
        boolean has5G = check5G(phone);

        // Để là cấu hình cao, cần ít nhất chip cao cấp và RAM đủ
        return hasHighEndChip && hasSufficientRam;
    }

    private boolean checkHighEndChip(Phone phone) {
        String chipset = phone.getDescription().getChipset();
        if (chipset == null || chipset.isEmpty()) {
            return false;
        }

        chipset = chipset.toLowerCase();
        for (String chip : HIGH_END_CHIPSETS) {
            if (chipset.contains(chip.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    private boolean checkSufficientRam(Phone phone) {
        String ramInfo = phone.getDescription().getAttribute("Dung lượng RAM");
        if (ramInfo == null || ramInfo.isEmpty()) {
            return false;
        }

        try {
            // Trích xuất dung lượng RAM từ chuỗi (vd: "8 GB")
            String numericPart = ramInfo.replaceAll("[^0-9]", "");
            int ram = Integer.parseInt(numericPart);
            return ram >= MIN_RAM;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean checkSufficientStorage(Phone phone) {
        String storageInfo = phone.getDescription().getStorage();
        if (storageInfo == null || storageInfo.isEmpty()) {
            return false;
        }

        try {
            // Trích xuất dung lượng bộ nhớ từ chuỗi (vd: "128 GB")
            String numericPart = storageInfo.replaceAll("[^0-9]", "");
            int storage = Integer.parseInt(numericPart);
            return storage >= MIN_STORAGE;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean check5G(Phone phone) {
        String networkInfo = phone.getDescription().getNetworkSupport();
        return networkInfo != null && networkInfo.contains("5G");
    }
}