package com.phonerecommend.controller;

import com.phonerecommend.model.Phone;
import com.phonerecommend.repository.PhoneRepository;
import com.phonerecommend.repository.RepositoryFactory;
import com.phonerecommend.service.scraper.WebScraperService;
import com.phonerecommend.service.scraper.impl.CellphonesScraper;

/**
 * Controller xử lý thông tin chi tiết điện thoại
 */
public class PhoneDetailController {
    private final PhoneRepository phoneRepository;
    private final WebScraperService webScraperService;

    /**
     * Constructor mặc định
     */
    public PhoneDetailController() {
        this.phoneRepository = RepositoryFactory.getPhoneRepository();
        this.webScraperService = new CellphonesScraper();
    }

    /**
     * Lấy thông tin chi tiết điện thoại từ link
     * @param link Link điện thoại
     * @return Đối tượng Phone chứa thông tin chi tiết
     */
    public Phone getPhoneDetails(String link) {
        // Kiểm tra xem điện thoại đã có trong repository chưa
        Phone phone = phoneRepository.findPhoneByLink(link).orElse(null);

        if (phone != null) {
            // Nếu đã có, cập nhật lượt xem
            phone.incrementViewCount();
            phoneRepository.updateViewCount(phone);
            return phone;
        }

        // Nếu chưa có, dùng web scraper để lấy thông tin
        phone = webScraperService.scrapePhoneDetails(link);

        if (phone != null) {
            // Lưu vào repository
            phoneRepository.savePhone(phone);
        }

        return phone;
    }

    /**
     * Cập nhật thông tin chi tiết điện thoại từ web
     * @param link Link điện thoại
     * @return Điện thoại đã cập nhật, hoặc null nếu có lỗi
     */
    public Phone refreshPhoneDetails(String link) {
        // Lấy thông tin mới từ web
        Phone updatedPhone = webScraperService.scrapePhoneDetails(link);

        if (updatedPhone != null) {
            // Kiểm tra điện thoại cũ
            Phone existingPhone = phoneRepository.findPhoneByLink(link).orElse(null);

            // Giữ lại số lượt xem
            if (existingPhone != null) {
                updatedPhone.setViewCount(existingPhone.getViewCount());
            }

            // Lưu vào repository
            phoneRepository.savePhone(updatedPhone);
        }

        return updatedPhone;
    }

    /**
     * Lấy URL hình ảnh của điện thoại
     * @param link Link điện thoại
     * @return URL hình ảnh
     */
    public String getPhoneImage(String link) {
        // Kiểm tra xem có điện thoại trong repository không
        Phone phone = phoneRepository.findPhoneByLink(link).orElse(null);

        if (phone != null && phone.getImageUrl() != null && !phone.getImageUrl().isEmpty()) {
            return phone.getImageUrl();
        }

        // Nếu không có, dùng web scraper để lấy
        return webScraperService.scrapeProductImage(link);
    }

    /**
     * Lấy giá mới nhất của điện thoại
     * @param link Link điện thoại
     * @return Giá mới nhất
     */
    public double getPhonePrice(String link) {
        // Kiểm tra xem có điện thoại trong repository không
        Phone phone = phoneRepository.findPhoneByLink(link).orElse(null);

        if (phone != null) {
            return phone.getPrice();
        }

        // Nếu không có, dùng web scraper để lấy
        return webScraperService.scrapeProductPrice(link);
    }

    /**
     * Lấy điện thoại từ link
     * @param link Link điện thoại
     * @return Đối tượng Phone, hoặc null nếu không tìm thấy
     */
    public Phone getPhoneByLink(String link) {
        return phoneRepository.findPhoneByLink(link).orElse(null);
    }
}