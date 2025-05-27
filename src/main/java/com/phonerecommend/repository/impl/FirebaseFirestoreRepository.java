package com.phonerecommend.repository.impl;

import com.phonerecommend.repository.AbstractPhoneRepository;
import com.phonerecommend.model.Phone;
import com.phonerecommend.model.PhoneDescription;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.concurrent.ExecutionException;
import java.io.FileInputStream;
import java.io.IOException;

// Firebase imports
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.api.core.ApiFuture;

/**
 * Firebase Firestore Repository Implementation
 * Project: admindashboard-f6703
 * Collection: fix
 */
public class FirebaseFirestoreRepository extends AbstractPhoneRepository {

    private Firestore db;
    private final String projectId;
    private final Map<String, Phone> phoneCache;

    // Firebase Configuration
    private static final String PHONES_COLLECTION = "fix"; // Collection name
    private static final String SERVICE_ACCOUNT_PATH = "src/main/resources/serviceAccountKey.json";

    // Fallback: Sử dụng sample data khi không connect được Firebase
    private boolean useFirebase = false;

    public FirebaseFirestoreRepository(String projectId) {
        this.projectId = projectId;
        this.phoneCache = new HashMap<>();
        initializeFirebase();
        loadPhonesData();
    }

    /**
     * Khởi tạo Firebase connection
     */
    private void initializeFirebase() {
        try {
            System.out.println("Attempting to initialize Firebase for project: " + projectId);

            // Kiểm tra xem Firebase App đã được khởi tạo chưa
            if (FirebaseApp.getApps().isEmpty()) {

                // Cách 1: Sử dụng Service Account Key file (nếu có)
                try {
                    FileInputStream serviceAccount = new FileInputStream(SERVICE_ACCOUNT_PATH);
                    FirebaseOptions options = FirebaseOptions.builder()
                            .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                            .setProjectId(projectId)
                            .build();
                    FirebaseApp.initializeApp(options);
                    System.out.println("Firebase initialized with service account key");
                } catch (IOException e) {
                    System.out.println("Service account key not found, trying default credentials...");

                    // Cách 2: Sử dụng default credentials (Google Cloud SDK)
                    FirebaseOptions options = FirebaseOptions.builder()
                            .setCredentials(GoogleCredentials.getApplicationDefault())
                            .setProjectId(projectId)
                            .build();
                    FirebaseApp.initializeApp(options);
                    System.out.println("Firebase initialized with default credentials");
                }
            }

            // Lấy Firestore instance
            db = FirestoreClient.getFirestore();
            useFirebase = true;
            System.out.println("Firestore client obtained successfully");

        } catch (Exception e) {
            System.err.println("Failed to initialize Firebase: " + e.getMessage());
            System.err.println("Falling back to sample data mode");
            useFirebase = false;
        }
    }

    /**
     * Load phones data - từ Firebase hoặc sample data
     */
    private void loadPhonesData() {
        if (useFirebase) {
            loadPhonesFromFirestore();
        } else {
            System.out.println("Using sample data instead of Firebase");
            loadSampleData();
        }
    }

    /**
     * Tải dữ liệu phones từ Firestore collection "fix"
     */
    private void loadPhonesFromFirestore() {
        try {
            System.out.println("Loading phones from Firestore collection: " + PHONES_COLLECTION);

            CollectionReference phonesCollection = db.collection(PHONES_COLLECTION);
            ApiFuture<QuerySnapshot> query = phonesCollection.get();
            QuerySnapshot querySnapshot = query.get();

            List<QueryDocumentSnapshot> documents = querySnapshot.getDocuments();
            System.out.println("Found " + documents.size() + " documents in Firestore");

            for (QueryDocumentSnapshot document : documents) {
                Phone phone = documentToPhone(document);
                if (phone != null) {
                    phoneCache.put(phone.getLink(), phone);
                    System.out.println("Loaded phone: " + phone.getName());
                }
            }

            System.out.println("Successfully loaded " + phoneCache.size() + " phones from Firestore");

        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Error loading data from Firestore: " + e.getMessage());
            e.printStackTrace();
            // Fallback to sample data
            System.out.println("Falling back to sample data");
            loadSampleData();
        }
    }

    /**
     * Chuyển đổi Firestore document thành Phone object
     */
    private Phone documentToPhone(QueryDocumentSnapshot document) {
        try {
            Map<String, Object> data = document.getData();
            Phone phone = new Phone();

            // Document ID thường là tên phone, nhưng ta dùng field name
            String name = (String) data.get("name");
            if (name == null || name.isEmpty()) {
                name = document.getId(); // Fallback to document ID
            }
            phone.setName(name);

            // Link
            String link = (String) data.get("link");
            if (link != null) {
                phone.setLink(link);
            }

            // Price
            if (data.get("price") != null) {
                Object priceObj = data.get("price");
                if (priceObj instanceof Number) {
                    phone.setPrice(((Number) priceObj).doubleValue());
                } else if (priceObj instanceof String) {
                    try {
                        phone.setPrice(Double.parseDouble((String) priceObj));
                    } catch (NumberFormatException e) {
                        phone.setPrice(0);
                    }
                }
            }

            // View count
            if (data.get("viewCount") != null) {
                phone.setViewCount(((Number) data.get("viewCount")).intValue());
            }

            // Image URL
            if (data.get("imgURL") != null) {
                phone.setImageUrl((String) data.get("imgURL"));
            }

            // Description - đây là phần quan trọng nhất
            PhoneDescription description = new PhoneDescription();
            Object descriptionData = data.get("description");

            if (descriptionData instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> descMap = (Map<String, Object>) descriptionData;

                System.out.println("Processing description for " + name + " with " + descMap.size() + " attributes");

                for (Map.Entry<String, Object> entry : descMap.entrySet()) {
                    String key = entry.getKey();
                    String value = entry.getValue() != null ? entry.getValue().toString() : "";
                    description.setAttribute(key, value);
                }
            }

            phone.setDescription(description);
            return phone;

        } catch (Exception e) {
            System.err.println("Error converting document '" + document.getId() + "' to Phone: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Load sample data khi không kết nối được Firebase
     */
    private void loadSampleData() {
        System.out.println("Loading sample data...");

        // iPhone 16 Pro Max
        Phone iphone = new Phone();
        iphone.setName("iPhone 16 Pro Max 256GB");
        iphone.setLink("https://cellphones.com.vn/iphone-16-pro-max.html");
        iphone.setPrice(30490000);
        iphone.setViewCount(0);
        iphone.setImageUrl("https://cdn2.cellphones.com.vn/insecure/rs:fill:358:358/q:90/plain/https://cellphones.com.vn/media/catalog/product/i/p/iphone-16-pro-max.png");

        PhoneDescription iphoneDesc = new PhoneDescription();
        iphoneDesc.setAttribute("Kích thước màn hình", "6.9 inches");
        iphoneDesc.setAttribute("Công nghệ màn hình", "Super Retina XDR OLED");
        iphoneDesc.setAttribute("Chipset", "Apple A18 Pro");
        iphoneDesc.setAttribute("Dung lượng RAM", "8 GB");
        iphoneDesc.setAttribute("Bộ nhớ trong", "256 GB");
        iphoneDesc.setAttribute("Pin", "4441 mAh");
        iphoneDesc.setAttribute("Hỗ trợ mạng", "5G");
        iphoneDesc.setAttribute("Tính năng đặc biệt", "Hỗ trợ 5G, Sạc không dây, Nhận diện khuôn mặt, Kháng nước, kháng bụi, Điện thoại AI");

        iphone.setDescription(iphoneDesc);
        phoneCache.put(iphone.getLink(), iphone);

        // Samsung Galaxy S25 Ultra
        Phone samsung = new Phone();
        samsung.setName("Samsung Galaxy S25 Ultra 12GB 256GB");
        samsung.setLink("https://cellphones.com.vn/dien-thoai-samsung-galaxy-s25-ultra.html");
        samsung.setPrice(28990000);
        samsung.setViewCount(0);
        samsung.setImageUrl("https://cdn2.cellphones.com.vn/insecure/rs:fill:358:358/q:90/plain/https://cellphones.com.vn/media/catalog/product/d/i/dien-thoai-samsung-galaxy-s25-ultra_3__3.png");

        PhoneDescription samsungDesc = new PhoneDescription();
        samsungDesc.setAttribute("Kích thước màn hình", "6.9 inches");
        samsungDesc.setAttribute("Công nghệ màn hình", "Dynamic AMOLED 2X");
        samsungDesc.setAttribute("Chipset", "Snapdragon 8 Elite dành cho Galaxy (3nm)");
        samsungDesc.setAttribute("Dung lượng RAM", "12 GB");
        samsungDesc.setAttribute("Bộ nhớ trong", "256 GB");
        samsungDesc.setAttribute("Pin", "5000 mAh");
        samsungDesc.setAttribute("Hỗ trợ mạng", "5G");
        samsungDesc.setAttribute("Tính năng đặc biệt", "Hỗ trợ 5G, Nhận diện khuôn mặt, Kháng nước, kháng bụi, Điện thoại AI, Đi kèm bút cảm ứng");

        samsung.setDescription(samsungDesc);
        phoneCache.put(samsung.getLink(), samsung);

        // ASUS 8z (giống như trong Firebase)
        Phone asus = new Phone();
        asus.setName("ASUS 8z");
        asus.setLink("https://cellphones.com.vn/asus-8z.html");
        asus.setPrice(0); // Như trong Firebase
        asus.setViewCount(0);
        asus.setImageUrl("https://cellphones.com.vn/media/catalog/product/1/3/13_5_51.jpg");

        PhoneDescription asusDesc = new PhoneDescription();
        asusDesc.setAttribute("Bluetooth", "5.2 (EDR + A2DP), hỗ trợ LDAC, Qualcomm® aptX™, aptX™ HD, aptX™ Adaptive, AAC");
        asusDesc.setAttribute("Bộ nhớ trong", "128 GB");
        asusDesc.setAttribute("Camera sau", "Camera chính: 64 MP, f/1.73 16 MP");
        asusDesc.setAttribute("Camera trước", "12 MP");
        asusDesc.setAttribute("Chipset", "Qualcomm® Snapdragon™ 888 5G");
        asusDesc.setAttribute("Chất liệu khung viền", "-");
        asusDesc.setAttribute("Chỉ số kháng nước, bụi", "IP65");
        asusDesc.setAttribute("Các loại cảm biến", "Cảm biến gia tốc, Cảm biến tiệm cận, Cảm biến ánh sáng, La bàn, Con quay hồi chuyển");
        asusDesc.setAttribute("Công nghệ NFC", "Có");

        asus.setDescription(asusDesc);
        phoneCache.put(asus.getLink(), asus);

        System.out.println("Loaded " + phoneCache.size() + " sample phones");
    }

    @Override
    public List<Phone> getAllPhones() {
        return new ArrayList<>(phoneCache.values());
    }

    @Override
    public Optional<Phone> findPhoneByName(String name) {
        return phoneCache.values().stream()
                .filter(p -> p.getName().equalsIgnoreCase(name))
                .findFirst();
    }

    @Override
    public Optional<Phone> findPhoneByLink(String link) {
        return Optional.ofNullable(phoneCache.get(link));
    }

    @Override
    public List<Phone> searchPhones(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllPhones();
        }

        String lowercaseKeyword = keyword.toLowerCase();
        return phoneCache.values().stream()
                .filter(p -> p.getName().toLowerCase().contains(lowercaseKeyword))
                .collect(Collectors.toList());
    }

    @Override
    public boolean savePhone(Phone phone) {
        if (!useFirebase) {
            // Chỉ lưu vào cache khi không dùng Firebase
            phoneCache.put(phone.getLink(), phone);
            return true;
        }

        try {
            // Tạo document ID từ tên phone (clean string)
            String documentId = phone.getName()
                    .replaceAll("[^a-zA-Z0-9\\s]", "")
                    .replaceAll("\\s+", "_");

            // Tạo map dữ liệu để lưu vào Firestore
            Map<String, Object> phoneData = phoneToDocumentData(phone);

            // Lưu vào Firestore
            DocumentReference docRef = db.collection(PHONES_COLLECTION).document(documentId);
            ApiFuture<WriteResult> result = docRef.set(phoneData);

            // Đợi cho đến khi ghi xong
            result.get();

            // Cập nhật cache
            phoneCache.put(phone.getLink(), phone);

            System.out.println("Successfully saved phone to Firestore: " + phone.getName());
            return true;

        } catch (Exception e) {
            System.err.println("Error saving phone to Firestore: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deletePhone(Phone phone) {
        if (!useFirebase) {
            // Chỉ xóa khỏi cache khi không dùng Firebase
            phoneCache.remove(phone.getLink());
            return true;
        }

        try {
            // Tạo document ID từ tên phone
            String documentId = phone.getName()
                    .replaceAll("[^a-zA-Z0-9\\s]", "")
                    .replaceAll("\\s+", "_");

            DocumentReference docRef = db.collection(PHONES_COLLECTION).document(documentId);
            ApiFuture<WriteResult> result = docRef.delete();
            result.get();

            // Xóa khỏi cache
            phoneCache.remove(phone.getLink());

            System.out.println("Successfully deleted phone from Firestore: " + phone.getName());
            return true;

        } catch (Exception e) {
            System.err.println("Error deleting phone from Firestore: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Chuyển đổi Phone object thành Firestore document data
     */
    private Map<String, Object> phoneToDocumentData(Phone phone) {
        Map<String, Object> data = new HashMap<>();
        data.put("name", phone.getName());
        data.put("link", phone.getLink());
        data.put("price", phone.getPrice());
        data.put("viewCount", phone.getViewCount());

        if (phone.getImageUrl() != null) {
            data.put("imgURL", phone.getImageUrl());
        }

        // Chuyển đổi description thành map
        Map<String, String> descriptionMap = phone.getDescription().getAllAttributes();
        data.put("description", descriptionMap);

        return data;
    }

    /**
     * Đồng bộ cache với Firestore
     */
    public void syncWithFirestore() {
        if (useFirebase) {
            phoneCache.clear();
            loadPhonesFromFirestore();
        } else {
            System.out.println("Firebase not available, cannot sync");
        }
    }

    /**
     * Lấy thông tin về kết nối
     */
    public String getConnectionInfo() {
        String status = useFirebase ? "Connected to Firebase" : "Using sample data";
        return status + " - Project: " + projectId +
                ", Collection: " + PHONES_COLLECTION +
                ", Cached phones: " + phoneCache.size();
    }

    /**
     * Kiểm tra xem có đang dùng Firebase hay không
     */
    public boolean isUsingFirebase() {
        return useFirebase;
    }
}