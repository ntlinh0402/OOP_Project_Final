package com.phonerecommend.service.scraper;

import com.phonerecommend.model.Phone;

/**
 * Interface định nghĩa dịch vụ lấy thông tin sản phẩm từ website
 */
public interface WebScraperService {

    /**
     * Lấy thông tin chi tiết điện thoại từ URL
     * @param url URL trang chi tiết sản phẩm
     * @return Đối tượng Phone chứa thông tin chi tiết, hoặc null nếu có lỗi
     */
    Phone scrapePhoneDetails(String url);

    /**
     * Lấy hình ảnh sản phẩm từ URL
     * @param url URL trang chi tiết sản phẩm
     * @return URL của hình ảnh sản phẩm, hoặc null nếu không tìm thấy
     */
    String scrapeProductImage(String url);

    /**
     * Lấy thông tin giá sản phẩm từ URL
     * @param url URL trang chi tiết sản phẩm
     * @return Giá sản phẩm, hoặc -1 nếu không tìm thấy
     */
    double scrapeProductPrice(String url);

    /**
     * Lấy danh sách điện thoại từ trang danh sách sản phẩm
     * @param url URL trang danh sách sản phẩm
     * @param maxItems Số lượng sản phẩm tối đa cần lấy (nếu <= 0 thì lấy tất cả)
     * @return Danh sách các đối tượng Phone, hoặc danh sách rỗng nếu có lỗi
     */
    java.util.List<Phone> scrapePhoneList(String url, int maxItems);
}