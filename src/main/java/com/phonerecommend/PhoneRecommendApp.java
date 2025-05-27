package com.phonerecommend;

import com.phonerecommend.controller.ChatbotController;
import com.phonerecommend.controller.PhoneDetailController;
import com.phonerecommend.controller.PhoneSearchController;
import com.phonerecommend.repository.PhoneRepository;
import com.phonerecommend.repository.RepositoryFactory;
import com.phonerecommend.repository.RepositoryFactory.RepositoryType;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Lớp chính khởi động ứng dụng
 */
public class PhoneRecommendApp extends Application {

    private static final String APP_TITLE = "Hệ Thống Gợi Ý Điện Thoại";
    private static final int DEFAULT_WIDTH = 1200;
    private static final int DEFAULT_HEIGHT = 800;

    private PhoneSearchController searchController;
    private PhoneDetailController detailController;
    private ChatbotController chatbotController;

    /**
     * Phương thức khởi động ứng dụng
     * @param args Tham số dòng lệnh
     */
    public static void main(String[] args) {
        // Thiết lập loại repository (mặc định là LOCAL_JSON)
        // Có thể thay đổi thành FIREBASE hoặc MONGODB tùy theo cấu hình
        RepositoryFactory.setRepositoryType(RepositoryType.LOCAL_JSON);

        // Thiết lập đường dẫn file dữ liệu nếu sử dụng LOCAL_JSON
        RepositoryFactory.setLocalJsonPath("data/phones.json");

        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            // Khởi tạo các controller
            initializeControllers();

            // Tải giao diện
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/MainView.fxml"));
            Parent root = loader.load();

            // Thiết lập cửa sổ
            Scene scene = new Scene(root, DEFAULT_WIDTH, DEFAULT_HEIGHT);
            primaryStage.setTitle(APP_TITLE);
            primaryStage.setScene(scene);
            primaryStage.show();

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Không thể khởi động ứng dụng: " + e.getMessage());
        }
    }

    /**
     * Khởi tạo các controller
     */
    private void initializeControllers() {
        searchController = new PhoneSearchController();
        detailController = new PhoneDetailController();
        chatbotController = new ChatbotController();
    }

    @Override
    public void stop() {
        // Dọn dẹp tài nguyên khi đóng ứng dụng
        System.out.println("Đóng ứng dụng, dọn dẹp tài nguyên...");
    }
}