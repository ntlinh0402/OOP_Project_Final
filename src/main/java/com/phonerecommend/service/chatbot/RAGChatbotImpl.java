package com.phonerecommend.service.chatbot;

import com.phonerecommend.model.Phone;
import com.phonerecommend.repository.PhoneRepository;
import com.phonerecommend.service.chatbot.ChatbotService;
import com.phonerecommend.service.chatbot.PhoneDataEmbedding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Triển khai chatbot RAG (Retrieval-Augmented Generation)
 * sử dụng dữ liệu điện thoại
 */
public class RAGChatbotImpl implements ChatbotService {

    private final PhoneRepository phoneRepository;
    private List<PhoneDataEmbedding> dataEmbeddings;
    private boolean isInitialized;

    // Kích thước vector nhúng
    private static final int EMBEDDING_SIZE = 384;

    // Số lượng tài liệu tìm kiếm tối đa
    private static final int MAX_SEARCH_RESULTS = 5;

    /**
     * Constructor với repository
     * @param phoneRepository Repository để lấy dữ liệu điện thoại
     */
    public RAGChatbotImpl(PhoneRepository phoneRepository) {
        this.phoneRepository = phoneRepository;
        this.dataEmbeddings = new ArrayList<>();
        this.isInitialized = false;
    }

    @Override
    public boolean initialize() {
        try {
            // Lấy danh sách điện thoại
            List<Phone> phones = phoneRepository.getAllPhones();

            // Tạo embeddings cho từng điện thoại
            for (Phone phone : phones) {
                // Tạo nội dung cần nhúng
                String content = createContentForEmbedding(phone);

                // Tạo vector nhúng (embedding) từ nội dung
                // Trong thực tế, sẽ sử dụng mô hình NLP để tạo embeddings
                float[] embedding = createEmbeddingForContent(content);

                // Tạo metadata
                Map<String, Object> metadata = new HashMap<>();
                metadata.put("name", phone.getName());
                metadata.put("price", phone.getPrice());
                metadata.put("brand", extractBrand(phone.getName()));

                // Tạo đối tượng PhoneDataEmbedding
                PhoneDataEmbedding dataEmbedding = new PhoneDataEmbedding(
                        phone.getLink(),
                        content,
                        embedding,
                        metadata,
                        phone
                );

                dataEmbeddings.add(dataEmbedding);
            }

            isInitialized = true;
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public String processQuestion(String question) {
        if (!isInitialized) {
            return "Chatbot chưa được khởi tạo. Vui lòng thử lại sau.";
        }

        try {
            // Tạo embedding cho câu hỏi
            float[] questionEmbedding = createEmbeddingForContent(question);

            // Tìm kiếm các tài liệu liên quan nhất
            List<PhoneDataEmbedding> relevantDocs = retrieveRelevantDocuments(questionEmbedding);

            // Tạo câu trả lời từ các tài liệu liên quan
            return generateAnswer(question, relevantDocs);
        } catch (Exception e) {
            e.printStackTrace();
            return "Xin lỗi, đã xảy ra lỗi khi xử lý câu hỏi của bạn. Vui lòng thử lại.";
        }
    }

    @Override
    public boolean updateData() {
        // Reset data embeddings
        dataEmbeddings.clear();
        isInitialized = false;

        // Khởi tạo lại
        return initialize();
    }

    @Override
    public boolean isReady() {
        return isInitialized;
    }

    @Override
    public List<String> getSuggestedQuestions() {
        return Arrays.asList(
                "Điện thoại nào pin trâu nhất?",
                "Điện thoại nào phù hợp để chơi game?",
                "Điện thoại nào có camera tốt nhất?",
                "iPhone mới nhất có gì đặc biệt?",
                "Điện thoại nào hỗ trợ 5G với giá dưới 10 triệu?"
        );
    }

    /**
     * Tạo nội dung để nhúng từ đối tượng Phone
     * @param phone Đối tượng Phone
     * @return Nội dung dạng chuỗi
     */
    private String createContentForEmbedding(Phone phone) {
        StringBuilder content = new StringBuilder();

        // Thêm tên
        content.append("Tên: ").append(phone.getName()).append("\n");

        // Thêm giá
        content.append("Giá: ").append(String.format("%,.0f", phone.getPrice())).append(" VNĐ\n");

        // Thêm thông tin từ description
        for (Map.Entry<String, String> entry : phone.getDescription().getAllAttributes().entrySet()) {
            content.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }

        return content.toString();
    }

    /**
     * Tạo vector nhúng cho nội dung
     * @param content Nội dung cần nhúng
     * @return Vector nhúng
     *
     * Lưu ý: Trong triển khai thực tế, phương thức này sẽ sử dụng
     * mô hình NLP như Sentence-BERT để tạo embeddings. Ở đây, chúng ta
     * mô phỏng đơn giản bằng cách tạo vector ngẫu nhiên.
     */
    private float[] createEmbeddingForContent(String content) {
        // Mô phỏng vector nhúng bằng cách băm nội dung
        float[] embedding = new float[EMBEDDING_SIZE];

        // Sử dụng hash code của nội dung để tạo vector giả
        int hash = content.hashCode();

        for (int i = 0; i < EMBEDDING_SIZE; i++) {
            // Tạo giá trị giả từ hash code
            embedding[i] = (float) Math.sin(hash * (i + 1)) * 0.5f;
            hash = hash * 31 + i;
        }

        // Chuẩn hóa vector
        double norm = 0.0;
        for (float value : embedding) {
            norm += value * value;
        }
        norm = Math.sqrt(norm);

        if (norm > 0) {
            for (int i = 0; i < embedding.length; i++) {
                embedding[i] /= norm;
            }
        }

        return embedding;
    }

    /**
     * Trích xuất tên hãng từ tên điện thoại
     * @param phoneName Tên điện thoại
     * @return Tên hãng
     */
    private String extractBrand(String phoneName) {
        phoneName = phoneName.toLowerCase();

        if (phoneName.contains("iphone") || phoneName.contains("apple")) {
            return "Apple";
        } else if (phoneName.contains("samsung") || phoneName.contains("galaxy")) {
            return "Samsung";
        } else if (phoneName.contains("xiaomi") || phoneName.contains("redmi") || phoneName.contains("poco")) {
            return "Xiaomi";
        } else if (phoneName.contains("oppo")) {
            return "Oppo";
        } else if (phoneName.contains("vivo")) {
            return "Vivo";
        } else if (phoneName.contains("huawei")) {
            return "Huawei";
        } else if (phoneName.contains("realme")) {
            return "Realme";
        } else if (phoneName.contains("nokia")) {
            return "Nokia";
        } else if (phoneName.contains("sony")) {
            return "Sony";
        } else if (phoneName.contains("asus") || phoneName.contains("rog")) {
            return "Asus";
        }

        return "Khác";
    }

    /**
     * Tìm kiếm các tài liệu liên quan nhất dựa trên vector nhúng
     * @param questionEmbedding Vector nhúng của câu hỏi
     * @return Danh sách các tài liệu liên quan
     */
    private List<PhoneDataEmbedding> retrieveRelevantDocuments(float[] questionEmbedding) {
        // Tính độ tương đồng của câu hỏi với từng tài liệu
        List<Map.Entry<PhoneDataEmbedding, Double>> embeddings = dataEmbeddings.stream()
                .map(embedding -> Map.entry(embedding, calculateSimilarity(questionEmbedding, embedding.getEmbedding())))
                .sorted(Map.Entry.<PhoneDataEmbedding, Double>comparingByValue().reversed())
                .limit(MAX_SEARCH_RESULTS)
                .collect(Collectors.toList());

        // Chỉ giữ lại các tài liệu có độ tương đồng đủ cao
        double minSimilarity = 0.2; // Ngưỡng tương đồng tối thiểu

        return embeddings.stream()
                .filter(entry -> entry.getValue() >= minSimilarity)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    /**
     * Tính độ tương đồng giữa hai vector nhúng
     * @param vec1 Vector thứ nhất
     * @param vec2 Vector thứ hai
     * @return Độ tương đồng (0-1)
     */
    private double calculateSimilarity(float[] vec1, float[] vec2) {
        // Sử dụng độ tương đồng cosine
        double dotProduct = 0.0;
        double norm1 = 0.0;
        double norm2 = 0.0;

        for (int i = 0; i < vec1.length; i++) {
            dotProduct += vec1[i] * vec2[i];
            norm1 += vec1[i] * vec1[i];
            norm2 += vec2[i] * vec2[i];
        }

        // Tránh chia cho 0
        if (norm1 == 0 || norm2 == 0) {
            return 0.0;
        }

        return dotProduct / (Math.sqrt(norm1) * Math.sqrt(norm2));
    }

    /**
     * Tạo câu trả lời từ câu hỏi và các tài liệu liên quan
     * @param question Câu hỏi
     * @param relevantDocs Các tài liệu liên quan
     * @return Câu trả lời
     */
    private String generateAnswer(String question, List<PhoneDataEmbedding> relevantDocs) {
        if (relevantDocs.isEmpty()) {
            return "Xin lỗi, tôi không tìm thấy thông tin phù hợp để trả lời câu hỏi của bạn. "
                    + "Vui lòng thử lại với câu hỏi khác hoặc tìm kiếm trực tiếp trên danh sách sản phẩm.";
        }

        question = question.toLowerCase();

        // Câu trả lời sẽ phụ thuộc vào loại câu hỏi
        if (question.contains("pin trâu") || question.contains("pin khỏe") || question.contains("pin lâu")) {
            return generateBatteryAnswer(relevantDocs);
        } else if (question.contains("chơi game") || question.contains("gaming")) {
            return generateGamingAnswer(relevantDocs);
        } else if (question.contains("camera") || question.contains("chụp ảnh") || question.contains("quay phim")) {
            return generateCameraAnswer(relevantDocs);
        } else if (question.contains("giá") || question.contains("rẻ") || question.contains("mắc") || question.contains("đắt")) {
            return generatePriceAnswer(question, relevantDocs);
        } else if (question.contains("so sánh")) {
            return generateComparisonAnswer(question, relevantDocs);
        } else {
            return generateGenericAnswer(relevantDocs);
        }
    }

    /**
     * Tạo câu trả lời về pin
     */
    private String generateBatteryAnswer(List<PhoneDataEmbedding> relevantDocs) {
        StringBuilder answer = new StringBuilder("Dựa trên dữ liệu của tôi, các điện thoại có pin trâu nhất bao gồm:\n\n");

        List<Phone> phones = relevantDocs.stream()
                .map(PhoneDataEmbedding::getPhone)
                .sorted((p1, p2) -> {
                    // So sánh dung lượng pin
                    String battery1 = p1.getDescription().getAttribute("Pin");
                    String battery2 = p2.getDescription().getAttribute("Pin");

                    int cap1 = extractBatteryCapacity(battery1);
                    int cap2 = extractBatteryCapacity(battery2);

                    return Integer.compare(cap2, cap1); // Giảm dần
                })
                .limit(3)
                .collect(Collectors.toList());

        for (Phone phone : phones) {
            answer.append("- ").append(phone.getName()).append(": ");
            answer.append(phone.getDescription().getAttribute("Pin"));
            answer.append(", Giá: ").append(String.format("%,.0f", phone.getPrice())).append(" VNĐ\n");
        }

        answer.append("\nTất cả đều có pin trâu đảm bảo sử dụng trong thời gian dài. ");
        answer.append("Bạn có thể xem thêm thông tin chi tiết bằng cách nhấp vào sản phẩm trên danh sách.");

        return answer.toString();
    }

    /**
     * Trích xuất dung lượng pin từ chuỗi
     */
    private int extractBatteryCapacity(String batteryInfo) {
        if (batteryInfo == null || batteryInfo.isEmpty()) {
            return 0;
        }

        try {
            // Trích xuất số từ chuỗi (ví dụ: "5000 mAh" -> 5000)
            String numericPart = batteryInfo.replaceAll("[^0-9]", "");
            return Integer.parseInt(numericPart);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /**
     * Tạo câu trả lời về gaming
     */
    private String generateGamingAnswer(List<PhoneDataEmbedding> relevantDocs) {
        StringBuilder answer = new StringBuilder("Dựa trên dữ liệu, các điện thoại phù hợp để chơi game bao gồm:\n\n");

        for (int i = 0; i < Math.min(3, relevantDocs.size()); i++) {
            Phone phone = relevantDocs.get(i).getPhone();
            answer.append("- ").append(phone.getName()).append("\n");
            answer.append("  + Chip: ").append(phone.getDescription().getChipset()).append("\n");
            answer.append("  + RAM: ").append(phone.getDescription().getRam()).append("\n");
            answer.append("  + Màn hình: ")
                    .append(phone.getDescription().getScreenSize())
                    .append(", ")
                    .append(phone.getDescription().getRefreshRate())
                    .append("\n");
            answer.append("  + Giá: ").append(String.format("%,.0f", phone.getPrice())).append(" VNĐ\n\n");
        }

        answer.append("Các điện thoại này có cấu hình mạnh, màn hình tần số quét cao và các tính năng tối ưu game, ");
        answer.append("giúp bạn có trải nghiệm chơi game tốt nhất.");

        return answer.toString();
    }

    /**
     * Tạo câu trả lời về camera
     */
    private String generateCameraAnswer(List<PhoneDataEmbedding> relevantDocs) {
        StringBuilder answer = new StringBuilder("Các điện thoại có camera tốt nhất bao gồm:\n\n");

        for (int i = 0; i < Math.min(3, relevantDocs.size()); i++) {
            Phone phone = relevantDocs.get(i).getPhone();
            answer.append("- ").append(phone.getName()).append("\n");
            answer.append("  + Camera sau: ").append(phone.getDescription().getRearCamera()).append("\n");
            answer.append("  + Camera trước: ").append(phone.getDescription().getFrontCamera()).append("\n");
            answer.append("  + Tính năng: ").append(phone.getDescription().getCameraFeatures()).append("\n");
            answer.append("  + Giá: ").append(String.format("%,.0f", phone.getPrice())).append(" VNĐ\n\n");
        }

        answer.append("Các điện thoại này được trang bị camera chất lượng cao, nhiều tính năng chụp ảnh thông minh ");
        answer.append("và khả năng quay video chuyên nghiệp.");

        return answer.toString();
    }

    /**
     * Tạo câu trả lời về giá cả
     */
    private String generatePriceAnswer(String question, List<PhoneDataEmbedding> relevantDocs) {
        double minPrice = Double.MAX_VALUE;
        double maxPrice = 0;

        // Tìm khoảng giá
        for (PhoneDataEmbedding doc : relevantDocs) {
            double price = doc.getPhone().getPrice();
            if (price < minPrice) minPrice = price;
            if (price > maxPrice) maxPrice = price;
        }

        boolean lookingForCheap = question.contains("rẻ") || question.contains("giá tốt") || question.contains("tiết kiệm");

        StringBuilder answer = new StringBuilder();

        if (lookingForCheap) {
            answer.append("Các điện thoại giá tốt phù hợp với nhu cầu của bạn:\n\n");

            relevantDocs.stream()
                    .map(PhoneDataEmbedding::getPhone)
                    .sorted(Comparator.comparingDouble(Phone::getPrice))
                    .limit(3)
                    .forEach(phone -> {
                        answer.append("- ").append(phone.getName())
                                .append(": ").append(String.format("%,.0f", phone.getPrice())).append(" VNĐ\n");
                    });
        } else {
            answer.append("Khoảng giá của các điện thoại phù hợp với nhu cầu của bạn:\n\n");
            answer.append("- Thấp nhất: ").append(String.format("%,.0f", minPrice)).append(" VNĐ\n");
            answer.append("- Cao nhất: ").append(String.format("%,.0f", maxPrice)).append(" VNĐ\n\n");

            answer.append("Một số điện thoại tiêu biểu:\n");
            relevantDocs.stream()
                    .map(PhoneDataEmbedding::getPhone)
                    .sorted(Comparator.comparingDouble(Phone::getPrice))
                    .limit(3)
                    .forEach(phone -> {
                        answer.append("- ").append(phone.getName())
                                .append(": ").append(String.format("%,.0f", phone.getPrice())).append(" VNĐ\n");
                    });
        }

        return answer.toString();
    }

    /**
     * Tạo câu trả lời so sánh điện thoại
     */
    private String generateComparisonAnswer(String question, List<PhoneDataEmbedding> relevantDocs) {
        if (relevantDocs.size() < 2) {
            return "Tôi không có đủ thông tin để so sánh các điện thoại. Vui lòng cung cấp tên cụ thể của các điện thoại bạn muốn so sánh.";
        }

        // Lấy 2 điện thoại đầu tiên để so sánh
        Phone phone1 = relevantDocs.get(0).getPhone();
        Phone phone2 = relevantDocs.get(1).getPhone();

        StringBuilder answer = new StringBuilder("So sánh giữa ").append(phone1.getName())
                .append(" và ").append(phone2.getName()).append(":\n\n");

        // So sánh giá
        answer.append("1. Giá bán:\n");
        answer.append("- ").append(phone1.getName()).append(": ").append(String.format("%,.0f", phone1.getPrice())).append(" VNĐ\n");
        answer.append("- ").append(phone2.getName()).append(": ").append(String.format("%,.0f", phone2.getPrice())).append(" VNĐ\n\n");

        // So sánh màn hình
        answer.append("2. Màn hình:\n");
        answer.append("- ").append(phone1.getName()).append(": ")
                .append(phone1.getDescription().getScreenSize()).append(", ")
                .append(phone1.getDescription().getScreenTechnology()).append(", ")
                .append(phone1.getDescription().getRefreshRate()).append("\n");
        answer.append("- ").append(phone2.getName()).append(": ")
                .append(phone2.getDescription().getScreenSize()).append(", ")
                .append(phone2.getDescription().getScreenTechnology()).append(", ")
                .append(phone2.getDescription().getRefreshRate()).append("\n\n");

        // So sánh camera
        answer.append("3. Camera:\n");
        answer.append("- ").append(phone1.getName()).append(": ").append(phone1.getDescription().getRearCamera()).append("\n");
        answer.append("- ").append(phone2.getName()).append(": ").append(phone2.getDescription().getRearCamera()).append("\n\n");

        // So sánh hiệu năng
        answer.append("4. Hiệu năng:\n");
        answer.append("- ").append(phone1.getName()).append(": ")
                .append(phone1.getDescription().getChipset()).append(", ")
                .append(phone1.getDescription().getRam()).append("\n");
        answer.append("- ").append(phone2.getName()).append(": ")
                .append(phone2.getDescription().getChipset()).append(", ")
                .append(phone2.getDescription().getRam()).append("\n\n");

        // So sánh pin
        answer.append("5. Pin và sạc:\n");
        answer.append("- ").append(phone1.getName()).append(": ")
                .append(phone1.getDescription().getAttribute("Pin")).append(", ")
                .append(phone1.getDescription().getAttribute("Công nghệ sạc")).append("\n");
        answer.append("- ").append(phone2.getName()).append(": ")
                .append(phone2.getDescription().getAttribute("Pin")).append(", ")
                .append(phone2.getDescription().getAttribute("Công nghệ sạc")).append("\n");

        return answer.toString();
    }

    /**
     * Tạo câu trả lời chung
     */
    private String generateGenericAnswer(List<PhoneDataEmbedding> relevantDocs) {
        StringBuilder answer = new StringBuilder("Dựa trên dữ liệu hiện có, tôi đề xuất các điện thoại sau:\n\n");

        for (int i = 0; i < Math.min(3, relevantDocs.size()); i++) {
            Phone phone = relevantDocs.get(i).getPhone();
            answer.append("- ").append(phone.getName()).append("\n");
            answer.append("  + Giá: ").append(String.format("%,.0f", phone.getPrice())).append(" VNĐ\n");
            answer.append("  + Màn hình: ").append(phone.getDescription().getScreenSize()).append(", ")
                    .append(phone.getDescription().getScreenTechnology()).append("\n");
            answer.append("  + Chip: ").append(phone.getDescription().getChipset()).append("\n");
            answer.append("  + RAM: ").append(phone.getDescription().getRam()).append("\n");
            answer.append("  + Bộ nhớ: ").append(phone.getDescription().getStorage()).append("\n");
            answer.append("  + Camera: ").append(phone.getDescription().getRearCamera()).append("\n\n");
        }

        answer.append("Bạn có thể xem thêm thông tin chi tiết bằng cách nhấp vào sản phẩm trong danh sách.");

        return answer.toString();
    }
}