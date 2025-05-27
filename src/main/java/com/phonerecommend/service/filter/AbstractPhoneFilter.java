package com.phonerecommend.service.filter;

import com.phonerecommend.model.Phone;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Lớp abstract cơ sở cho các bộ lọc điện thoại
 */
public abstract class AbstractPhoneFilter implements PhoneFilter {
    private final String filterId;
    private final String description;

    /**
     * Constructor với ID và mô tả
     * @param filterId ID bộ lọc
     * @param description Mô tả bộ lọc
     */
    protected AbstractPhoneFilter(String filterId, String description) {
        this.filterId = filterId;
        this.description = description;
    }

    @Override
    public List<Phone> filter(List<Phone> phones) {
        return phones.stream()
                .filter(this::isPhoneMatched)
                .collect(Collectors.toList());
    }

    /**
     * Kiểm tra điện thoại có thỏa mãn tiêu chí lọc không
     * @param phone Điện thoại cần kiểm tra
     * @return true nếu thỏa mãn, false nếu không
     */
    protected abstract boolean isPhoneMatched(Phone phone);

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String getFilterId() {
        return filterId;
    }
}