package com.phonerecommend.service.chatbot;

import com.phonerecommend.model.Phone;
import java.util.Map;

/**
 * Lớp chứa thông tin nhúng (embedding) của dữ liệu điện thoại
 * để sử dụng trong RAG chatbot
 */
public class PhoneDataEmbedding {
    // ID của tài liệu, thường là link của điện thoại
    private String documentId;

    // Nội dung tài liệu
    private String documentContent;

    // Vector nhúng của tài liệu
    private float[] embedding;

    // Các thông tin bổ sung
    private Map<String, Object> metadata;

    // Điện thoại liên kết
    private Phone phone;

    /**
     * Constructor mặc định
     */
    public PhoneDataEmbedding() {
    }

    /**
     * Constructor với thông tin cơ bản
     * @param documentId ID tài liệu
     * @param documentContent Nội dung tài liệu
     * @param embedding Vector nhúng
     */
    public PhoneDataEmbedding(String documentId, String documentContent, float[] embedding) {
        this.documentId = documentId;
        this.documentContent = documentContent;
        this.embedding = embedding;
    }

    /**
     * Constructor đầy đủ
     * @param documentId ID tài liệu
     * @param documentContent Nội dung tài liệu
     * @param embedding Vector nhúng
     * @param metadata Metadata
     * @param phone Điện thoại liên kết
     */
    public PhoneDataEmbedding(String documentId, String documentContent, float[] embedding,
                              Map<String, Object> metadata, Phone phone) {
        this.documentId = documentId;
        this.documentContent = documentContent;
        this.embedding = embedding;
        this.metadata = metadata;
        this.phone = phone;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getDocumentContent() {
        return documentContent;
    }

    public void setDocumentContent(String documentContent) {
        this.documentContent = documentContent;
    }

    public float[] getEmbedding() {
        return embedding;
    }

    public void setEmbedding(float[] embedding) {
        this.embedding = embedding;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }

    public Phone getPhone() {
        return phone;
    }

    public void setPhone(Phone phone) {
        this.phone = phone;
    }

    /**
     * Tính độ tương đồng cosine với vector khác
     * @param otherEmbedding Vector nhúng khác
     * @return Độ tương đồng (0-1)
     */
    public double cosineSimilarity(float[] otherEmbedding) {
        if (this.embedding == null || otherEmbedding == null) {
            return 0.0;
        }

        if (this.embedding.length != otherEmbedding.length) {
            return 0.0;
        }

        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;

        for (int i = 0; i < this.embedding.length; i++) {
            dotProduct += this.embedding[i] * otherEmbedding[i];
            normA += Math.pow(this.embedding[i], 2);
            normB += Math.pow(otherEmbedding[i], 2);
        }

        if (normA == 0 || normB == 0) {
            return 0.0;
        }

        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }
}