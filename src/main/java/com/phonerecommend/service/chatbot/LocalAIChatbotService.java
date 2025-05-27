package com.phonerecommend.service.chatbot;

import com.phonerecommend.model.Phone;
import com.phonerecommend.repository.PhoneRepository;
import com.phonerecommend.service.chatbot.ChatbotService;

import java.util.*;
import java.util.stream.Collectors;
import java.util.regex.Pattern;

/**
 * Local AI Chatbot Service - Không cần kết nối external
 * Sử dụng logic thông minh để tư vấn dựa trên dữ liệu local
 */
public class LocalAIChatbotService implements ChatbotService {

    private final PhoneRepository phoneRepository;
    private List<Phone> allPhones;
    private boolean isReady = false;

    // Conversation context để nhớ cuộc trò chuyện
    private Map<String, String> conversationContext = new HashMap<>();

    public LocalAIChatbotService(PhoneRepository phoneRepository) {
        this.phoneRepository = phoneRepository;
    }

    @Override
    public boolean initialize() {
        try {
            System.out.println("🤖 Khởi tạo Local AI Chatbot...");
            allPhones = phoneRepository.getAllPhones();

            if (allPhones.isEmpty()) {
                System.err.println("❌ Không có dữ liệu điện thoại!");
                return false;
            }

            System.out.println("✅ Đã load " + allPhones.size() + " điện thoại");
            System.out.println("🧠 Local AI ready với intelligent matching");

            isReady = true;
            return true;

        } catch (Exception e) {
            System.err.println("❌ Lỗi khởi tạo Local AI: " + e.getMessage());
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
            // Normalize question
            String normalizedQuestion = question.toLowerCase().trim();

            // Detect intent và extract information
            Intent intent = detectIntent(normalizedQuestion);
            PriceRange priceRange = extractPriceRange(normalizedQuestion);
            List<String> brands = extractBrands(normalizedQuestion);
            List<String> features = extractFeatures(normalizedQuestion);

            // Generate intelligent response
            return generateSmartResponse(intent, priceRange, brands, features, normalizedQuestion);

        } catch (Exception e) {
            System.err.println("❌ Error processing question: " + e.getMessage());
            return "Xin lỗi, đã xảy ra lỗi. Vui lòng thử lại với câu hỏi khác.";
        }
    }

    /**
     * Detect user intent from question
     */
    private Intent detectIntent(String question) {
        if (question.contains("xin chào") || question.contains("hello") || question.contains("hi")) {
            return Intent.GREETING;
        } else if (question.contains("so sánh") || question.contains("khác nhau") || question.contains("vs")) {
            return Intent.COMPARE;
        } else if (question.contains("pin") && (question.contains("trâu") || question.contains("lâu") || question.contains("khỏe"))) {
            return Intent.BATTERY;
        } else if (question.contains("camera") || question.contains("chụp ảnh") || question.contains("quay phim")) {
            return Intent.CAMERA;
        } else if (question.contains("game") || question.contains("gaming") || question.contains("chơi game")) {
            return Intent.GAMING;
        } else if (question.contains("giá rẻ") || question.contains("tiết kiệm") || question.contains("rẻ")) {
            return Intent.BUDGET;
        } else if (question.contains("cao cấp") || question.contains("flagship") || question.contains("đắt tiền")) {
            return Intent.PREMIUM;
        } else if (question.contains("5g") || question.contains("mạng")) {
            return Intent.NETWORK;
        } else if (question.contains("tư vấn") || question.contains("gợi ý") || question.contains("nên mua")) {
            return Intent.RECOMMEND;
        } else {
            return Intent.GENERAL;
        }
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
     * Extract features from question
     */
    private List<String> extractFeatures(String question) {
        List<String> features = new ArrayList<>();
        if (question.contains("sạc nhanh")) features.add("fast_charging");
        if (question.contains("kháng nước")) features.add("waterproof");
        if (question.contains("vân tay")) features.add("fingerprint");
        if (question.contains("face id") || question.contains("nhận diện khuôn mặt")) features.add("face_recognition");
        if (question.contains("wireless") || question.contains("không dây")) features.add("wireless_charging");
        return features;
    }

    /**
     * Generate smart response based on detected intent
     */
    private String generateSmartResponse(Intent intent, PriceRange priceRange,
                                         List<String> brands, List<String> features, String originalQuestion) {

        switch (intent) {
            case GREETING:
                return generateGreetingResponse();

            case COMPARE:
                return generateCompareResponse(brands, priceRange);

            case BATTERY:
                return generateBatteryResponse(priceRange, brands);

            case CAMERA:
                return generateCameraResponse(priceRange, brands);

            case GAMING:
                return generateGamingResponse(priceRange, brands);

            case BUDGET:
                return generateBudgetResponse(priceRange);

            case PREMIUM:
                return generatePremiumResponse();

            case NETWORK:
                return generateNetworkResponse(priceRange);

            case RECOMMEND:
                return generateRecommendResponse(priceRange, brands, features);

            default:
                return generateGeneralResponse(originalQuestion, priceRange, brands);
        }
    }

    private String generateGreetingResponse() {
        return "Xin chào! 👋 Tôi là trợ lý tư vấn điện thoại thông minh.\n\n" +
                "🤖 Tôi có thể hỗ trợ bạn:\n" +
                "• Tư vấn điện thoại theo ngân sách\n" +
                "• So sánh các dòng máy\n" +
                "• Tìm điện thoại theo nhu cầu (camera, gaming, pin...)\n" +
                "• Đánh giá ưu nhược điểm\n\n" +
                "💡 Hãy cho tôi biết bạn cần tư vấn gì nhé!";
    }

    private String generateBatteryResponse(PriceRange priceRange, List<String> brands) {
        List<Phone> phones = filterPhones(priceRange, brands, null);

        // Sort by battery (extract from description)
        List<Phone> batteryPhones = phones.stream()
                .filter(p -> {
                    String battery = p.getDescription().getAttribute("Pin");
                    return battery != null && !battery.isEmpty();
                })
                .sorted((p1, p2) -> {
                    int battery1 = extractBatteryCapacity(p1.getDescription().getAttribute("Pin"));
                    int battery2 = extractBatteryCapacity(p2.getDescription().getAttribute("Pin"));
                    return Integer.compare(battery2, battery1);
                })
                .limit(3)
                .collect(Collectors.toList());

        if (batteryPhones.isEmpty()) {
            return "Hiện tại không tìm thấy điện thoại phù hợp với yêu cầu pin của bạn. " +
                    "Bạn có thể cho tôi biết ngân sách cụ thể để tư vấn chính xác hơn?";
        }

        StringBuilder response = new StringBuilder("🔋 **Điện thoại pin trâu nhất** theo yêu cầu của bạn:\n\n");

        for (int i = 0; i < batteryPhones.size(); i++) {
            Phone phone = batteryPhones.get(i);
            response.append(String.format("**%d. %s**\n", i + 1, phone.getName()));
            response.append(String.format("   💰 Giá: %,.0f VNĐ\n", phone.getPrice()));
            response.append(String.format("   🔋 Pin: %s\n", phone.getDescription().getAttribute("Pin")));
            response.append(String.format("   📱 Chip: %s\n", phone.getDescription().getChipset()));
            response.append("\n");
        }

        response.append("✨ **Lời khuyên**: Để pin bền lâu, nên chọn điện thoại có dung lượng pin ≥ 4000mAh và chip tiết kiệm điện.\n\n");
        response.append("❓ Bạn có câu hỏi gì khác về các mẫu điện thoại này không?");

        return response.toString();
    }

    private String generateCameraResponse(PriceRange priceRange, List<String> brands) {
        List<Phone> phones = filterPhones(priceRange, brands, null);

        // Sort by camera quality (simple heuristic)
        List<Phone> cameraPhones = phones.stream()
                .filter(p -> {
                    String camera = p.getDescription().getRearCamera();
                    return camera != null && !camera.isEmpty();
                })
                .sorted((p1, p2) -> {
                    int score1 = calculateCameraScore(p1);
                    int score2 = calculateCameraScore(p2);
                    return Integer.compare(score2, score1);
                })
                .limit(3)
                .collect(Collectors.toList());

        if (cameraPhones.isEmpty()) {
            return "Hiện tại không tìm thấy thông tin camera chi tiết. " +
                    "Bạn có thể cho tôi biết ngân sách và mục đích chụp ảnh để tư vấn phù hợp?";
        }

        StringBuilder response = new StringBuilder("📸 **Điện thoại camera tốt nhất** cho bạn:\n\n");

        for (int i = 0; i < cameraPhones.size(); i++) {
            Phone phone = cameraPhones.get(i);
            response.append(String.format("**%d. %s**\n", i + 1, phone.getName()));
            response.append(String.format("   💰 Giá: %,.0f VNĐ\n", phone.getPrice()));
            response.append(String.format("   📷 Camera sau: %s\n", phone.getDescription().getRearCamera()));
            response.append(String.format("   🤳 Camera trước: %s\n", phone.getDescription().getFrontCamera()));

            String cameraFeatures = phone.getDescription().getCameraFeatures();
            if (cameraFeatures != null && !cameraFeatures.isEmpty()) {
                response.append(String.format("   ✨ Tính năng: %s\n", cameraFeatures));
            }
            response.append("\n");
        }

        response.append("💡 **Gợi ý**: \n");
        response.append("• iPhone: Màu sắc tự nhiên, video ổn định\n");
        response.append("• Samsung: Zoom tốt, chụp đêm\n");
        response.append("• Xiaomi: Camera Leica chất lượng cao\n\n");
        response.append("🤔 Bạn chủ yếu chụp gì? Selfie, phong cảnh hay chụp đêm?");

        return response.toString();
    }

    private String generateGamingResponse(PriceRange priceRange, List<String> brands) {
        List<Phone> phones = filterPhones(priceRange, brands, null);

        // Sort by gaming performance (RAM + Chip)
        List<Phone> gamingPhones = phones.stream()
                .sorted((p1, p2) -> {
                    int score1 = calculateGamingScore(p1);
                    int score2 = calculateGamingScore(p2);
                    return Integer.compare(score2, score1);
                })
                .limit(3)
                .collect(Collectors.toList());

        StringBuilder response = new StringBuilder("🎮 **Điện thoại gaming tốt nhất** trong tầm giá:\n\n");

        for (int i = 0; i < gamingPhones.size(); i++) {
            Phone phone = gamingPhones.get(i);
            response.append(String.format("**%d. %s**\n", i + 1, phone.getName()));
            response.append(String.format("   💰 Giá: %,.0f VNĐ\n", phone.getPrice()));
            response.append(String.format("   🧠 Chip: %s\n", phone.getDescription().getChipset()));
            response.append(String.format("   💾 RAM: %s\n", phone.getDescription().getRam()));
            response.append(String.format("   📱 Màn hình: %s\n", phone.getDescription().getScreenSize()));
            response.append("\n");
        }

        response.append("🚀 **Tips gaming**: \n");
        response.append("• RAM ≥ 8GB cho game mượt\n");
        response.append("• Màn hình 120Hz cho trải nghiệm tốt\n");
        response.append("• Pin lớn cho gaming lâu\n\n");
        response.append("🎯 Bạn chơi game gì chủ yếu? PUBG, Liên Quân hay game nặng khác?");

        return response.toString();
    }

    private String generateBudgetResponse(PriceRange priceRange) {
        PriceRange budgetRange = priceRange != null ? priceRange : new PriceRange(0, 10_000_000);
        List<Phone> phones = filterPhones(budgetRange, null, null);

        List<Phone> budgetPhones = phones.stream()
                .sorted(Comparator.comparingDouble(Phone::getPrice))
                .limit(5)
                .collect(Collectors.toList());

        StringBuilder response = new StringBuilder("💰 **Điện thoại giá tốt** cho bạn:\n\n");

        for (int i = 0; i < budgetPhones.size(); i++) {
            Phone phone = budgetPhones.get(i);
            response.append(String.format("**%d. %s**\n", i + 1, phone.getName()));
            response.append(String.format("   💰 Giá: %,.0f VNĐ\n", phone.getPrice()));
            response.append(String.format("   ⭐ Highlights: %s, %s\n",
                    phone.getDescription().getChipset(),
                    phone.getDescription().getRam()));
            response.append("\n");
        }

        response.append("💡 **Lưu ý**: Điện thoại giá rẻ vẫn có thể đáp ứng tốt nhu cầu cơ bản như gọi điện, nhắn tin, mạng xã hội.\n\n");
        response.append("🤷‍♂️ Bạn có nhu cầu đặc biệt nào khác không?");

        return response.toString();
    }

    private String generateCompareResponse(List<String> brands, PriceRange priceRange) {
        if (brands.size() < 2) {
            return "Để so sánh, bạn hãy cho tôi biết 2 hãng hoặc 2 dòng điện thoại cụ thể nhé!\n\n" +
                    "Ví dụ: \"So sánh iPhone và Samsung\" hoặc \"So sánh Galaxy S25 và iPhone 16\"";
        }

        StringBuilder response = new StringBuilder("⚖️ **So sánh giữa " + String.join(" và ", brands) + "**:\n\n");

        for (String brand : brands) {
            List<Phone> brandPhones = filterPhones(priceRange, Arrays.asList(brand), null)
                    .stream().limit(2).collect(Collectors.toList());

            if (!brandPhones.isEmpty()) {
                Phone representative = brandPhones.get(0);
                response.append(String.format("🏷️ **%s** (VD: %s)\n", brand, representative.getName()));
                response.append(String.format("   💰 Giá: %,.0f VNĐ\n", representative.getPrice()));
                response.append(String.format("   📱 Chip: %s\n", representative.getDescription().getChipset()));
                response.append(String.format("   📷 Camera: %s\n", representative.getDescription().getRearCamera()));
                response.append("\n");
            }
        }

        response.append("🎯 **Kết luận**: Mỗi hãng có ưu điểm riêng. Bạn ưu tiên tính năng nào nhất để tôi tư vấn cụ thể?");

        return response.toString();
    }

    private String generateGeneralResponse(String question, PriceRange priceRange, List<String> brands) {
        return "🤖 Tôi hiểu bạn đang tìm hiểu về điện thoại.\n\n" +
                "💡 Để tư vấn chính xác nhất, bạn có thể cho tôi biết:\n" +
                "• 💰 Ngân sách dự kiến (VD: dưới 15 triệu)\n" +
                "• 🎯 Mục đích chính (gaming, camera, công việc...)\n" +
                "• 🏷️ Thương hiệu yêu thích (nếu có)\n" +
                "• ⭐ Tính năng quan trọng (pin, camera, màn hình...)\n\n" +
                "📱 Hiện tôi có thông tin về " + allPhones.size() + " điện thoại để tư vấn cho bạn!\n\n" +
                "❓ Bạn muốn tôi tư vấn về điều gì cụ thể?";
    }

    // Helper methods
    private List<Phone> filterPhones(PriceRange priceRange, List<String> brands, List<String> features) {
        return allPhones.stream()
                .filter(phone -> {
                    // Filter by price
                    if (priceRange != null) {
                        double price = phone.getPrice();
                        if (price < priceRange.min || price > priceRange.max) {
                            return false;
                        }
                    }

                    // Filter by brands
                    if (brands != null && !brands.isEmpty()) {
                        String phoneName = phone.getName().toLowerCase();
                        boolean matchesBrand = brands.stream()
                                .anyMatch(brand -> phoneName.contains(brand.toLowerCase()));
                        if (!matchesBrand) {
                            return false;
                        }
                    }

                    return true;
                })
                .collect(Collectors.toList());
    }

    private int extractBatteryCapacity(String batteryInfo) {
        if (batteryInfo == null) return 0;
        try {
            String numericPart = batteryInfo.replaceAll("[^0-9]", "");
            return numericPart.isEmpty() ? 0 : Integer.parseInt(numericPart);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private int calculateCameraScore(Phone phone) {
        int score = 0;
        String camera = phone.getDescription().getRearCamera();
        if (camera != null) {
            if (camera.contains("48") || camera.contains("50")) score += 30;
            if (camera.contains("64")) score += 35;
            if (camera.contains("108") || camera.contains("200")) score += 50;
            if (camera.toLowerCase().contains("leica")) score += 20;
            if (camera.toLowerCase().contains("ultra")) score += 15;
        }

        // Price bonus (higher price usually means better camera)
        if (phone.getPrice() > 20_000_000) score += 25;
        else if (phone.getPrice() > 15_000_000) score += 15;
        else if (phone.getPrice() > 10_000_000) score += 10;

        return score;
    }

    private int calculateGamingScore(Phone phone) {
        int score = 0;

        // RAM score
        String ram = phone.getDescription().getRam();
        if (ram != null) {
            if (ram.contains("12") || ram.contains("16")) score += 40;
            else if (ram.contains("8")) score += 30;
            else if (ram.contains("6")) score += 20;
        }

        // Chip score
        String chip = phone.getDescription().getChipset();
        if (chip != null) {
            chip = chip.toLowerCase();
            if (chip.contains("snapdragon 8") || chip.contains("a17") || chip.contains("a18")) score += 50;
            else if (chip.contains("snapdragon 7") || chip.contains("a15") || chip.contains("a16")) score += 40;
            else if (chip.contains("dimensity 9") || chip.contains("snapdragon 888")) score += 35;
        }

        return score;
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
                "So sánh iPhone và Samsung",
                "Điện thoại gaming tốt nhất trong tầm giá",
                "Điện thoại giá rẻ dưới 10 triệu"
        );
    }

    // Inner classes
    private enum Intent {
        GREETING, COMPARE, BATTERY, CAMERA, GAMING, BUDGET, PREMIUM, NETWORK, RECOMMEND, GENERAL
    }

    private static class PriceRange {
        final double min, max;

        PriceRange(double min, double max) {
            this.min = min;
            this.max = max;
        }
    }
    // Thêm vào cuối class LocalAIChatbotService, trước dấu }

    private String generatePremiumResponse() {
        List<Phone> phones = filterPhones(new PriceRange(20_000_000, Double.MAX_VALUE), null, null);

        List<Phone> premiumPhones = phones.stream()
                .sorted(Comparator.comparingDouble(Phone::getPrice).reversed())
                .limit(3)
                .collect(Collectors.toList());

        StringBuilder response = new StringBuilder("💎 **Điện thoại cao cấp flagship** đáng mua:\n\n");

        for (int i = 0; i < premiumPhones.size(); i++) {
            Phone phone = premiumPhones.get(i);
            response.append(String.format("**%d. %s**\n", i + 1, phone.getName()));
            response.append(String.format("   💰 Giá: %,.0f VNĐ\n", phone.getPrice()));
            response.append(String.format("   🧠 Chip: %s\n", phone.getDescription().getChipset()));
            response.append(String.format("   💾 RAM: %s\n", phone.getDescription().getRam()));
            response.append("\n");
        }

        response.append("✨ **Flagship features**: Camera pro, chip mạnh nhất, build quality cao cấp.\n\n");
        response.append("🤔 Bạn có yêu cầu đặc biệt nào cho chiếc flagship này không?");

        return response.toString();
    }

    private String generateNetworkResponse(PriceRange priceRange) {
        List<Phone> phones = filterPhones(priceRange, null, null);

        List<Phone> networkPhones = phones.stream()
                .filter(p -> {
                    String network = p.getDescription().getNetworkSupport();
                    return network != null && network.contains("5G");
                })
                .sorted(Comparator.comparingDouble(Phone::getPrice))
                .limit(3)
                .collect(Collectors.toList());

        if (networkPhones.isEmpty()) {
            return "🔍 Không tìm thấy điện thoại 5G trong tầm giá này.\n\n" +
                    "💡 Hầu hết điện thoại từ 8 triệu trở lên đều hỗ trợ 5G.\n" +
                    "Bạn có thể cho tôi biết ngân sách cụ thể?";
        }

        StringBuilder response = new StringBuilder("📶 **Điện thoại hỗ trợ 5G** tốt nhất:\n\n");

        for (int i = 0; i < networkPhones.size(); i++) {
            Phone phone = networkPhones.get(i);
            response.append(String.format("**%d. %s**\n", i + 1, phone.getName()));
            response.append(String.format("   💰 Giá: %,.0f VNĐ\n", phone.getPrice()));
            response.append(String.format("   📶 Mạng: %s\n", phone.getDescription().getNetworkSupport()));
            response.append(String.format("   🧠 Chip: %s\n", phone.getDescription().getChipset()));
            response.append("\n");
        }

        response.append("🚀 **Lợi ích 5G**: Tốc độ nhanh, độ trễ thấp, streaming 4K mượt.\n\n");
        response.append("📍 Bạn có dùng 5G thường xuyên không?");

        return response.toString();
    }

    private String generateRecommendResponse(PriceRange priceRange, List<String> brands, List<String> features) {
        List<Phone> phones = filterPhones(priceRange, brands, features);

        if (phones.isEmpty()) {
            return "🔍 Không tìm thấy điện thoại phù hợp với tiêu chí.\n\n" +
                    "💡 Gợi ý:\n" +
                    "• Mở rộng ngân sách\n" +
                    "• Thử thương hiệu khác\n" +
                    "• Giảm yêu cầu tính năng\n\n" +
                    "🤔 Bạn có thể linh hoạt tiêu chí nào?";
        }

        // Sort by overall score
        List<Phone> recommendedPhones = phones.stream()
                .sorted((p1, p2) -> {
                    int score1 = calculateOverallScore(p1);
                    int score2 = calculateOverallScore(p2);
                    return Integer.compare(score2, score1);
                })
                .limit(3)
                .collect(Collectors.toList());

        StringBuilder response = new StringBuilder("🎯 **Điện thoại được đề xuất** cho bạn:\n\n");

        for (int i = 0; i < recommendedPhones.size(); i++) {
            Phone phone = recommendedPhones.get(i);
            response.append(String.format("**%d. %s** ⭐\n", i + 1, phone.getName()));
            response.append(String.format("   💰 Giá: %,.0f VNĐ\n", phone.getPrice()));
            response.append(String.format("   🧠 Chip: %s\n", phone.getDescription().getChipset()));
            response.append(String.format("   💾 RAM: %s\n", phone.getDescription().getRam()));
            response.append(String.format("   📷 Camera: %s\n", phone.getDescription().getRearCamera()));
            response.append(String.format("   🔋 Pin: %s\n", phone.getDescription().getAttribute("Pin")));
            response.append("\n");
        }

        response.append("💡 **Lý do đề xuất**: Cân bằng tốt giữa hiệu năng, giá cả và tính năng.\n\n");
        response.append("❓ Bạn muốn tìm hiểu chi tiết về điện thoại nào?");

        return response.toString();
    }

    private int calculateOverallScore(Phone phone) {
        int score = 0;

        // Price score (lower price = higher score in budget range)
        double price = phone.getPrice();
        if (price < 10_000_000) score += 20;
        else if (price < 15_000_000) score += 25;
        else if (price < 20_000_000) score += 30;
        else score += 15;

        // Combine camera and gaming scores
        score += calculateCameraScore(phone) / 2;
        score += calculateGamingScore(phone) / 2;

        return score;
    }
}