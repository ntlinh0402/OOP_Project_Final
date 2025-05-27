package com.phonerecommend.model;

/**
 * Lớp đại diện cho một điện thoại
 */
public class Phone {
    private String name;
    private String link;
    private double price;
    private PhoneDescription description;
    private String imageUrl;
    private int viewCount; // Số lượt xem

    public Phone() {
        this.description = new PhoneDescription();
        this.viewCount = 0;
    }

    public Phone(String name, String link, double price, PhoneDescription description) {
        this.name = name;
        this.link = link;
        this.price = price;
        this.description = description;
        this.viewCount = 0;

        // Mặc định sử dụng link + /image.jpg làm URL hình ảnh nếu chưa có
        this.imageUrl = null;
    }

    /**
     * Tạo URL hình ảnh dựa trên link sản phẩm
     * Phương pháp này chỉ được sử dụng khi không có sẵn URL hình ảnh từ JSON
     * @param link Link sản phẩm
     * @return URL hình ảnh
     */
    private String generateImageUrlFromLink(String link) {
        if (link == null || link.isEmpty()) {
            return null;
        }

        // Phân tích URL để lấy tên sản phẩm
        try {
            // Thử tạo URL từ mẫu của trang Cellphones
            // Ví dụ: https://cellphones.com.vn/iphone-16-pro-max.html -> iphone-16-pro-max
            String productName = link.replace("https://cellphones.com.vn/", "")
                    .replace("http://cellphones.com.vn/", "")
                    .replace(".html", "");

            // Tạo URL hình theo định dạng CDN của trang
            return "https://cdn2.cellphones.com.vn/x358,webp,q100/media/catalog/product/" + productName + ".png";
        } catch (Exception e) {
            System.err.println("Lỗi khi tạo URL hình ảnh từ link: " + e.getMessage());
            return null;
        }
    }

    // Getters và Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;

        // Không cập nhật URL hình ảnh tự động từ link nữa
        // chỉ khi imageUrl chưa được thiết lập
        if (this.imageUrl == null || this.imageUrl.isEmpty()) {
            this.imageUrl = generateImageUrlFromLink(link);
        }
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public PhoneDescription getDescription() {
        return description;
    }

    public void setDescription(PhoneDescription description) {
        this.description = description;
    }

    public String getImageUrl() {
        // Nếu imageUrl chưa được thiết lập, tạo một URL dựa trên link
        if (imageUrl == null || imageUrl.isEmpty()) {
            return generateImageUrlFromLink(link);
        }
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getViewCount() {
        return viewCount;
    }

    public void setViewCount(int viewCount) {
        this.viewCount = viewCount;
    }

    public void incrementViewCount() {
        this.viewCount++;
    }

    @Override
    public String toString() {
        return name + " - " + String.format("%,.0f", price) + " VNĐ";
    }
}