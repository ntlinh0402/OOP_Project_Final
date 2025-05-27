package com.phonerecommend.service.filter.impl;

import com.phonerecommend.model.Phone;
import com.phonerecommend.service.filter.AbstractPhoneFilter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Collections;
import com.phonerecommend.service.filter.PhoneFilter;

/**
 * Bộ lọc tổng hợp kết hợp nhiều bộ lọc khác
 */
public class CompositeFilter implements PhoneFilter {
    private final List<PhoneFilter> filters;
    private final boolean requireAllFilters; // True: AND, False: OR
    private final String filterId;
    private final String description;

    /**
     * Constructor với chế độ kết hợp
     * @param requireAllFilters True nếu cần thỏa mãn tất cả bộ lọc (AND), False nếu chỉ cần thỏa mãn một bộ lọc (OR)
     */
    public CompositeFilter(boolean requireAllFilters) {
        this.filters = new ArrayList<>();
        this.requireAllFilters = requireAllFilters;
        this.filterId = "composite_" + (requireAllFilters ? "and" : "or");
        this.description = requireAllFilters ? "Thỏa mãn tất cả điều kiện" : "Thỏa mãn ít nhất một điều kiện";
    }

    /**
     * Thêm một bộ lọc vào danh sách
     * @param filter Bộ lọc cần thêm
     * @return Chính đối tượng này (để sử dụng method chaining)
     */
    public CompositeFilter addFilter(PhoneFilter filter) {
        filters.add(filter);
        return this;
    }

    /**
     * Xóa một bộ lọc khỏi danh sách
     * @param filter Bộ lọc cần xóa
     * @return Chính đối tượng này (để sử dụng method chaining)
     */
    public CompositeFilter removeFilter(PhoneFilter filter) {
        filters.remove(filter);
        return this;
    }

    /**
     * Xóa tất cả bộ lọc
     * @return Chính đối tượng này (để sử dụng method chaining)
     */
    public CompositeFilter clearFilters() {
        filters.clear();
        return this;
    }

    /**
     * Lấy danh sách các bộ lọc hiện tại
     * @return Danh sách bộ lọc
     */
    public List<PhoneFilter> getFilters() {
        return new ArrayList<>(filters);
    }

    @Override
    public List<Phone> filter(List<Phone> phones) {
        if (filters.isEmpty()) {
            return phones; // Nếu không có bộ lọc nào, trả về danh sách gốc
        }

        if (requireAllFilters) {
            // Thực hiện lọc AND (thỏa mãn tất cả điều kiện)
            List<Phone> result = new ArrayList<>(phones);

            for (PhoneFilter filter : filters) {
                result = filter.filter(result);
            }

            return result;
        } else {
            // Thực hiện lọc OR (thỏa mãn ít nhất một điều kiện)
            return phones.stream()
                    .filter(phone -> filters.stream().anyMatch(filter -> !filter.filter(Collections.singletonList(phone)).isEmpty()))
                    .distinct()
                    .collect(Collectors.toList());
        }
    }

    @Override
    public String getDescription() {
        if (filters.isEmpty()) {
            return description + " (chưa có điều kiện)";
        }

        List<String> filterDescriptions = filters.stream()
                .map(PhoneFilter::getDescription)
                .collect(Collectors.toList());

        String combinationType = requireAllFilters ? "thỏa mãn tất cả" : "thỏa mãn ít nhất một";
        return "Bộ lọc tổng hợp (" + combinationType + "): " + String.join(", ", filterDescriptions);
    }

    @Override
    public String getFilterId() {
        return filterId;
    }
}