# Phone Recommendation System with AI Chatbot

Hệ thống gợi ý điện thoại với AI Chatbot tích hợp Gemini API và RAG.

## 🚀 Quick Start

### 1. Clone Repository

```bash
git clone https://github.com/your-username/phone-recommendation-system.git
cd phone-recommendation-system
```

### 2. Setup API Key

**Để sử dụng AI Chatbot, bạn cần cấu hình Gemini API Key:**

1. **Lấy Gemini API Key:**
   - Truy cập: https://aistudio.google.com/
   - Đăng nhập Google account
   - Nhấn **Get API key** → **Create API key**
   - Copy API key

2. **Paste API Key vào code:**
   - Mở file: `src/main/java/com/phonerecommend/service/chatbot/DirectGeminiChatbotImpl.java`
   - Tìm dòng: `private static final String GEMINI_API_KEY = "YOUR_GEMINI_API_KEY_HERE";`
   - Thay `YOUR_GEMINI_API_KEY_HERE` bằng API key của bạn

### 3. Run Application

```bash
# Compile và chạy
mvn clean compile
mvn javafx:run

# Hoặc chạy main class
mvn exec:java -Dexec.mainClass="com.phonerecommend.PhoneRecommendApp"
```

## 🤖 AI Engine Options

System hỗ trợ 2 AI engines:

### Option 1: Gemini API (Khuyến nghị)
- **Ưu điểm:** Thông minh, câu trả lời chất lượng cao
- **Nhược điểm:** Cần internet + API key
- **Setup:** Cấu hình API key như hướng dẫn trên

### Option 2: Local RAG
- **Ưu điểm:** Offline, không cần API key
- **Nhược điểm:** Câu trả lời đơn giản hơn
- **Setup:** Không cần cấu hình gì

### Switch giữa AI Engines

Trong `ChatbotController`, thay đổi constructor:

```java
// Sử dụng Gemini API (cần API key)
this.chatbotService = new DirectGeminiChatbotImpl(phoneRepository);

// Sử dụng Local RAG (offline)
this.chatbotService = new RAGChatbotImpl(phoneRepository);
```

## 📋 Requirements

- **Java 17+**
- **JavaFX 17+**
- **Maven 3.6+**
- **Internet connection** (cho Gemini API)

## 🗃️ Data Source

- **Repository:** Local JSON hoặc Firebase Firestore
- **Switch Repository:** Trong `PhoneRecommendApp.java`

```java
// Local JSON
RepositoryFactory.setRepositoryType(RepositoryType.LOCAL_JSON);

// Firebase Firestore
RepositoryFactory.setRepositoryType(RepositoryType.FIREBASE_FIRESTORE);
```

## 🔧 Configuration

### Repository Configuration

```java
// Local JSON
RepositoryFactory.setLocalJsonPath("data/phones.json");

// Firebase
RepositoryFactory.setFirestoreProjectId("your-project-id");
```

### AI Configuration

- **Gemini API:** Paste API key vào `DirectGeminiChatbotImpl.java`
- **Switch AI:** Thay constructor trong `ChatbotController`

### Firebase Setup (Optional)

1. **Tạo Firebase Project:**
   - Truy cập: https://console.firebase.google.com/
   - Tạo project mới, enable Firestore

2. **Download Service Account Key:**
   - Project Settings → Service Accounts
   - Generate new private key → Download JSON

3. **Cấu hình:**
   - Đặt file JSON vào: `src/main/resources/serviceAccountKey.json`
   - Uncomment code trong `FirebaseFirestoreRepository.java`

## 🎯 Features

- ✅ **Phone Search & Filter** - Tìm kiếm và lọc điện thoại
- ✅ **AI Chatbot** - Tư vấn thông minh với Gemini API
- ✅ **RAG System** - Retrieval-Augmented Generation
- ✅ **Multi Repository** - Local JSON / Firebase Firestore
- ✅ **JavaFX UI** - Giao diện desktop hiện đại
- ✅ **Web Scraping** - Tự động cập nhật dữ liệu
- ✅ **Smart Filtering** - Lọc theo hãng, giá, tính năng
- ✅ **Product Comparison** - So sánh điện thoại

### Detailed Features

**🔍 Search & Filter:**
- Tìm kiếm theo từ khóa
- Lọc theo hãng (Samsung, Apple, Xiaomi, Oppo)
- Lọc theo giá (5 khoảng giá)
- Lọc theo nhu cầu (gaming, pin trâu, cấu hình cao)
- Lọc theo tính năng camera, RAM, chipset

**🤖 AI Chatbot:**
- Trả lời câu hỏi về điện thoại
- So sánh sản phẩm
- Gợi ý theo nhu cầu
- Câu hỏi gợi ý có sẵn

## 🛠️ Development

/**
 * CẤU TRÚC PROJECT: Hệ Thống Gợi Ý Điện Thoại
 * 
 * src/
 * ├── main/
 * │   ├── java/
 * │   │   ├── com/
 * │   │   │   ├── phonerecommend/
 * │   │   │   │   ├── model/                          # Package chứa các model
 * │   │   │   │   │   ├── Phone.java                  # Thông tin điện thoại
 * │   │   │   │   │   ├── PhoneDescription.java       # Mô tả chi tiết điện thoại
 * │   │   │   │   │   └── User.java                   # Thông tin người dùng
 * │   │   │   │   │
 * │   │   │   │   ├── repository/                     # Package cho Repository Pattern
 * │   │   │   │   │   ├── PhoneRepository.java        # Interface Repository
 * │   │   │   │   │   ├── impl/
 * │   │   │   │   │   │   ├── LocalJsonPhoneRepository.java  # Lưu trữ JSON local
 * │   │   │   │   │   │   ├── FirebasePhoneRepository.java   # Lưu trữ Firebase
 * │   │   │   │   │   │   └── MongoDBPhoneRepository.java    # Lưu trữ MongoDB
 * │   │   │   │   │   │
 * │   │   │   │   │   └── RepositoryFactory.java     # Factory tạo Repository
 * │   │   │   │   │ 
 * │   │   │   │   ├── service/                       # Package chứa các service
 * │   │   │   │   │   ├── filter/                    # Các bộ lọc
 * │   │   │   │   │   │   ├── PhoneFilter.java       # Interface bộ lọc
 * │   │   │   │   │   │   ├── AbstractPhoneFilter.java  # Abstract class cơ sở cho bộ lọc
 * │   │   │   │   │   │   └── impl/                     # Triển khai cụ thể
 * │   │   │   │   │   │       ├── GamingFilter.java
 * │   │   │   │   │   │       ├── LongBatteryFilter.java
 * │   │   │   │   │   │       ├── HighSpecFilter.java
 * │   │   │   │   │   │       ├── CompactSizeFilter.java
 * │   │   │   │   │   │       ├── LivestreamFilter.java
 * │   │   │   │   │   │       ├── BrandFilter.java
 * │   │   │   │   │   │       ├── CameraFeatureFilter.java
 * │   │   │   │   │   │       ├── RamCapacityFilter.java
 * │   │   │   │   │   │       ├── SpecialFeatureFilter.java
 * │   │   │   │   │   │       ├── ChipsetFilter.java
 * │   │   │   │   │   │       └── CompositeFilter.java    # Tổng hợp nhiều bộ lọc
 * │   │   │   │   │   │ 
 * │   │   │   │   │   ├── search/                    # Dịch vụ tìm kiếm
 * │   │   │   │   │   │   ├── SearchService.java     # Dịch vụ tìm kiếm
 * │   │   │   │   │   │   └── SortingService.java    # Dịch vụ sắp xếp kết quả
 * │   │   │   │   │   │
 * │   │   │   │   │   ├── scraper/                   # Dịch vụ lấy thông tin web
 * │   │   │   │   │   │   ├── WebScraperService.java    # Interface scraper
 * │   │   │   │   │   │   └── impl/                     # Triển khai cụ thể
 * │   │   │   │   │   │       └── CellphonesScraper.java   # Scraper cho trang Cellphones
 * │   │   │   │   │   │
 * │   │   │   │   │   └── chatbot/                   # Dịch vụ chatbot RAG
 * │   │   │   │   │       ├── ChatbotService.java    # Interface chatbot
 * │   │   │   │   │       ├── PhoneDataEmbedding.java   # Nhúng dữ liệu điện thoại
 * │   │   │   │   │       └── RAGChatbotImpl.java    # Triển khai RAG chatbot
 * │   │   │   │   │
 * │   │   │   │   ├── controller/                    # Package MVC controller
 * │   │   │   │   │   ├── PhoneSearchController.java  # Điều khiển tìm kiếm
 * │   │   │   │   │   ├── PhoneFilterController.java  # Điều khiển lọc
 * │   │   │   │   │   ├── PhoneDetailController.java  # Điều khiển chi tiết
 * │   │   │   │   │   └── ChatbotController.java      # Điều khiển chatbot
 * │   │   │   │   │
 * │   │   │   │   ├── view/                          # Package JavaFX view
 * │   │   │   │   │   ├── MainView.fxml              # Giao diện chính
 * │   │   │   │   │   ├── PhoneListView.fxml         # Giao diện danh sách
 * │   │   │   │   │   ├── PhoneDetailView.fxml       # Giao diện chi tiết
 * │   │   │   │   │   └── ChatbotView.fxml           # Giao diện chatbot
 * │   │   │   │   │
 * │   │   │   │   └── PhoneRecommendApp.java         # Main class ứng dụng
 * │   │
 * │   └── resources/
 * │       ├── images/                               # Thư mục hình ảnh
 * │       ├── styles/                               # CSS styles
 * │       └── data/                                 # Dữ liệu mẫu
 * │           └── phones.json                       # File dữ liệu điện thoại
 * │
 * └── test/                                         # Unit tests
 *     └── java/
 *         └── com/
 *             └── phonerecommend/
 *                 ├── repository/
 *                 ├── service/
 *                 └── controller/
 */

### Adding New AI Engine

1. Implement `ChatbotService` interface
2. Add to `ChatbotController` constructor
3. Update switch logic

### Adding New Repository

1. Implement `PhoneRepository` interface
2. Add to `RepositoryFactory`
3. Configure in `PhoneRecommendApp`

## 🧪 Testing

```bash
# Run all tests
mvn test

# Test specific component
mvn test -Dtest=RAGChatbotImplTest
mvn test -Dtest=LocalJsonPhoneRepositoryTest
```

## 📖 Usage Guide

### Using Search & Filter

1. Enter keyword in search box
2. Use filters on the left panel
3. Sort results by price/popularity

### Using AI Chatbot

1. Click "Chat" tab
2. Type question or click suggested questions
3. **Example questions:**
   - "Điện thoại nào pin trâu nhất?"
   - "So sánh iPhone 16 với Samsung S25"
   - "Điện thoại gaming dưới 15 triệu"

### View Phone Details

1. Click on phone card or "View Details"
2. See full specifications
3. Compare with other phones

## 📦 Dependencies

```xml
<!-- JavaFX -->
<dependency>
    <groupId>org.openjfx</groupId>
    <artifactId>javafx-controls</artifactId>
    <version>17</version>
</dependency>

<!-- JSON Processing -->
<dependency>
    <groupId>org.json</groupId>
    <artifactId>json</artifactId>
    <version>20230618</version>
</dependency>

<!-- Web Scraping -->
<dependency>
    <groupId>org.jsoup</groupId>
    <artifactId>jsoup</artifactId>
    <version>1.17.2</version>
</dependency>
```

## 🛠️ Troubleshooting

### Common Issues

1. **JavaFX Runtime Error:**
   ```bash
   mvn javafx:run
   ```

2. **API Key không hoạt động:**
   - Check API key đã paste đúng
   - Kiểm tra internet connection
   - Check Gemini API quota

3. **Firebase Connection Error:**
   - Check `serviceAccountKey.json` file
   - Verify project ID
   - Uncomment Firebase code

## 📝 Roadmap

### Version 2.0
- [ ] OpenAI GPT integration
- [ ] Advanced product comparison
- [ ] Multi-language support
- [ ] Mobile app companion

### Version 2.1
- [ ] MongoDB integration
- [ ] REST API
- [ ] Machine Learning recommendations
- [ ] User reviews system

## 📝 License

This project is licensed under the MIT License.

## 🤝 Contributing

1. Fork the repository
2. Create feature branch (`git checkout -b feature/AmazingFeature`)
3. Make changes
4. Submit pull request

## ⚠️ Important Notes

- **Không commit API keys** lên Git
- **File `.gitignore`** đã được cấu hình để bảo vệ sensitive data
- **Gemini API** có rate limit, sử dụng hợp lý
- **Firebase** cần service account key để hoạt động
- **Local JSON** là option an toàn nhất cho development

## 🙏 Acknowledgments

- [CellphoneS.com.vn](https://cellphones.com.vn) - Phone data source
- [Google Gemini API](https://aistudio.google.com/) - AI Chat engine
- [JavaFX Community](https://openjfx.io/) - UI Framework

---

**⭐ Star this repo if you find it useful!**
