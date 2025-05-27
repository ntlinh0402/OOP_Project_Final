package com.phonerecommend.repository;

import com.phonerecommend.model.Phone;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Abstract class cơ sở cho các repository
 * Triển khai các phương thức chung
 */
public abstract class AbstractPhoneRepository implements PhoneRepository {

    @Override
    public boolean updateViewCount(Phone phone) {
        if (phone == null) {
            return false;
        }

        // Lấy điện thoại hiện tại từ kho dữ liệu
        Optional<Phone> existingPhone = findPhoneByLink(phone.getLink());

        if (existingPhone.isPresent()) {
            // Tăng lượt xem và lưu lại
            Phone updatedPhone = existingPhone.get();
            updatedPhone.incrementViewCount();
            return savePhone(updatedPhone);
        }

        return false;
    }

    /**
     * Phương thức hỗ trợ để tìm điện thoại theo tiêu chí
     * @param phones Danh sách điện thoại
     * @param criteria Tiêu chí tìm kiếm
     * @return Danh sách điện thoại thỏa mãn
     */
    protected List<Phone> findPhonesByCriteria(List<Phone> phones, PhoneSearchCriteria criteria) {
        return phones.stream()
                .filter(criteria::matches)
                .collect(Collectors.toList());
    }

    /**
     * Interface tiêu chí tìm kiếm điện thoại
     */
    protected interface PhoneSearchCriteria {
        boolean matches(Phone phone);
    }
}