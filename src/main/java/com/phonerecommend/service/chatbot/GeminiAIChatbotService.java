package com.phonerecommend.service.chatbot;

import com.phonerecommend.model.Phone;
import com.phonerecommend.repository.PhoneRepository;
import com.phonerecommend.service.chatbot.ChatbotService;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;
import java.util.Scanner;

import org.json.JSONObject;
import org.json.JSONArray;

/**
 * Gemini AI Chatbot Service - Sử dụng Google Gemini AI API
 * Chatbot thông minh có thể giao tiếp tự nhiên và tư vấn điện thoại
 */
public class GeminiAIChatbotService implements ChatbotService {

    private final PhoneRepository phoneRepository;
    private List<Phone> allPhones;
    private boolean isReady = false;

    // Gemini AI Configuration
    private static final String GEMINI_API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent";
    private final String apiKey;

    // Conversation history để maintain context
    private List<ConversationMessage> conversationHistory = new ArrayList<>();
    private static final int MAX_HISTORY_SIZE = 10;

    public GeminiAIChatbotService(PhoneRepository phoneRepository, String geminiApiKey) {
        this.phoneRepository = phoneRepository;
        this.apiKey = geminiApiKey;
    }

    @Override
    public boolean initialize() {
        try {
            System.out.println("🤖 Khởi tạo Gemini AI Chatbot...");

            if (apiKey == null || apiKey.trim().isEmpty()) {
                System.err.println("❌ API Key không hợp lệ!");
                return false;
            }

            // Load phone data
            allPhones = phoneRepository.getAllPhones();
            if (allPhones.isEmpty()) {
                System.err.println("❌ Không có dữ liệu điện thoại!");
                return false;
            }

            // Test API connection
            if (!testGeminiConnection()) {
                System.err.println("❌ Không thể kết nối đến Gemini API!");
                return false;
            }

            System.out.println("✅ Đã load " + allPhones.size() + " điện thoại");
            System.out.println("🧠 Gemini AI sẵn sàng!");

            isReady = true;
            return true;

        } catch (Exception e) {
            System.err.println("❌ Lỗi khởi tạo Gemini AI: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public String processQuestion(String question) {
        if (!isReady) {
            return "Chatbot chưa được khởi tạo. Vui lòng thử lại.";
        }

        if (question == null || question.trim().isEmpty()) {
            return "Vui lòng nhập câu hỏi của bạn.";
        }

        try {
            // Add user message to history
            addToHistory("user", question);

            // Get relevant phone data based on question
            String relevantPhones = getRelevantPhonesContext(question);

            // Create prompt with context
            String fullPrompt = createPromptWithContext(question, relevantPhones);

            // Call Gemini API
            String response = callGeminiAPI(fullPrompt);

            // Add response to history
            addToHistory("assistant", response);

            return response;

        } catch (Exception e) {
            System.err.println("❌ Error calling Gemini API: " + e.getMessage());
            return "Xin lỗi, tôi đang gặp sự cố kỹ thuật. Vui lòng thử lại sau ít phút.";
        }
    }

    /**
     * Test Gemini API connection
     */
    private boolean testGeminiConnection() {
        try {
            String testPrompt = "Trả lời ngắn gọn: Bạn có thể nói tiếng Việt không?";
            String response = callGeminiAPI(testPrompt);
            return response != null && !response.trim().isEmpty();
        } catch (Exception e) {
            System.err.println("❌ Test connection failed: " + e.getMessage());
            return false;
        }
    }

    /**
     * Call Gemini AI API
     */
    private String callGeminiAPI(String prompt) throws IOException {
        URL url = new URL(GEMINI_API_URL + "?key=" + apiKey);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        try {
            // Setup connection
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);
            conn.setConnectTimeout(30000); // 30 seconds
            conn.setReadTimeout(60000);    // 60 seconds

            // Create request body
            JSONObject requestBody = new JSONObject();
            JSONArray contents = new JSONArray();
            JSONObject content = new JSONObject();
            JSONArray parts = new JSONArray();
            JSONObject part = new JSONObject();

            part.put("text", prompt);
            parts.put(part);
            content.put("parts", parts);
            contents.put(content);
            requestBody.put("contents", contents);

            // Add generation config for better responses
            JSONObject generationConfig = new JSONObject();
            generationConfig.put("temperature", 0.7);
            generationConfig.put("topP", 0.8);
            generationConfig.put("topK", 40);
            generationConfig.put("maxOutputTokens", 2048);
            requestBody.put("generationConfig", generationConfig);

            // Send request
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = requestBody.toString().getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            // Read response
            int responseCode = conn.getResponseCode();
            Scanner scanner;

            if (responseCode == 200) {
                scanner = new Scanner(conn.getInputStream(), StandardCharsets.UTF_8);
            } else {
                scanner = new Scanner(conn.getErrorStream(), StandardCharsets.UTF_8);
                String errorResponse = scanner.useDelimiter("\\A").next();
                throw new IOException("API Error " + responseCode + ": " + errorResponse);
            }

            String response = scanner.useDelimiter("\\A").next();
            scanner.close();

            // Parse response
            JSONObject jsonResponse = new JSONObject(response);

            if (jsonResponse.has("candidates")) {
                JSONArray candidates = jsonResponse.getJSONArray("candidates");
                if (candidates.length() > 0) {
                    JSONObject candidate = candidates.getJSONObject(0);
                    if (candidate.has("content")) {
                        JSONObject content_response = candidate.getJSONObject("content");
                        if (content_response.has("parts")) {
                            JSONArray parts_response = content_response.getJSONArray("parts");
                            if (parts_response.length() > 0) {
                                JSONObject part_response = parts_response.getJSONObject(0);
                                if (part_response.has("text")) {
                                    return part_response.getString("text");
                                }
                            }
                        }
                    }
                }
            }

            throw new IOException("Unexpected response format from Gemini API");

        } finally {
            conn.disconnect();
        }
    }

    /**
     * Get relevant phones based on user question
     */
    private String getRelevantPhonesContext(String question) {
        String lowerQuestion = question.toLowerCase();
        List<Phone> relevantPhones = new ArrayList<>();

        // Extract price range
        PriceRange priceRange = extractPriceRange(lowerQuestion);

        // Extract brands
        List<String> brands = extractBrands(lowerQuestion);

        // Filter phones
        relevantPhones = allPhones.stream()
                .filter(phone -> {
                    if (priceRange != null) {
                        double price = phone.getPrice();
                        if (price < priceRange.min || price > priceRange.max) {
                            return false;
                        }
                    }

                    if (!brands.isEmpty()) {
                        String phoneName = phone.getName().toLowerCase();
                        boolean matchesBrand = brands.stream()
                                .anyMatch(brand -> phoneName.contains(brand.toLowerCase()));
                        if (!matchesBrand) {
                            return false;
                        }
                    }

                    return true;
                })
                .limit(10) // Limit to 10 phones for context
                .collect(Collectors.toList());

        // If no specific filters, get top phones by price range
        if (relevantPhones.isEmpty()) {
            relevantPhones = allPhones.stream()
                    .limit(10)
                    .collect(Collectors.toList());
        }

        // Format phone data for AI
        StringBuilder phoneContext = new StringBuilder();
        for (Phone phone : relevantPhones) {
            phoneContext.append("- ").append(phone.getName())
                    .append(" (Giá: ").append(String.format("%,.0f", phone.getPrice())).append(" VNĐ")
                    .append(", Chip: ").append(phone.getDescription().getChipset())
                    .append(", RAM: ").append(phone.getDescription().getRam())
                    .append(", Camera: ").append(phone.getDescription().getRearCamera())
                    .append(")\n");
        }

        return phoneContext.toString();
    }

    /**
     * Create prompt with context
     */
    private String createPromptWithContext(String userQuestion, String phoneData) {
        StringBuilder prompt = new StringBuilder();

        prompt.append("Bạn là một chuyên gia tư vấn điện thoại thông minh, thân thiện và am hiểu về công nghệ. ");
        prompt.append("Hãy trả lời câu hỏi của khách hàng một cách tự nhiên, chi tiết và hữu ích.\n\n");

        prompt.append("NGUYÊN TẮC TƯ VẤN:\n");
        prompt.append("- Luôn thân thiện, nhiệt tình\n");
        prompt.append("- Đưa ra lời khuyên cụ thể dựa trên dữ liệu\n");
        prompt.append("- Giải thích rõ ưu nhược điểm\n");
        prompt.append("- Hỏi thêm thông tin nếu cần\n");
        prompt.append("- Sử dụng emoji phù hợp\n\n");

        prompt.append("DỮ LIỆU ĐIỆN THOẠI HIỆN CÓ:\n");
        prompt.append(phoneData);
        prompt.append("\n");

        // Add conversation history for context
        if (!conversationHistory.isEmpty()) {
            prompt.append("LỊCH SỬ CUỘC TRÒ CHUYỆN:\n");
            for (ConversationMessage msg : conversationHistory) {
                prompt.append(msg.role).append(": ").append(msg.content).append("\n");
            }
            prompt.append("\n");
        }

        prompt.append("CÂU HỎI CỦA KHÁCH HÀNG: ").append(userQuestion).append("\n\n");
        prompt.append("HÃY TRẢ LỜI:");

        return prompt.toString();
    }

    /**
     * Extract price range from question
     */
    private PriceRange extractPriceRange(String question) {
        if (question.contains("dưới 5") || question.contains("< 5")) {
            return new PriceRange(0, 5_000_000);
        } else if (question.contains("5") && question.contains("10")) {
            return new PriceRange(5_000_000, 10_000_000);
        } else if (question.contains("10") && question.contains("15")) {
            return new PriceRange(10_000_000, 15_000_000);
        } else if (question.contains("15") && question.contains("20")) {
            return new PriceRange(15_000_000, 20_000_000);
        } else if (question.contains("trên 20") || question.contains("> 20")) {
            return new PriceRange(20_000_000, Double.MAX_VALUE);
        } else if (question.contains("dưới 10")) {
            return new PriceRange(0, 10_000_000);
        } else if (question.contains("dưới 15")) {
            return new PriceRange(0, 15_000_000);
        } else if (question.contains("dưới 20")) {
            return new PriceRange(0, 20_000_000);
        }
        return null;
    }

    /**
     * Extract brands from question
     */
    private List<String> extractBrands(String question) {
        List<String> brands = new ArrayList<>();
        if (question.contains("iphone") || question.contains("apple")) brands.add("Apple");
        if (question.contains("samsung") || question.contains("galaxy")) brands.add("Samsung");
        if (question.contains("xiaomi") || question.contains("redmi")) brands.add("Xiaomi");
        if (question.contains("oppo")) brands.add("Oppo");
        if (question.contains("vivo")) brands.add("Vivo");
        if (question.contains("huawei")) brands.add("Huawei");
        if (question.contains("realme")) brands.add("Realme");
        return brands;
    }

    /**
     * Add message to conversation history
     */
    private void addToHistory(String role, String content) {
        conversationHistory.add(new ConversationMessage(role, content));

        // Keep only recent messages
        if (conversationHistory.size() > MAX_HISTORY_SIZE) {
            conversationHistory.remove(0);
        }
    }

    @Override
    public boolean updateData() {
        return initialize();
    }

    @Override
    public boolean isReady() {
        return isReady;
    }

    @Override
    public List<String> getSuggestedQuestions() {
        return Arrays.asList(
                "Xin chào! Tôi cần tư vấn điện thoại",
                "Điện thoại nào pin trâu nhất dưới 15 triệu?",
                "Tư vấn điện thoại chụp ảnh đẹp khoảng 20 triệu",
                "So sánh iPhone và Samsung Galaxy",
                "Điện thoại gaming tốt nhất trong tầm giá",
                "Điện thoại giá rẻ dưới 10 triệu có gì hay?",
                "Nên mua điện thoại hãng nào trong năm 2025?",
                "Tư vấn điện thoại cho học sinh sinh viên"
        );
    }

    // Inner classes
    private static class ConversationMessage {
        String role;
        String content;

        ConversationMessage(String role, String content) {
            this.role = role;
            this.content = content;
        }
    }

    private static class PriceRange {
        final double min, max;

        PriceRange(double min, double max) {
            this.min = min;
            this.max = max;
        }
    }
}