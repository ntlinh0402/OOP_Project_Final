package com.phonerecommend.service.chatbot;

import java.util.List;

/**
 * Interface định nghĩa dịch vụ chatbot
 */
public interface ChatbotService {

    /**
     * Khởi tạo dữ liệu cho chatbot
     * @return true nếu khởi tạo thành công, false nếu thất bại
     */
    boolean initialize();

    /**
     * Xử lý câu hỏi và trả lời
     * @param question Câu hỏi của người dùng
     * @return Câu trả lời của chatbot
     */
    String processQuestion(String question);

    /**
     * Cập nhật dữ liệu cho chatbot
     * @return true nếu cập nhật thành công, false nếu thất bại
     */
    boolean updateData();

    /**
     * Kiểm tra xem chatbot đã sẵn sàng chưa
     * @return true nếu đã sẵn sàng, false nếu chưa
     */
    boolean isReady();

    /**
     * Lấy danh sách câu hỏi gợi ý
     * @return Danh sách câu hỏi gợi ý
     */
    List<String> getSuggestedQuestions();
}