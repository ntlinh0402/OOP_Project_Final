package com.phonerecommend.config;

/**
 * Configuration class cho API keys và settings
 */
public class ApiConfig {

    // ==> ĐẶT API KEY CỦA BẠN TẠI ĐÂY <==
    public static final String GEMINI_API_KEY = "Your_API_Key";

    // Các cấu hình khác
    public static final String GEMINI_MODEL = "gemini-2.0-flash";
    public static final int CHAT_TIMEOUT = 30000; // 30 seconds
    public static final int MAX_RETRIES = 3;

    /**
     * Load API key từ nhiều nguồn (ưu tiên theo thứ tự)
     */
    public static String getGeminiApiKey() {
        // 1. Ưu tiên Environment Variable (production)
        String envKey = System.getenv("GEMINI_API_KEY");
        if (envKey != null && !envKey.trim().isEmpty()) {
            return envKey;
        }

        // 2. System Property (-DGEMINI_API_KEY=xxx)
        String propKey = System.getProperty("gemini.api.key");
        if (propKey != null && !propKey.trim().isEmpty()) {
            return propKey;
        }

        // 3. Fallback to hardcoded (development)
        if (!GEMINI_API_KEY.startsWith("AIza")) {
            System.err.println("⚠️ WARNING: Please set your real Gemini API key!");
            System.err.println("💡 Get it from: https://makersuite.google.com/app/apikey");
        }

        return GEMINI_API_KEY;
    }

    /**
     * Validate API key format
     */
    public static boolean isValidApiKey(String apiKey) {
        return apiKey != null &&
                apiKey.startsWith("AIza") &&
                apiKey.length() > 20;
    }

    /**
     * Check if API key is configured
     */
    public static boolean hasValidApiKey() {
        String key = getGeminiApiKey();
        return isValidApiKey(key);
    }
}