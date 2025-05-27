package com.phonerecommend.service.search;

import com.phonerecommend.model.Phone;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Dịch vụ sắp xếp kết quả tìm kiếm điện thoại
 */
public class SortingService {

    /**
     * Enum định nghĩa các tiêu chí sắp xếp
     */
    public enum SortCriteria {
        PRICE_LOW_TO_HIGH("Giá thấp đến cao"),
        PRICE_HIGH_TO_LOW("Giá cao đến thấp"),
        MOST_VIEWED("Xem nhiều"),
        NEWEST("Mới nhất");

        private final String description;

        SortCriteria(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * Sắp xếp danh sách điện thoại theo tiêu chí
     * @param phones Danh sách điện thoại cần sắp xếp
     * @param criteria Tiêu chí sắp xếp
     * @return Danh sách điện thoại đã sắp xếp
     */
    public List<Phone> sort(List<Phone> phones, SortCriteria criteria) {
        switch (criteria) {
            case PRICE_LOW_TO_HIGH:
                return sortByPriceLowToHigh(phones);
            case PRICE_HIGH_TO_LOW:
                return sortByPriceHighToLow(phones);
            case MOST_VIEWED:
                return sortByMostViewed(phones);
            case NEWEST:
                return sortByNewest(phones);
            default:
                return phones;
        }
    }

    /**
     * Sắp xếp theo giá tăng dần
     */
    private List<Phone> sortByPriceLowToHigh(List<Phone> phones) {
        return phones.stream()
                .sorted(Comparator.comparingDouble(Phone::getPrice))
                .collect(Collectors.toList());
    }

    /**
     * Sắp xếp theo giá giảm dần
     */
    private List<Phone> sortByPriceHighToLow(List<Phone> phones) {
        return phones.stream()
                .sorted(Comparator.comparingDouble(Phone::getPrice).reversed())
                .collect(Collectors.toList());
    }

    /**
     * Sắp xếp theo lượt xem nhiều nhất
     */
    private List<Phone> sortByMostViewed(List<Phone> phones) {
        return phones.stream()
                .sorted(Comparator.comparingInt(Phone::getViewCount).reversed())
                .collect(Collectors.toList());
    }

    /**
     * Sắp xếp theo thời gian ra mắt mới nhất
     * Lưu ý: Đơn giản hóa bằng cách sử dụng thông tin "Thời điểm ra mắt" từ mô tả
     */
    private List<Phone> sortByNewest(List<Phone> phones) {
        return phones.stream()
                .sorted((p1, p2) -> {
                    String date1 = p1.getDescription().getAttribute("Thời điểm ra mắt");
                    String date2 = p2.getDescription().getAttribute("Thời điểm ra mắt");

                    // Nếu không có thông tin ngày, đặt ở cuối
                    if (date1 == null || date1.isEmpty()) return 1;
                    if (date2 == null || date2.isEmpty()) return -1;

                    // Giả định định dạng MM-yyyy hoặc MM/yyyy
                    // Trong thực tế cần xử lý chuyển đổi chuỗi ngày tháng phức tạp hơn
                    return date2.compareTo(date1); // Sắp xếp giảm dần (mới nhất lên đầu)
                })
                .collect(Collectors.toList());
    }
}