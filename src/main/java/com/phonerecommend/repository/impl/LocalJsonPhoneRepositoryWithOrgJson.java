package com.phonerecommend.repository.impl;

import com.phonerecommend.repository.AbstractPhoneRepository;
import com.phonerecommend.model.Phone;
import com.phonerecommend.model.PhoneDescription;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

// Import cho JSON processing
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.Iterator;

/**
 * Repository sử dụng org.json thay vì SimpleJSON
 */
public class LocalJsonPhoneRepositoryWithOrgJson extends AbstractPhoneRepository {
    private final String filePath;
    private List<Phone> phones;

    public LocalJsonPhoneRepositoryWithOrgJson(String filePath) {
        this.filePath = filePath;
        this.phones = new ArrayList<>();
        loadData();
    }

    private void loadData() {
        File file = new File(filePath);
        if (!file.exists()) {
            System.err.println("File không tồn tại: " + filePath);
            return;
        }

        try {
            // Đọc toàn bộ file với UTF-8
            String content = Files.readString(file.toPath(), StandardCharsets.UTF_8);
            System.out.println("DEBUG: File content length: " + content.length());

            // Parse JSON với org.json
            JSONArray jsonArray = new JSONArray(content);
            System.out.println("DEBUG: Parsed " + jsonArray.length() + " phones");

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject phoneJson = jsonArray.getJSONObject(i);

                Phone phone = new Phone();
                phone.setName(phoneJson.optString("name", ""));
                phone.setLink(phoneJson.optString("link", ""));
                phone.setPrice(phoneJson.optDouble("price", 0));
                phone.setViewCount(phoneJson.optInt("viewCount", 0));

                if (phoneJson.has("imgURL")) {
                    phone.setImageUrl(phoneJson.getString("imgURL"));
                }

                // Xử lý description
                PhoneDescription description = new PhoneDescription();
                if (phoneJson.has("description")) {
                    JSONObject descJson = phoneJson.getJSONObject("description");

                    System.out.println("DEBUG: Processing description for " + phone.getName());
                    System.out.println("DEBUG: Description has " + descJson.length() + " keys");

                    // Lấy tất cả keys từ description
                    Iterator<String> keys = descJson.keys();
                    while (keys.hasNext()) {
                        String key = keys.next();
                        String value = descJson.optString(key, "");
                        description.setAttribute(key, value);

                        // Debug một vài key quan trọng
                        if (key.equals("Camera sau") || key.equals("Chipset")) {
                            System.out.println("DEBUG: Set '" + key + "' = '" +
                                    (value.length() > 50 ? value.substring(0, 50) + "..." : value) + "'");
                        }
                    }

                    System.out.println("DEBUG: Description now has " +
                            description.getAllAttributes().size() + " attributes");
                }

                phone.setDescription(description);
                phones.add(phone);

                // Debug cho phone đầu tiên
                if (i == 0) {
                    System.out.println("DEBUG: First phone test:");
                    System.out.println("- Camera sau: '" + description.getAttribute("Camera sau") + "'");
                    System.out.println("- Camera trước: '" + description.getAttribute("Camera trước") + "'");
                    System.out.println("- Chipset: '" + description.getAttribute("Chipset") + "'");
                }
            }

            System.out.println("Successfully loaded " + phones.size() + " phones");

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Lỗi đọc file: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Lỗi parse JSON: " + e.getMessage());
        }
    }

    @Override
    public List<Phone> getAllPhones() {
        return new ArrayList<>(phones);
    }

    @Override
    public Optional<Phone> findPhoneByName(String name) {
        return phones.stream()
                .filter(p -> p.getName().equalsIgnoreCase(name))
                .findFirst();
    }

    @Override
    public Optional<Phone> findPhoneByLink(String link) {
        return phones.stream()
                .filter(p -> p.getLink().equals(link))
                .findFirst();
    }

    @Override
    public List<Phone> searchPhones(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllPhones();
        }

        String lowercaseKeyword = keyword.toLowerCase();
        return phones.stream()
                .filter(p -> p.getName().toLowerCase().contains(lowercaseKeyword))
                .collect(Collectors.toList());
    }

    @Override
    public boolean savePhone(Phone phone) {
        // Implementation for saving
        return false; // Simplified for now
    }

    @Override
    public boolean deletePhone(Phone phone) {
        // Implementation for deleting
        return false; // Simplified for now
    }
}