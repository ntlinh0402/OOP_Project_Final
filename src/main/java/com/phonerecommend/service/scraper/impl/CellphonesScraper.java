package com.phonerecommend.service.scraper.impl;

import com.phonerecommend.model.Phone;
import com.phonerecommend.model.PhoneDescription;
import com.phonerecommend.service.scraper.WebScraperService;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Triển khai scraper cho trang web Cellphones.com.vn
 */
public class CellphonesScraper implements WebScraperService {

    private final HttpClient httpClient;

    public CellphonesScraper() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    @Override
    public Phone scrapePhoneDetails(String url) {
        try {
            String htmlContent = fetchPageContent(url);
            if (htmlContent == null) {
                return null;
            }

            Document doc = Jsoup.parse(htmlContent);

            // Khởi tạo đối tượng Phone
            Phone phone = new Phone();
            phone.setLink(url);

            // Lấy tên sản phẩm
            Element titleElement = doc.selectFirst("h1.product-title");
            if (titleElement != null) {
                phone.setName(titleElement.text().trim());
            }

            // Lấy giá sản phẩm
            double price = scrapeProductPrice(doc);
            phone.setPrice(price);

            // Lấy URL hình ảnh
            String imageUrl = scrapeProductImage(doc);
            if (imageUrl != null) {
                phone.setImageUrl(imageUrl);
            }

            // Lấy thông tin chi tiết sản phẩm
            PhoneDescription description = scrapeProductDescription(doc);
            phone.setDescription(description);

            return phone;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String scrapeProductImage(String url) {
        try {
            String htmlContent = fetchPageContent(url);
            if (htmlContent == null) {
                return null;
            }

            Document doc = Jsoup.parse(htmlContent);
            return scrapeProductImage(doc);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String scrapeProductImage(Document doc) {
        // Tìm phần tử hình ảnh chính
        Element imgElement = doc.selectFirst("div.featured-image img");
        if (imgElement != null) {
            return imgElement.attr("src");
        }

        // Thử tìm phần tử hình ảnh khác
        imgElement = doc.selectFirst("div.product-image img");
        if (imgElement != null) {
            return imgElement.attr("src");
        }

        return null;
    }

    @Override
    public double scrapeProductPrice(String url) {
        try {
            String htmlContent = fetchPageContent(url);
            if (htmlContent == null) {
                return -1;
            }

            Document doc = Jsoup.parse(htmlContent);
            return scrapeProductPrice(doc);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    private double scrapeProductPrice(Document doc) {
        // Tìm phần tử giá sản phẩm
        Element priceElement = doc.selectFirst("div.product-price span.price");
        if (priceElement != null) {
            String priceText = priceElement.text().replaceAll("[^0-9]", "");
            try {
                return Double.parseDouble(priceText);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }

        return -1;
    }

    private PhoneDescription scrapeProductDescription(Document doc) {
        PhoneDescription description = new PhoneDescription();

        // Lấy thông tin từ bảng thông số kỹ thuật
        Element specTable = doc.selectFirst("div.product-specifications table");
        if (specTable != null) {
            Elements rows = specTable.select("tr");
            for (Element row : rows) {
                Element nameCell = row.selectFirst("td.name");
                Element valueCell = row.selectFirst("td.value");

                if (nameCell != null && valueCell != null) {
                    String name = nameCell.text().trim();
                    String value = valueCell.text().trim();

                    description.setAttribute(name, value);
                }
            }
        }

        return description;
    }

    @Override
    public List<Phone> scrapePhoneList(String url, int maxItems) {
        List<Phone> phones = new ArrayList<>();

        try {
            String htmlContent = fetchPageContent(url);
            if (htmlContent == null) {
                return phones;
            }

            Document doc = Jsoup.parse(htmlContent);
            Elements productItems = doc.select("div.product-item");

            int count = 0;
            for (Element item : productItems) {
                if (maxItems > 0 && count >= maxItems) {
                    break;
                }

                Element linkElement = item.selectFirst("a.product-name");
                if (linkElement != null) {
                    String productUrl = linkElement.attr("href");
                    if (!productUrl.startsWith("http")) {
                        productUrl = "https://cellphones.com.vn" + productUrl;
                    }

                    // Tạo đối tượng Phone với thông tin cơ bản
                    Phone phone = new Phone();
                    phone.setLink(productUrl);

                    // Lấy tên sản phẩm
                    phone.setName(linkElement.text().trim());

                    // Lấy giá sản phẩm
                    Element priceElement = item.selectFirst("span.price");
                    if (priceElement != null) {
                        String priceText = priceElement.text().replaceAll("[^0-9]", "");
                        try {
                            phone.setPrice(Double.parseDouble(priceText));
                        } catch (NumberFormatException e) {
                            phone.setPrice(0);
                        }
                    }

                    // Lấy URL hình ảnh
                    Element imgElement = item.selectFirst("img.product-image");
                    if (imgElement != null) {
                        phone.setImageUrl(imgElement.attr("src"));
                    }

                    phones.add(phone);
                    count++;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return phones;
    }

    /**
     * Lấy nội dung HTML của trang web
     * @param url URL trang web
     * @return Chuỗi HTML, hoặc null nếu có lỗi
     */
    private String fetchPageContent(String url) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(10))
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return response.body();
            } else {
                System.err.println("Lỗi khi tải trang: " + response.statusCode());
                return null;
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }
}