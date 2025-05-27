package com.phonerecommend.repository;

import com.phonerecommend.model.Phone;
import java.util.List;
import java.util.Optional;

/**
 * Interface định nghĩa các phương thức để truy cập dữ liệu điện thoại
 */
public interface PhoneRepository {
    /**
     * Lấy danh sách tất cả điện thoại
     * @return Danh sách điện thoại
     */
    List<Phone> getAllPhones();

    /**
     * Tìm điện thoại theo tên
     * @param name Tên điện thoại cần tìm
     * @return Điện thoại tương ứng (nếu có)
     */
    Optional<Phone> findPhoneByName(String name);

    /**
     * Tìm điện thoại theo ID/link
     * @param link Link của điện thoại
     * @return Điện thoại tương ứng (nếu có)
     */
    Optional<Phone> findPhoneByLink(String link);

    /**
     * Tìm kiếm điện thoại với từ khóa
     * @param keyword Từ khóa tìm kiếm
     * @return Danh sách điện thoại phù hợp
     */
    List<Phone> searchPhones(String keyword);

    /**
     * Lưu thông tin điện thoại mới hoặc cập nhật thông tin hiện có
     * @param phone Điện thoại cần lưu
     * @return true nếu thành công, false nếu thất bại
     */
    boolean savePhone(Phone phone);

    /**
     * Xóa thông tin điện thoại
     * @param phone Điện thoại cần xóa
     * @return true nếu thành công, false nếu thất bại
     */
    boolean deletePhone(Phone phone);

    /**
     * Cập nhật số lượt xem của điện thoại
     * @param phone Điện thoại cần cập nhật
     * @return true nếu thành công, false nếu thất bại
     */
    boolean updateViewCount(Phone phone);
}