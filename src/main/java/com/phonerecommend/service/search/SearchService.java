package com.phonerecommend.service.search;

import com.phonerecommend.model.Phone;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Dịch vụ tìm kiếm điện thoại
 */
public class SearchService {

    /**
     * Tìm kiếm điện thoại theo từ khóa
     * @param phones Danh sách điện thoại để tìm kiếm
     * @param keyword Từ khóa tìm kiếm
     * @return Danh sách điện thoại thỏa mãn từ khóa
     */
    public List<Phone> searchByKeyword(List<Phone> phones, String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return phones;
        }

        String lowercaseKeyword = keyword.toLowerCase();

        return phones.stream()
                .filter(phone -> {
                    // Tìm kiếm trong tên điện thoại
                    if (phone.getName().toLowerCase().contains(lowercaseKeyword)) {
                        return true;
                    }

                    // Tìm kiếm trong các thuộc tính của điện thoại
                    for (String value : phone.getDescription().getAllAttributes().values()) {
                        if (value.toLowerCase().contains(lowercaseKeyword)) {
                            return true;
                        }
                    }

                    return false;
                })
                .collect(Collectors.toList());
    }

    /**
     * Tìm kiếm điện thoại theo khoảng giá
     * @param phones Danh sách điện thoại để tìm kiếm
     * @param minPrice Giá tối thiểu (nếu < 0 thì không giới hạn)
     * @param maxPrice Giá tối đa (nếu < 0 thì không giới hạn)
     * @return Danh sách điện thoại thỏa mãn khoảng giá
     */
    public List<Phone> searchByPriceRange(List<Phone> phones, double minPrice, double maxPrice) {
        Predicate<Phone> priceCriteria = phone -> {
            double price = phone.getPrice();
            boolean meetsMinPrice = minPrice < 0 || price >= minPrice;
            boolean meetsMaxPrice = maxPrice < 0 || price <= maxPrice;
            return meetsMinPrice && meetsMaxPrice;
        };

        return phones.stream()
                .filter(priceCriteria)
                .collect(Collectors.toList());
    }

    /**
     * Tìm kiếm điện thoại kết hợp từ khóa và khoảng giá
     * @param phones Danh sách điện thoại để tìm kiếm
     * @param keyword Từ khóa tìm kiếm
     * @param minPrice Giá tối thiểu (nếu < 0 thì không giới hạn)
     * @param maxPrice Giá tối đa (nếu < 0 thì không giới hạn)
     * @return Danh sách điện thoại thỏa mãn điều kiện
     */
    public List<Phone> searchPhones(List<Phone> phones, String keyword, double minPrice, double maxPrice) {
        List<Phone> keywordResults = searchByKeyword(phones, keyword);
        return searchByPriceRange(keywordResults, minPrice, maxPrice);
    }
}