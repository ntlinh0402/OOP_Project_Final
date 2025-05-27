package com.phonerecommend.test;

import org.example.util.SimpleJSON;
import java.util.List;
import java.util.Map;

/**
 * Test SimpleJSON parser để xem có parse được description không
 */
public class SimpleJSONTest {

    public static void main(String[] args) {
        // Test với chuỗi JSON đơn giản
        String simpleJson = "{\"name\":\"Test Phone\",\"price\":1000000,\"description\":{\"Camera sau\":\"48MP\",\"Chipset\":\"A18 Pro\"}}";

        System.out.println("=== TEST SIMPLE JSON ===");
        System.out.println("Input: " + simpleJson);

        // Parse object
        Map<String, Object> parsed = SimpleJSON.parseJSONObject(simpleJson);
        System.out.println("Parsed object keys: " + parsed.keySet());

        // Kiểm tra description
        Object desc = parsed.get("description");
        System.out.println("Description type: " + (desc != null ? desc.getClass().getSimpleName() : "NULL"));

        if (desc instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> descMap = (Map<String, Object>) desc;
            System.out.println("Description keys: " + descMap.keySet());
            System.out.println("Camera sau: " + descMap.get("Camera sau"));
        }

        // Test với array
        String arrayJson = "[" + simpleJson + "]";
        System.out.println("\n=== TEST JSON ARRAY ===");

        List<Object> array = SimpleJSON.parseJSONArray(arrayJson);
        System.out.println("Array size: " + array.size());

        if (!array.isEmpty() && array.get(0) instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> firstPhone = (Map<String, Object>) array.get(0);
            System.out.println("First phone keys: " + firstPhone.keySet());

            Object firstDesc = firstPhone.get("description");
            if (firstDesc instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> firstDescMap = (Map<String, Object>) firstDesc;
                System.out.println("First phone description keys: " + firstDescMap.keySet());
            }
        }

        // Test với dữ liệu thực từ file
        System.out.println("\n=== TEST WITH REAL FILE CONTENT ===");
        String realJsonPart = "{\n" +
                "  \"name\" : \"iPhone 16 Pro Max 256GB\",\n" +
                "  \"link\" : \"https://cellphones.com.vn/iphone-16-pro-max.html\",\n" +
                "  \"price\" : 30490000,\n" +
                "  \"description\" : {\n" +
                "    \"Kích thước màn hình\" : \"6.9 inches\",\n" +
                "    \"Camera sau\" : \"Camera chính: 48MP, f/1.78\",\n" +
                "    \"Chipset\" : \"Apple A18 Pro\"\n" +
                "  }\n" +
                "}";

        Map<String, Object> realParsed = SimpleJSON.parseJSONObject(realJsonPart);
        System.out.println("Real parsed keys: " + realParsed.keySet());

        Object realDesc = realParsed.get("description");
        System.out.println("Real description type: " + (realDesc != null ? realDesc.getClass().getSimpleName() : "NULL"));

        if (realDesc instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> realDescMap = (Map<String, Object>) realDesc;
            System.out.println("Real description size: " + realDescMap.size());
            System.out.println("Real description keys: " + realDescMap.keySet());
            for (Map.Entry<String, Object> entry : realDescMap.entrySet()) {
                System.out.println("  '" + entry.getKey() + "' = '" + entry.getValue() + "'");
            }
        }
    }
}