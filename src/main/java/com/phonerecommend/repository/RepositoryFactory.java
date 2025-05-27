package com.phonerecommend.repository;

import com.phonerecommend.repository.impl.LocalJsonPhoneRepositoryWithOrgJson;
import com.phonerecommend.repository.impl.FirebaseFirestoreRepository;

/**
 * Factory để tạo các Repository, tuân thủ nguyên tắc Factory Method Pattern
 * Updated để support Firebase với config thực tế
 */
public class RepositoryFactory {

    public enum RepositoryType {
        LOCAL_JSON,
        FIREBASE_FIRESTORE,
        MONGODB
    }

    // Cấu hình mặc định
    private static RepositoryType currentType = RepositoryType.LOCAL_JSON;

    // Local JSON config
    private static String localJsonPath = "data/phones.json";

    // Firebase config - sử dụng project thực tế của bạn
    private static String firestoreProjectId = "admindashboard-f6703";

    // MongoDB config (cho tương lai)
    private static String mongoDbConnectionString = "mongodb://localhost:27017";

    private static PhoneRepository instance;

    /**
     * Lấy instance của PhoneRepository
     * @return PhoneRepository instance
     */
    public static PhoneRepository getPhoneRepository() {
        if (instance == null) {
            createRepository();
        }
        return instance;
    }

    /**
     * Thiết lập loại repository - ĐÂY LÀ ĐIỂM CHUYỂN ĐỔI DUY NHẤT
     * @param type Loại repository
     */
    public static void setRepositoryType(RepositoryType type) {
        if (currentType != type) {
            currentType = type;
            instance = null; // Reset để tạo mới repository
            System.out.println("Repository type changed to: " + type);
        }
    }

    /**
     * Thiết lập đường dẫn file JSON (cho LOCAL_JSON)
     * @param path Đường dẫn file JSON
     */
    public static void setLocalJsonPath(String path) {
        localJsonPath = path;
        if (currentType == RepositoryType.LOCAL_JSON) {
            instance = null; // Reset nếu đang sử dụng LOCAL_JSON
        }
    }

    /**
     * Thiết lập Project ID cho Firestore (cho FIREBASE_FIRESTORE)
     * @param projectId Firestore Project ID
     */
    public static void setFirestoreProjectId(String projectId) {
        firestoreProjectId = projectId;
        if (currentType == RepositoryType.FIREBASE_FIRESTORE) {
            instance = null; // Reset nếu đang sử dụng FIREBASE_FIRESTORE
        }
    }

    /**
     * Thiết lập connection string MongoDB (cho MONGODB)
     * @param connectionString Connection string MongoDB
     */
    public static void setMongoDbConnectionString(String connectionString) {
        mongoDbConnectionString = connectionString;
        if (currentType == RepositoryType.MONGODB) {
            instance = null; // Reset nếu đang sử dụng MONGODB
        }
    }

    /**
     * Tạo repository dựa trên loại đã thiết lập
     */
    private static void createRepository() {
        switch (currentType) {
            case LOCAL_JSON:
                System.out.println("=== INITIALIZING LOCAL JSON REPOSITORY ===");
                System.out.println("File path: " + localJsonPath);
                instance = new LocalJsonPhoneRepositoryWithOrgJson(localJsonPath);
                break;

            case FIREBASE_FIRESTORE:
                System.out.println("=== INITIALIZING FIREBASE FIRESTORE REPOSITORY ===");
                System.out.println("Project ID: " + firestoreProjectId);
                System.out.println("Collection: fix");
                instance = new FirebaseFirestoreRepository(firestoreProjectId);

                // In thông tin kết nối
                if (instance instanceof FirebaseFirestoreRepository) {
                    FirebaseFirestoreRepository firebaseRepo = (FirebaseFirestoreRepository) instance;
                    System.out.println("Connection info: " + firebaseRepo.getConnectionInfo());
                    System.out.println("Using Firebase: " + firebaseRepo.isUsingFirebase());
                }
                break;

            case MONGODB:
                System.out.println("=== MONGODB REPOSITORY ===");
                System.out.println("MongoDB repository chưa được triển khai");
                System.out.println("Chuyển về sử dụng Local JSON Repository");
                currentType = RepositoryType.LOCAL_JSON;
                instance = new LocalJsonPhoneRepositoryWithOrgJson(localJsonPath);
                break;

            default:
                System.err.println("Loại repository không được hỗ trợ: " + currentType);
                System.out.println("Sử dụng Local JSON Repository mặc định");
                currentType = RepositoryType.LOCAL_JSON;
                instance = new LocalJsonPhoneRepositoryWithOrgJson(localJsonPath);
                break;
        }

        System.out.println("Repository initialized successfully: " + instance.getClass().getSimpleName());
        System.out.println("=========================================");
    }

    /**
     * Lấy loại repository hiện tại
     * @return RepositoryType hiện tại
     */
    public static RepositoryType getCurrentType() {
        return currentType;
    }

    /**
     * Reset factory (để testing hoặc reconfigure)
     */
    public static void reset() {
        instance = null;
        System.out.println("Repository factory reset");
    }

    /**
     * Lấy thông tin cấu hình hiện tại
     * @return Chuỗi thông tin cấu hình
     */
    public static String getConfigurationInfo() {
        StringBuilder info = new StringBuilder();
        info.append("Repository Type: ").append(currentType).append("\n");

        switch (currentType) {
            case LOCAL_JSON:
                info.append("JSON File Path: ").append(localJsonPath);
                break;
            case FIREBASE_FIRESTORE:
                info.append("Firestore Project ID: ").append(firestoreProjectId);
                info.append("\nCollection: fix");
                break;
            case MONGODB:
                info.append("MongoDB Connection: ").append(mongoDbConnectionString);
                break;
        }

        return info.toString();
    }

    /**
     * Kiểm tra xem repository đã được khởi tạo chưa
     * @return true nếu đã khởi tạo, false nếu chưa
     */
    public static boolean isInitialized() {
        return instance != null;
    }

    /**
     * Convenience methods để chuyển đổi nhanh
     */
    public static void useLocalJson() {
        setRepositoryType(RepositoryType.LOCAL_JSON);
    }

    public static void useFirebase() {
        setRepositoryType(RepositoryType.FIREBASE_FIRESTORE);
    }

    public static void useMongoDB() {
        setRepositoryType(RepositoryType.MONGODB);
    }
}