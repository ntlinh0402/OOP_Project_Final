package com.phonerecommend.repository.impl;

import com.phonerecommend.repository.AbstractPhoneRepository;
import com.phonerecommend.model.Phone;
import com.phonerecommend.model.PhoneDescription;
import org.example.util.SimpleJSON;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.HashMap;

/**
 * Repository với UTF-8 encoding
 */
public class LocalJsonPhoneRepository extends AbstractPhoneRepository {
    private final String filePath;
    private List<Phone> phones;

    public LocalJsonPhoneRepository(String filePath) {
        this.filePath = filePath;
        this.phones = new ArrayList<>();
        loadData();
    }

    private void loadData() {
        File file = new File(filePath);
        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
                saveEmptyData();
            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("Không thể tạo file dữ liệu: " + e.getMessage());
            }
            return;
        }

        try {
            // Đọc file với UTF-8 encoding
            StringBuilder content = new StringBuilder();
            try (InputStreamReader reader = new InputStreamReader(
                    new FileInputStream(file), StandardCharsets.UTF_8)) {
                char[] buffer = new char[1024];
                int length;
                while ((length = reader.read(buffer)) != -1) {
                    content.append(buffer, 0, length);
                }
            }

            String jsonContent = content.toString().trim();
            if (jsonContent.isEmpty()) {
                return;
            }

            // Parse JSON
            List<Object> phonesArray = SimpleJSON.parseJSONArray(jsonContent);

            for (Object obj : phonesArray) {
                if (obj instanceof Map) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> phoneObj = (Map<String, Object>) obj;

                    Phone phone = new Phone();
                    phone.setName((String) phoneObj.get("name"));
                    phone.setLink((String) phoneObj.get("link"));

                    if (phoneObj.get("price") instanceof Number) {
                        phone.setPrice(((Number) phoneObj.get("price")).doubleValue());
                    }

                    if (phoneObj.get("viewCount") instanceof Number) {
                        phone.setViewCount(((Number) phoneObj.get("viewCount")).intValue());
                    }

                    if (phoneObj.get("imgURL") != null) {
                        phone.setImageUrl((String) phoneObj.get("imgURL"));
                    }

                    // Xử lý description
                    @SuppressWarnings("unchecked")
                    Map<String, Object> descObj = (Map<String, Object>) phoneObj.get("description");
                    PhoneDescription description = new PhoneDescription();

                    if (descObj != null) {
                        for (Map.Entry<String, Object> entry : descObj.entrySet()) {
                            String value = entry.getValue() != null ? entry.getValue().toString() : "";
                            description.setAttribute(entry.getKey(), value);
                        }
                    }

                    phone.setDescription(description);
                    phones.add(phone);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Lỗi khi đọc file dữ liệu: " + e.getMessage());
        }
    }

    private void saveEmptyData() throws IOException {
        try (OutputStreamWriter writer = new OutputStreamWriter(
                new FileOutputStream(filePath), StandardCharsets.UTF_8)) {
            writer.write("[]");
        }
    }

    private boolean saveData() {
        try (OutputStreamWriter writer = new OutputStreamWriter(
                new FileOutputStream(filePath), StandardCharsets.UTF_8)) {

            List<Object> phonesArray = new ArrayList<>();

            for (Phone phone : phones) {
                Map<String, Object> phoneObj = new HashMap<>();
                phoneObj.put("name", phone.getName());
                phoneObj.put("link", phone.getLink());
                phoneObj.put("price", phone.getPrice());
                phoneObj.put("viewCount", phone.getViewCount());

                if (phone.getImageUrl() != null && !phone.getImageUrl().isEmpty()) {
                    phoneObj.put("imgURL", phone.getImageUrl());
                }

                Map<String, Object> descObj = new HashMap<>();
                PhoneDescription desc = phone.getDescription();

                for (String key : desc.getAllAttributes().keySet()) {
                    descObj.put(key, desc.getAttribute(key));
                }

                phoneObj.put("description", descObj);
                phonesArray.add(phoneObj);
            }

            String jsonString = SimpleJSON.toJSON(phonesArray);
            writer.write(jsonString);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Lỗi khi ghi file dữ liệu: " + e.getMessage());
            return false;
        }
    }

    // Các phương thức khác giữ nguyên...
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
        Optional<Phone> existing = findPhoneByLink(phone.getLink());

        if (existing.isPresent()) {
            int index = phones.indexOf(existing.get());
            phones.set(index, phone);
        } else {
            phones.add(phone);
        }

        return saveData();
    }

    @Override
    public boolean deletePhone(Phone phone) {
        boolean removed = phones.removeIf(p -> p.getLink().equals(phone.getLink()));
        if (removed) {
            return saveData();
        }
        return false;
    }
}