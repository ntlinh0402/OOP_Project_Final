package com.phonerecommend.service.filter.impl;

import com.phonerecommend.model.Phone;
import com.phonerecommend.service.filter.AbstractPhoneFilter;

/**
 * Lọc điện thoại phù hợp để livestream
 */
public class LivestreamFilter extends AbstractPhoneFilter {
    private static final int MIN_REAR_CAMERA_RESOLUTION = 12; // MP
    private static final int MIN_FRONT_CAMERA_RESOLUTION = 10; // MP
    private static final int MIN_BATTERY_CAPACITY = 4000; // mAh
    private static final int MIN_RAM = 8; // GB

    public LivestreamFilter() {
        super("livestream", "Điện thoại phù hợp livestream");
    }

    @Override
    protected boolean isPhoneMatched(Phone phone) {
        // Cần đảm bảo có camera tốt, pin trâu và RAM đủ để livestream
        return hasHighQualityCamera(phone) &&
                hasLongBatteryForLivestream(phone) &&
                hasSufficientRamForLivestream(phone) &&
                has4KVideoCapability(phone);
    }

    private boolean hasHighQualityCamera(Phone phone) {
        // Kiểm tra camera sau
        String rearCameraInfo = phone.getDescription().getRearCamera();
        if (rearCameraInfo != null && !rearCameraInfo.isEmpty()) {
            try {
                // Trích xuất độ phân giải từ chuỗi (vd: "48MP")
                String[] parts = rearCameraInfo.split("MP");
                for (String part : parts) {
                    String numStr = part.replaceAll("[^0-9]", "").trim();
                    if (!numStr.isEmpty()) {
                        int resolution = Integer.parseInt(numStr);
                        if (resolution >= MIN_REAR_CAMERA_RESOLUTION) {
                            return true;
                        }
                    }
                }
            } catch (Exception e) {
                // Xử lý lỗi khi parse
            }
        }

        // Kiểm tra camera trước
        String frontCameraInfo = phone.getDescription().getFrontCamera();
        if (frontCameraInfo != null && !frontCameraInfo.isEmpty()) {
            try {
                String numericPart = frontCameraInfo.replaceAll("[^0-9]", "");
                int resolution = Integer.parseInt(numericPart);
                return resolution >= MIN_FRONT_CAMERA_RESOLUTION;
            } catch (NumberFormatException e) {
                // Xử lý lỗi khi parse
            }
        }

        return false;
    }

    private boolean hasLongBatteryForLivestream(Phone phone) {
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

    private boolean hasSufficientRamForLivestream(Phone phone) {
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

    private boolean has4KVideoCapability(Phone phone) {
        String videoInfo = phone.getDescription().getAttribute("Quay video");
        return videoInfo != null && videoInfo.contains("4K");
    }
}