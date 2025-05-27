package com.phonerecommend.service.filter;

import com.phonerecommend.model.Phone;
import java.util.List;

/**
 * Interface định nghĩa các phương thức lọc điện thoại
 */
public interface PhoneFilter {
    /**
     * Lọc danh sách điện thoại theo điều kiện
     * @param phones Danh sách điện thoại đầu vào
     * @return Danh sách điện thoại sau khi lọc
     */
    List<Phone> filter(List<Phone> phones);

    /**
     * Mô tả tiêu chí lọc
     * @return Mô tả dạng chuỗi
     */
    String getDescription();

    /**
     * Lấy ID của bộ lọc, dùng để nhận diện loại bộ lọc
     * @return ID dạng chuỗi
     */
    String getFilterId();
}