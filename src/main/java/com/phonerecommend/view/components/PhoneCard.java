package com.phonerecommend.view.components;

import com.phonerecommend.model.Phone;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.function.Consumer;

/**
 * Component để hiển thị thông tin điện thoại dạng card
 */
public class PhoneCard extends VBox {

    private final Phone phone;
    private final NumberFormat currencyFormat;
    private Consumer<Phone> onDetailClick;

    public PhoneCard(Phone phone) {
        this.phone = phone;
        this.currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

        initializeCard();
    }

    private void initializeCard() {
        // Thiết lập layout
        this.setSpacing(10);
        this.setPadding(new Insets(10));
        this.setAlignment(Pos.CENTER);
        this.setPrefWidth(320);
        this.setMaxWidth(320);
        this.setStyle("-fx-border-color: #e0e0e0; -fx-border-radius: 5; -fx-background-color: white;");

        // Tạo hình ảnh
        ImageView imageView = createImageView();

        // Tạo tên điện thoại
        Label nameLabel = createNameLabel();

        // Tạo giá
        Label priceLabel = createPriceLabel();

        // Tạo nút chi tiết
        Button detailButton = createDetailButton();

        // Thêm các thành phần vào card
        this.getChildren().addAll(imageView, nameLabel, priceLabel, detailButton);

        // Sự kiện click vào card
        this.setOnMouseClicked(e -> {
            if (onDetailClick != null) {
                onDetailClick.accept(phone);
            }
        });
    }

    private ImageView createImageView() {
        ImageView imageView = new ImageView();
        imageView.setFitWidth(200);
        imageView.setFitHeight(200);
        imageView.setPreserveRatio(true);

        String imageUrl = phone.getImageUrl();

        // Kiểm tra xem có URL hình ảnh không
        if (imageUrl != null && !imageUrl.isEmpty()) {
            try {
                // Tạo Image với background loading
                Image image = new Image(imageUrl, true);

                // Kiểm tra nếu hình ảnh tải thành công
                image.progressProperty().addListener((obs, oldProgress, newProgress) -> {
                    if (newProgress.doubleValue() >= 1.0) {
                        // Hình ảnh đã tải xong
                        if (!image.isError()) {
                            imageView.setImage(image);
                        } else {
                            // Lỗi khi tải hình ảnh, dùng placeholder
                            loadPlaceholderImage(imageView);
                        }
                    }
                });

                // Nếu hình ảnh đã được cache, set ngay
                if (!image.isBackgroundLoading() && !image.isError()) {
                    imageView.setImage(image);
                } else if (image.isError()) {
                    loadPlaceholderImage(imageView);
                }

            } catch (Exception e) {
                System.err.println("Error loading image: " + imageUrl + " - " + e.getMessage());
                loadPlaceholderImage(imageView);
            }
        } else {
            // Không có URL hình ảnh, dùng placeholder
            loadPlaceholderImage(imageView);
        }

        return imageView;
    }
    private void loadPlaceholderImage(ImageView imageView) {
        try {
            // Thử tải SVG placeholder trước
            imageView.setImage(new Image(getClass().getResourceAsStream("/images/phone_placeholder.svg")));
        } catch (Exception e) {
            // Nếu không có SVG, tạo hình ảnh đơn giản bằng code
            // Hoặc có thể tạo một hình nền màu xám
            System.err.println("Could not load placeholder image: " + e.getMessage());
        }
    }

    private Label createNameLabel() {
        Label nameLabel = new Label(phone.getName());
        nameLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        nameLabel.setWrapText(true);
        nameLabel.setAlignment(Pos.CENTER);
        nameLabel.setMaxWidth(300);
        return nameLabel;
    }

    private Label createPriceLabel() {
        Label priceLabel = new Label(currencyFormat.format(phone.getPrice()));
        priceLabel.setFont(Font.font("System", 14));
        priceLabel.setTextFill(Color.RED);
        return priceLabel;
    }

    private Button createDetailButton() {
        Button detailButton = new Button("Xem chi tiết");
        detailButton.setOnAction(e -> {
            if (onDetailClick != null) {
                onDetailClick.accept(phone);
            }
        });
        return detailButton;
    }

    /**
     * Thiết lập callback khi người dùng click để xem chi tiết
     * @param onDetailClick Callback function
     */
    public void setOnDetailClick(Consumer<Phone> onDetailClick) {
        this.onDetailClick = onDetailClick;
    }

    /**
     * Lấy đối tượng Phone của card này
     * @return Phone object
     */
    public Phone getPhone() {
        return phone;
    }
}