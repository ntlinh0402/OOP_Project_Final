package com.phonerecommend.controller;

import com.phonerecommend.repository.PhoneRepository;
import com.phonerecommend.repository.RepositoryFactory;
import com.phonerecommend.service.chatbot.ChatbotService;
import com.phonerecommend.service.chatbot.DirectGeminiChatbotImpl;
import com.phonerecommend.service.chatbot.RAGChatbotImpl;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Controller xử lý chatbot với switching giữa AI engines
 */
public class ChatbotController {
    private final ChatbotService chatbotService;
    private final boolean useGeminiAPI;

    /**
     * Constructor mặc định - sử dụng Gemini API
     */
    public ChatbotController() {
        this(false); // false = sử dụng Gemini API
    }

    /**
     * Constructor với lựa chọn loại chatbot
     * @param useLocalRAG true để sử dụng Local RAG, false để sử dụng Gemini API
     */
    public ChatbotController(boolean useLocalRAG) {
        PhoneRepository phoneRepository = RepositoryFactory.getPhoneRepository();
        this.useGeminiAPI = !useLocalRAG;

        if (useLocalRAG) {
            System.out.println("🤖 Khởi tạo Local RAG Chatbot...");
            this.chatbotService = new RAGChatbotImpl(phoneRepository);
        } else {
            System.out.println("🤖 Khởi tạo Gemini API Chatbot...");
            this.chatbotService = new DirectGeminiChatbotImpl(phoneRepository);
        }

        initializeChatbot();
    }

    /**
     * Khởi tạo chatbot
     */
    private void initializeChatbot() {
        if (!chatbotService.isReady()) {
            System.out.println("🔄 Đang khởi tạo chatbot...");
            boolean success = chatbotService.initialize();

            if (success) {
                System.out.println("✅ Chatbot đã sẵn sàng!");
            } else {
                System.err.println("❌ Lỗi khởi tạo chatbot!");

                if (useGeminiAPI) {
                    System.err.println("💡 Kiểm tra lại Gemini API key trong DirectGeminiChatbotImpl.java");
                    System.err.println("🌐 Lấy API key tại: https://aistudio.google.com/");
                }
            }
        }
    }

    /**
     * Xử lý câu hỏi từ người dùng
     * @param question Câu hỏi
     * @return Câu trả lời
     */
    public String processQuestion(String question) {
        // Đảm bảo chatbot đã được khởi tạo
        if (!chatbotService.isReady()) {
            initializeChatbot();
        }

        // Xử lý câu hỏi
        return chatbotService.processQuestion(question);
    }

    /**
     * Xử lý câu hỏi bất đồng bộ (không block UI)
     * @param question Câu hỏi
     * @return CompletableFuture chứa câu trả lời
     */
    public CompletableFuture<String> processQuestionAsync(String question) {
        return CompletableFuture.supplyAsync(() -> processQuestion(question));
    }

    /**
     * Lấy danh sách câu hỏi gợi ý
     * @return Danh sách câu hỏi gợi ý
     */
    public List<String> getSuggestedQuestions() {
        return chatbotService.getSuggestedQuestions();
    }

    /**
     * Cập nhật dữ liệu cho chatbot
     * @return true nếu cập nhật thành công, false nếu thất bại
     */
    public boolean updateChatbotData() {
        return chatbotService.updateData();
    }

    /**
     * Kiểm tra xem chatbot đã sẵn sàng chưa
     * @return true nếu đã sẵn sàng, false nếu chưa
     */
    public boolean isChatbotReady() {
        return chatbotService.isReady();
    }

    /**
     * Kiểm tra kết nối API
     * @return true nếu kết nối OK, false nếu không
     */
    public boolean testAPIConnection() {
        if (useGeminiAPI && chatbotService instanceof DirectGeminiChatbotImpl) {
            return ((DirectGeminiChatbotImpl) chatbotService).testAPIConnection();
        }
        return chatbotService.isReady();
    }

    /**
     * Test nhanh chatbot với câu hỏi đơn giản
     * @return Kết quả test
     */
    public String quickTest() {
        if (!isChatbotReady()) {
            return "❌ Chatbot chưa sẵn sàng";
        }

        try {
            String testQuestion = "Xin chào";
            String response = processQuestion(testQuestion);
            return "✅ Test thành công: " + response.substring(0, Math.min(100, response.length())) + "...";
        } catch (Exception e) {
            return "❌ Test thất bại: " + e.getMessage();
        }
    }

    /**
     * Lấy thông tin về chatbot
     * @return Thông tin chatbot
     */
    public String getChatbotInfo() {
        if (chatbotService instanceof DirectGeminiChatbotImpl) {
            DirectGeminiChatbotImpl gemini = (DirectGeminiChatbotImpl) chatbotService;
            return "Direct Gemini API Chatbot - " + gemini.getAPIStatus();
        } else if (chatbotService instanceof RAGChatbotImpl) {
            return "Local RAG Chatbot - ✅ Hoạt động";
        } else {
            return "Unknown Chatbot Type";
        }
    }
}