package com.phonerecommend.service.ai;

import com.phonerecommend.repository.PhoneRepository;
import com.phonerecommend.service.chatbot.ChatbotService;
import com.phonerecommend.service.chatbot.DirectGeminiChatbotImpl;
import com.phonerecommend.service.chatbot.RAGChatbotImpl;

/**
 * Factory để tạo AI Service, tuân thủ nguyên tắc Factory Method Pattern
 * Tương tự RepositoryFactory
 */
public class AIServiceFactory {

    public enum AIServiceType {
        GEMINI_API,
        LOCAL_RAG,
        OLLAMA_LOCAL,
        OPENAI_API
    }

    // Cấu hình mặc định
    private static AIServiceType currentType = AIServiceType.GEMINI_API;
    private static String geminiApiKey = "YOUR_GEMINI_API_KEY_HERE";
    private static String openaiApiKey = "";
    private static String ollamaBaseUrl = "http://localhost:11434";
    private static String ollamaModel = "llama3.2";

    private static ChatbotService instance;

    /**
     * Lấy instance của ChatbotService
     * @return ChatbotService instance
     */
    public static ChatbotService getChatbotService(PhoneRepository phoneRepository) {
        if (instance == null) {
            createService(phoneRepository);
        }
        return instance;
    }

    /**
     * Thiết lập loại AI service
     * @param type Loại AI service
     */
    public static void setAIServiceType(AIServiceType type) {
        if (currentType != type) {
            currentType = type;
            instance = null; // Reset để tạo mới service
        }
    }

    /**
     * Thiết lập Gemini API Key
     * @param apiKey Gemini API Key
     */
    public static void setGeminiApiKey(String apiKey) {
        geminiApiKey = apiKey;
        if (currentType == AIServiceType.GEMINI_API) {
            instance = null; // Reset nếu đang sử dụng Gemini
        }
    }

    /**
     * Thiết lập OpenAI API Key
     * @param apiKey OpenAI API Key
     */
    public static void setOpenAIApiKey(String apiKey) {
        openaiApiKey = apiKey;
        if (currentType == AIServiceType.OPENAI_API) {
            instance = null; // Reset nếu đang sử dụng OpenAI
        }
    }

    /**
     * Thiết lập Ollama configuration
     * @param baseUrl Ollama base URL
     * @param model Model name
     */
    public static void setOllamaConfig(String baseUrl, String model) {
        ollamaBaseUrl = baseUrl;
        ollamaModel = model;
        if (currentType == AIServiceType.OLLAMA_LOCAL) {
            instance = null; // Reset nếu đang sử dụng Ollama
        }
    }

    /**
     * Tạo service dựa trên loại đã thiết lập
     */
    private static void createService(PhoneRepository phoneRepository) {
        switch (currentType) {
            case GEMINI_API:
                System.out.println("Khởi tạo Gemini API Service");
                instance = new DirectGeminiChatbotImpl(phoneRepository);
                // Set API key vào DirectGeminiChatbotImpl
                if (instance instanceof DirectGeminiChatbotImpl) {
                    ((DirectGeminiChatbotImpl) instance).setApiKey(geminiApiKey);
                }
                break;

            case LOCAL_RAG:
                System.out.println("Khởi tạo Local RAG Service");
                instance = new RAGChatbotImpl(phoneRepository);
                break;





            default:
                System.err.println("Loại AI service không được hỗ trợ: " + currentType);
                System.out.println("Sử dụng Local RAG Service mặc định");
                currentType = AIServiceType.LOCAL_RAG;
                instance = new RAGChatbotImpl(phoneRepository);
                break;
        }
    }

    /**
     * Lấy loại service hiện tại
     * @return AIServiceType hiện tại
     */
    public static AIServiceType getCurrentType() {
        return currentType;
    }

    /**
     * Reset factory (để testing hoặc reconfigure)
     */
    public static void reset() {
        instance = null;
    }

    /**
     * Lấy thông tin cấu hình hiện tại
     * @return Chuỗi thông tin cấu hình
     */
    public static String getConfigurationInfo() {
        StringBuilder info = new StringBuilder();
        info.append("AI Service Type: ").append(currentType).append("\n");

        switch (currentType) {
            case GEMINI_API:
                info.append("Gemini API Key: ").append(geminiApiKey.isEmpty() ? "Not configured" : "Configured");
                break;
            case LOCAL_RAG:
                info.append("Local RAG: Ready");
                break;
            case OLLAMA_LOCAL:
                info.append("Ollama URL: ").append(ollamaBaseUrl).append("\n");
                info.append("Model: ").append(ollamaModel);
                break;
            case OPENAI_API:
                info.append("OpenAI API Key: ").append(openaiApiKey.isEmpty() ? "Not configured" : "Configured");
                break;
        }

        return info.toString();
    }

    /**
     * Kiểm tra xem service đã được khởi tạo chưa
     * @return true nếu đã khởi tạo, false nếu chưa
     */
    public static boolean isInitialized() {
        return instance != null;
    }

    /**
     * Kiểm tra service có sẵn sàng không
     * @return true nếu service sẵn sàng
     */
    public static boolean isServiceReady() {
        switch (currentType) {
            case GEMINI_API:
                return !geminiApiKey.isEmpty() && !geminiApiKey.equals("YOUR_GEMINI_API_KEY_HERE");
            case LOCAL_RAG:
                return true; // Luôn sẵn sàng
            case OLLAMA_LOCAL:
                return isOllamaRunning();
            case OPENAI_API:
                return !openaiApiKey.isEmpty();
            default:
                return false;
        }
    }

    private static boolean isOllamaRunning() {
        try {
            java.net.http.HttpClient client = java.net.http.HttpClient.newHttpClient();
            java.net.http.HttpRequest request = java.net.http.HttpRequest.newBuilder()
                    .uri(java.net.URI.create(ollamaBaseUrl + "/api/tags"))
                    .timeout(java.time.Duration.ofSeconds(5))
                    .GET()
                    .build();

            java.net.http.HttpResponse<String> response = client.send(request,
                    java.net.http.HttpResponse.BodyHandlers.ofString());

            return response.statusCode() == 200;
        } catch (Exception e) {
            return false;
        }
    }
}