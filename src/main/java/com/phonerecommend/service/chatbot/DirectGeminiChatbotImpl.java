package com.phonerecommend.service.chatbot;

import com.phonerecommend.model.Phone;
import com.phonerecommend.repository.PhoneRepository;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Direct Gemini API Integration - Tích hợp trực tiếp Gemini API
 */
public class DirectGeminiChatbotImpl implements ChatbotService {

    private final PhoneRepository phoneRepository;
    private final HttpClient httpClient;
    private List<Phone> phones;
    private boolean isInitialized;

    // THAY BẰNG API KEY CỦA BẠN - LẤY TỪ https://aistudio.google.com/
    private static String GEMINI_API_KEY = "YOUR_GEMINI_API_KEY_HERE";
    private final String GEMINI_API_URL_BASE = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash-exp:generateContent?key=";

    public DirectGeminiChatbotImpl(PhoneRepository phoneRepository) {
        this.phoneRepository = phoneRepository;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        this.isInitialized = false;
    }

    @Override
    public boolean initialize() {
        try {
            System.out.println("🔄 Khởi tạo Direct Gemini Chatbot...");

            // Kiểm tra API key
            if (GEMINI_API_KEY.equals("YOUR_GEMINI_API_KEY_HERE")) {
                System.err.println("❌ Chưa cấu hình GEMINI_API_KEY!");
                System.err.println("💡 Vui lòng lấy API key từ: https://aistudio.google.com/");
                return false;
            }

            // Tải dữ liệu điện thoại
            phones = phoneRepository.getAllPhones();
            System.out.println("✅ Đã tải " + phones.size() + " điện thoại");

            // Test Gemini API với câu hỏi đơn giản
            System.out.println("🔗 Test kết nối Gemini API...");
            String testResponse = callGeminiAPI("Xin chào", "");

            if (testResponse != null && !testResponse.isEmpty()) {
                System.out.println("✅ Kết nối Gemini API thành công!");
                System.out.println("🤖 Test response: " + testResponse.substring(0, Math.min(100, testResponse.length())) + "...");
                isInitialized = true;
                return true;
            } else {
                System.err.println("❌ Không thể kết nối Gemini API");
                return false;
            }

        } catch (Exception e) {
            System.err.println("❌ Lỗi khởi tạo: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public String processQuestion(String question) {
        if (!isInitialized) {
            return "❌ Chatbot chưa được khởi tạo. Vui lòng kiểm tra API key và thử lại.";
        }

        try {
            System.out.println("💬 Xử lý câu hỏi: " + question);

            // Tìm điện thoại liên quan
            List<Phone> relevantPhones = findRelevantPhones(question, 5);
            System.out.println("🔍 Tìm thấy " + relevantPhones.size() + " điện thoại liên quan");

            // Tạo context từ điện thoại
            String phoneContext = createPhoneContext(relevantPhones);

            // Gọi Gemini API
            String response = callGeminiAPI(question, phoneContext);

            return response != null ? response : "❌ Xin lỗi, không thể xử lý câu hỏi lúc này. Vui lòng thử lại.";

        } catch (Exception e) {
            System.err.println("❌ Lỗi xử lý câu hỏi: " + e.getMessage());
            return "❌ Xin lỗi, đã xảy ra lỗi khi xử lý câu hỏi của bạn. Vui lòng thử lại.";
        }
    }

    /**
     * Tìm điện thoại liên quan đến câu hỏi
     */
    private List<Phone> findRelevantPhones(String query, int limit) {
        String queryLower = query.toLowerCase();

        // Tìm kiếm theo multiple criteria
        return phones.stream()
                .filter(phone -> {
                    // Tìm trong tên điện thoại
                    if (phone.getName().toLowerCase().contains(queryLower)) {
                        return true;
                    }

                    // Tìm theo keywords đặc biệt
                    if (queryLower.contains("pin") || queryLower.contains("battery")) {
                        String battery = phone.getDescription().getAttribute("Pin");
                        return !battery.isEmpty();
                    }

                    if (queryLower.contains("camera") || queryLower.contains("chụp ảnh")) {
                        String camera = phone.getDescription().getAttribute("Camera sau");
                        return !camera.isEmpty();
                    }

                    if (queryLower.contains("game") || queryLower.contains("gaming")) {
                        String chipset = phone.getDescription().getAttribute("Chipset");
                        return chipset.toLowerCase().contains("snapdragon") ||
                                chipset.toLowerCase().contains("apple") ||
                                chipset.toLowerCase().contains("dimensity");
                    }

                    if (queryLower.contains("iphone") || queryLower.contains("apple")) {
                        return phone.getName().toLowerCase().contains("iphone");
                    }

                    if (queryLower.contains("samsung") || queryLower.contains("galaxy")) {
                        return phone.getName().toLowerCase().contains("samsung") ||
                                phone.getName().toLowerCase().contains("galaxy");
                    }

                    // Tìm theo giá
                    if (queryLower.contains("rẻ") || queryLower.contains("tiết kiệm")) {
                        return phone.getPrice() < 15000000; // Dưới 15 triệu
                    }

                    if (queryLower.contains("cao cấp") || queryLower.contains("đắt")) {
                        return phone.getPrice() > 20000000; // Trên 20 triệu
                    }

                    // Tìm trong description
                    for (String value : phone.getDescription().getAllAttributes().values()) {
                        if (value.toLowerCase().contains(queryLower)) {
                            return true;
                        }
                    }

                    return false;
                })
                .sorted(Comparator.comparingDouble(Phone::getPrice))
                .limit(limit)
                .collect(Collectors.toList());
    }

    /**
     * Tạo context từ danh sách điện thoại
     */
    private String createPhoneContext(List<Phone> relevantPhones) {
        if (relevantPhones.isEmpty()) {
            return "Không tìm thấy điện thoại phù hợp với yêu cầu.";
        }

        StringBuilder context = new StringBuilder("Dưới đây là các điện thoại phù hợp nhất:\n\n");

        for (int i = 0; i < relevantPhones.size(); i++) {
            Phone phone = relevantPhones.get(i);
            context.append(i + 1).append(". ").append(phone.getName()).append("\n");
            context.append("   💰 Giá: ").append(String.format("%,.0f", phone.getPrice())).append(" VNĐ\n");

            // Thêm specs quan trọng
            String[] importantSpecs = {
                    "Chipset", "Dung lượng RAM", "Bộ nhớ trong", "Pin",
                    "Camera sau", "Camera trước", "Kích thước màn hình",
                    "Công nghệ màn hình", "Tính năng đặc biệt", "Hỗ trợ mạng"
            };

            for (String spec : importantSpecs) {
                String value = phone.getDescription().getAttribute(spec);
                if (!value.isEmpty()) {
                    // Rút gọn nếu quá dài
                    if (value.length() > 150) {
                        value = value.substring(0, 150) + "...";
                    }
                    context.append("   📋 ").append(spec).append(": ").append(value).append("\n");
                }
            }
            context.append("\n");
        }

        return context.toString();
    }

    /**
     * Gọi Gemini API
     */
    private String callGeminiAPI(String question, String phoneContext) {
        try {
            // Tạo system prompt chuyên nghiệp
            String systemPrompt = """
                Bạn là chuyên gia tư vấn điện thoại chuyên nghiệp tại một cửa hàng công nghệ hàng đầu Việt Nam.

                NHIỆM VỤ:
                - Tư vấn điện thoại dựa trên thông tin sản phẩm được cung cấp
                - Đưa ra lời khuyên chuyên nghiệp, khách quan và hữu ích
                - Giải thích rõ ràng, dễ hiểu cho người dùng

                PHONG CÁCH TƯ VẤN:
                - Thân thiện, nhiệt tình nhưng chuyên nghiệp
                - Sử dụng emoji phù hợp để dễ đọc
                - Đưa ra 2-3 lựa chọn cụ thể với lý do rõ ràng
                - So sánh ưu nhược điểm một cách khách quan
                - Luôn đề cập đến giá cả và tính phù hợp
                - Kết thúc bằng câu hỏi để tiếp tục hỗ trợ

                QUY TẮC QUAN TRỌNG:
                - Chỉ tư vấn dựa trên thông tin sản phẩm được cung cấp
                - Không bịa đặt thông tin không có
                - Nếu không có sản phẩm phù hợp, giải thích rõ lý do
                - Luôn trả lời bằng tiếng Việt
                """;

            String fullPrompt = systemPrompt + "\n\n📱 THÔNG TIN SẢN PHẨM:\n" + phoneContext +
                    "\n\n💬 KHÁCH HÀNG HỎI: " + question +
                    "\n\n🎯 HÃY TƯ VẤN CHUYÊN NGHIỆP:";

            // Tạo JSON request cho Gemini API
            JSONObject textPart = new JSONObject();
            textPart.put("text", fullPrompt);

            JSONArray parts = new JSONArray();
            parts.put(textPart);

            JSONObject content = new JSONObject();
            content.put("parts", parts);

            JSONArray contents = new JSONArray();
            contents.put(content);

            // Cấu hình generation
            JSONObject generationConfig = new JSONObject();
            generationConfig.put("temperature", 0.7);
            generationConfig.put("topP", 0.8);
            generationConfig.put("topK", 40);
            generationConfig.put("maxOutputTokens", 2048);

            JSONObject requestBody = new JSONObject();
            requestBody.put("contents", contents);
            requestBody.put("generationConfig", generationConfig);

            // Gửi request đến Gemini API
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(getApiUrl()))
                    .timeout(Duration.ofSeconds(30))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
                    .build();

            HttpResponse<String> response = httpClient.send(request,
                    HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JSONObject jsonResponse = new JSONObject(response.body());
                JSONArray candidates = jsonResponse.getJSONArray("candidates");

                if (candidates.length() > 0) {
                    JSONObject candidate = candidates.getJSONObject(0);
                    JSONObject contentResponse = candidate.getJSONObject("content");
                    JSONArray partsResponse = contentResponse.getJSONArray("parts");

                    if (partsResponse.length() > 0) {
                        String aiResponse = partsResponse.getJSONObject(0).getString("text");
                        System.out.println("✅ Gemini API response received: " + aiResponse.length() + " characters");
                        return aiResponse;
                    }
                }
            } else {
                System.err.println("❌ Gemini API HTTP error: " + response.statusCode());
                System.err.println("Response body: " + response.body());

                if (response.statusCode() == 400) {
                    return "❌ Lỗi API: Yêu cầu không hợp lệ. Vui lòng kiểm tra API key.";
                } else if (response.statusCode() == 403) {
                    return "❌ Lỗi API: API key không hợp lệ hoặc đã hết quota.";
                }
            }

        } catch (IOException | InterruptedException e) {
            System.err.println("❌ Network error calling Gemini API: " + e.getMessage());
            return "❌ Lỗi kết nối mạng. Vui lòng kiểm tra internet và thử lại.";
        } catch (Exception e) {
            System.err.println("❌ Exception calling Gemini API: " + e.getMessage());
            e.printStackTrace();
        }

        return "❌ Xin lỗi, không thể kết nối với hệ thống AI lúc này. Vui lòng thử lại sau.";
    }

    @Override
    public boolean updateData() {
        // Reload phone data
        phones = phoneRepository.getAllPhones();
        System.out.println("🔄 Đã cập nhật " + phones.size() + " điện thoại");
        return true;
    }

    @Override
    public boolean isReady() {
        return isInitialized;
    }

    @Override
    public List<String> getSuggestedQuestions() {
        return Arrays.asList(
                "Điện thoại nào pin trâu nhất trong tầm giá 15 triệu?",
                "Tư vấn điện thoại chụp ảnh đẹp dưới 20 triệu",
                "So sánh iPhone và Samsung flagship mới nhất",
                "Điện thoại gaming tốt nhất hiện tại",
                "Điện thoại nào hỗ trợ 5G với giá tốt nhất?",
                "Tư vấn điện thoại cho người già dễ sử dụng",
                "Điện thoại học sinh sinh viên giá rẻ chất lượng"
        );
    }

    /**
     * Get API status info
     */
    public String getAPIStatus() {
        if (!isInitialized) {
            return "❌ Chưa khởi tạo";
        }

        boolean connected = testAPIConnection();
        return connected ? "✅ Kết nối bình thường" : "❌ Mất kết nối API";
    }

    /**
     * Set API key dynamically
     */
    public void setApiKey(String apiKey) {
        GEMINI_API_KEY = apiKey;
        System.out.println("✅ Gemini API key updated");
    }

    /**
     * Get current API URL
     */
    private String getApiUrl() {
        return GEMINI_API_URL_BASE + GEMINI_API_KEY;
    }

    /**
     * Kiểm tra kết nối API
     */
    public boolean testAPIConnection() {
        try {
            String response = callGeminiAPI("Test", "");
            return response != null && !response.isEmpty();
        } catch (Exception e) {
            return false;
        }
    }
}