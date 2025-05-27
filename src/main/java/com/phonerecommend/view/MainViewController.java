package com.phonerecommend.view;

import com.phonerecommend.controller.ChatbotController;
import com.phonerecommend.controller.PhoneDetailController;
import com.phonerecommend.controller.PhoneSearchController;
import com.phonerecommend.model.Phone;
import com.phonerecommend.service.filter.PhoneFilter;
import com.phonerecommend.service.filter.impl.*;
import com.phonerecommend.service.filter.impl.CameraFeatureFilter.CameraFeature;
import com.phonerecommend.service.filter.impl.ChipsetFilter.ChipsetType;
import com.phonerecommend.service.filter.impl.SpecialFeatureFilter.SpecialFeature;
import com.phonerecommend.service.search.SortingService.SortCriteria;
import com.phonerecommend.model.PhoneDescription;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.net.URL;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.ResourceBundle;
import java.util.Map;
/**
 * Controller cho giao diện chính
 */
public class MainViewController implements Initializable {

    // Controllers
    private PhoneSearchController searchController;
    private PhoneDetailController detailController;
    private ChatbotController chatbotController;

    // UI Components - Main
    @FXML private TextField searchField;
    @FXML private GridPane phoneGridPane;
    @FXML private Label resultCountLabel;
    @FXML private ComboBox<String> sortComboBox;
    @FXML private BorderPane detailPane;

    // UI Components - Usage Filters
    @FXML private CheckBox gamingCheckbox;
    @FXML private CheckBox longBatteryCheckbox;
    @FXML private CheckBox highSpecCheckbox;
    @FXML private CheckBox compactSizeCheckbox;
    @FXML private CheckBox livestreamCheckbox;

    // UI Components - Brand Filters
    @FXML private CheckBox samsungCheckbox;
    @FXML private CheckBox appleCheckbox;
    @FXML private CheckBox oppoCheckbox;
    @FXML private CheckBox xiaomiCheckbox;

    // UI Components - Price Filters
    @FXML private ToggleGroup priceGroup;
    @FXML private RadioButton priceRangeAll;
    @FXML private RadioButton priceRangeUnder5M;
    @FXML private RadioButton priceRange5To10M;
    @FXML private RadioButton priceRange10To15M;
    @FXML private RadioButton priceRange15To20M;
    @FXML private RadioButton priceRangeOver20M;

    // UI Components - Camera Features
    @FXML private CheckBox portraitCheckbox;
    @FXML private CheckBox wideAngleCheckbox;
    @FXML private CheckBox video4kCheckbox;
    @FXML private CheckBox stabilizationCheckbox;
    @FXML private CheckBox zoomCheckbox;
    @FXML private CheckBox nightModeCheckbox;
    @FXML private CheckBox macroCheckbox;
    @FXML private CheckBox aiCameraCheckbox;
    @FXML private CheckBox motionPhotoCheckbox;

    // UI Components - RAM Capacity
    @FXML private CheckBox ram4To6GBCheckbox;
    @FXML private CheckBox ram8GBCheckbox;
    @FXML private CheckBox ram8To12GBCheckbox;
    @FXML private CheckBox ram12GBPlusCheckbox;

    // UI Components - Special Features
    @FXML private CheckBox support5GCheckbox;
    @FXML private CheckBox fingerprintCheckbox;
    @FXML private CheckBox faceRecognitionCheckbox;
    @FXML private CheckBox waterResistantCheckbox;
    @FXML private CheckBox dustResistantCheckbox;
    @FXML private CheckBox aiPhoneCheckbox;
    @FXML private CheckBox wirelessChargingCheckbox;
    @FXML private CheckBox stylusPenCheckbox;

    // UI Components - Chipset
    @FXML private CheckBox snapdragonCheckbox;
    @FXML private CheckBox appleChipsetCheckbox;
    @FXML private CheckBox exynosCheckbox;
    @FXML private CheckBox mediatekHelioCheckbox;
    @FXML private CheckBox mediatekDimensityCheckbox;

    // UI Components - Chat
    @FXML private VBox chatHistoryBox;
    @FXML private TextField chatInputField;
    @FXML private FlowPane suggestedQuestionsPane;

    // Current state
    private List<Phone> currentPhones = new ArrayList<>();
    private CompositeFilter compositeFilter;
    private NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Khởi tạo controllers
        searchController = new PhoneSearchController();
        detailController = new PhoneDetailController();
        chatbotController = new ChatbotController();

        // Khởi tạo composite filter
        compositeFilter = new CompositeFilter(true); // AND filter

        // Setup UI components
        setupSortComboBox();
        setupPriceToggleGroup();
        setupSuggestedQuestions();

        // Tải dữ liệu ban đầu
        loadInitialData();
    }

    private void setupSortComboBox() {
        // Thêm các tiêu chí sắp xếp
        sortComboBox.setItems(FXCollections.observableArrayList(
                "Giá thấp đến cao",
                "Giá cao đến thấp",
                "Xem nhiều",
                "Mới nhất"
        ));
        sortComboBox.getSelectionModel().selectFirst();
    }

    private void setupPriceToggleGroup() {
        // Tạo toggle group nếu chưa có
        if (priceGroup == null) {
            priceGroup = new ToggleGroup();
            priceRangeAll.setToggleGroup(priceGroup);
            priceRangeUnder5M.setToggleGroup(priceGroup);
            priceRange5To10M.setToggleGroup(priceGroup);
            priceRange10To15M.setToggleGroup(priceGroup);
            priceRange15To20M.setToggleGroup(priceGroup);
            priceRangeOver20M.setToggleGroup(priceGroup);
        }

        // Chọn "Tất cả" mặc định
        priceRangeAll.setSelected(true);
    }

    private void setupSuggestedQuestions() {
        // Lấy câu hỏi gợi ý từ chatbot
        List<String> questions = chatbotController.getSuggestedQuestions();

        // Tạo các Button cho câu hỏi gợi ý
        for (String question : questions) {
            Button questionButton = new Button(question);
            questionButton.setOnAction(e -> {
                chatInputField.setText(question);
                handleChatSend(e);
            });
            suggestedQuestionsPane.getChildren().add(questionButton);
        }
    }

    private void loadInitialData() {
        // Tải tất cả điện thoại
        currentPhones = searchController.getAllPhones();
        updatePhoneGridPane(currentPhones);
        updateResultCount(currentPhones.size());
    }

    @FXML
    private void handleSearch(ActionEvent event) {
        String keyword = searchField.getText();
        currentPhones = searchController.searchPhones(keyword);
        updatePhoneGridPane(currentPhones);
        updateResultCount(currentPhones.size());
    }

    @FXML
    private void handleFilterChange(ActionEvent event) {
        // Cập nhật bộ lọc dựa trên thay đổi UI
        updateFilters();

        // Tìm kiếm lại với bộ lọc mới
        String keyword = searchField.getText();
        currentPhones = searchController.searchPhones(keyword);

        // Cập nhật giao diện
        updatePhoneGridPane(currentPhones);
        updateResultCount(currentPhones.size());
    }

    @FXML
    private void handleClearFilters(ActionEvent event) {
        // Xóa tất cả lựa chọn bộ lọc nhu cầu sử dụng
        if (gamingCheckbox != null) gamingCheckbox.setSelected(false);
        if (longBatteryCheckbox != null) longBatteryCheckbox.setSelected(false);
        if (highSpecCheckbox != null) highSpecCheckbox.setSelected(false);
        if (compactSizeCheckbox != null) compactSizeCheckbox.setSelected(false);
        if (livestreamCheckbox != null) livestreamCheckbox.setSelected(false);

        // Xóa bộ lọc hãng
        if (samsungCheckbox != null) samsungCheckbox.setSelected(false);
        if (appleCheckbox != null) appleCheckbox.setSelected(false);
        if (oppoCheckbox != null) oppoCheckbox.setSelected(false);
        if (xiaomiCheckbox != null) xiaomiCheckbox.setSelected(false);

        // Reset giá
        if (priceRangeAll != null) priceRangeAll.setSelected(true);

        // Xóa bộ lọc camera
        if (portraitCheckbox != null) portraitCheckbox.setSelected(false);
        if (wideAngleCheckbox != null) wideAngleCheckbox.setSelected(false);
        if (video4kCheckbox != null) video4kCheckbox.setSelected(false);
        if (stabilizationCheckbox != null) stabilizationCheckbox.setSelected(false);
        if (zoomCheckbox != null) zoomCheckbox.setSelected(false);
        if (nightModeCheckbox != null) nightModeCheckbox.setSelected(false);
        if (macroCheckbox != null) macroCheckbox.setSelected(false);
        if (aiCameraCheckbox != null) aiCameraCheckbox.setSelected(false);
        if (motionPhotoCheckbox != null) motionPhotoCheckbox.setSelected(false);

        // Xóa bộ lọc RAM
        if (ram4To6GBCheckbox != null) ram4To6GBCheckbox.setSelected(false);
        if (ram8GBCheckbox != null) ram8GBCheckbox.setSelected(false);
        if (ram8To12GBCheckbox != null) ram8To12GBCheckbox.setSelected(false);
        if (ram12GBPlusCheckbox != null) ram12GBPlusCheckbox.setSelected(false);

        // Xóa bộ lọc tính năng đặc biệt
        if (support5GCheckbox != null) support5GCheckbox.setSelected(false);
        if (fingerprintCheckbox != null) fingerprintCheckbox.setSelected(false);
        if (faceRecognitionCheckbox != null) faceRecognitionCheckbox.setSelected(false);
        if (waterResistantCheckbox != null) waterResistantCheckbox.setSelected(false);
        if (dustResistantCheckbox != null) dustResistantCheckbox.setSelected(false);
        if (aiPhoneCheckbox != null) aiPhoneCheckbox.setSelected(false);
        if (wirelessChargingCheckbox != null) wirelessChargingCheckbox.setSelected(false);
        if (stylusPenCheckbox != null) stylusPenCheckbox.setSelected(false);

        // Xóa bộ lọc chipset
        if (snapdragonCheckbox != null) snapdragonCheckbox.setSelected(false);
        if (appleChipsetCheckbox != null) appleChipsetCheckbox.setSelected(false);
        if (exynosCheckbox != null) exynosCheckbox.setSelected(false);
        if (mediatekHelioCheckbox != null) mediatekHelioCheckbox.setSelected(false);
        if (mediatekDimensityCheckbox != null) mediatekDimensityCheckbox.setSelected(false);

        // Xóa tất cả bộ lọc và tìm kiếm lại
        String keyword = searchField.getText();
        searchController.clearFilters();
        currentPhones = searchController.searchPhones(keyword);

        // Cập nhật giao diện
        updatePhoneGridPane(currentPhones);
        updateResultCount(currentPhones.size());
    }

    @FXML
    private void handleSort(ActionEvent event) {
        String sortOption = sortComboBox.getValue();
        SortCriteria criteria = mapSortCriteria(sortOption);

        currentPhones = searchController.sortResults(criteria);
        updatePhoneGridPane(currentPhones);
    }

    @FXML
    private void handleChatSend(ActionEvent event) {
        String question = chatInputField.getText();
        if (question == null || question.trim().isEmpty()) {
            return;
        }

        // Hiển thị câu hỏi của người dùng
        addChatMessage(question, true);

        // Xóa input
        chatInputField.clear();

        // Hiển thị "đang trả lời..."
        TextFlow loadingMessage = new TextFlow();
        Text text = new Text("Đang trả lời...");
        text.setFill(Color.GRAY);
        loadingMessage.getChildren().add(text);

        VBox messageBox = new VBox(loadingMessage);
        messageBox.setAlignment(Pos.CENTER_LEFT);
        chatHistoryBox.getChildren().add(messageBox);

        // Xử lý bất đồng bộ để không block UI
        CompletableFuture.supplyAsync(() -> chatbotController.processQuestion(question))
                .thenAccept(answer -> {
                    Platform.runLater(() -> {
                        // Xóa thông báo "đang trả lời"
                        chatHistoryBox.getChildren().remove(messageBox);

                        // Hiển thị câu trả lời
                        addChatMessage(answer, false);
                    });
                });
    }

    private void updateFilters() {
        // Reset bộ lọc tổng hợp
        compositeFilter.clearFilters();

        // Bộ lọc nhu cầu sử dụng - thêm kiểm tra null
        if (gamingCheckbox != null && gamingCheckbox.isSelected()) {
            compositeFilter.addFilter(new GamingFilter());
        }
        if (longBatteryCheckbox != null && longBatteryCheckbox.isSelected()) {
            compositeFilter.addFilter(new LongBatteryFilter());
        }
        if (highSpecCheckbox != null && highSpecCheckbox.isSelected()) {
            compositeFilter.addFilter(new HighSpecFilter());
        }
        if (compactSizeCheckbox != null && compactSizeCheckbox.isSelected()) {
            compositeFilter.addFilter(new CompactSizeFilter());
        }
        if (livestreamCheckbox != null && livestreamCheckbox.isSelected()) {
            compositeFilter.addFilter(new LivestreamFilter());
        }

        // Bộ lọc hãng sản xuất
        List<String> selectedBrands = new ArrayList<>();
        if (samsungCheckbox != null && samsungCheckbox.isSelected()) selectedBrands.add("samsung");
        if (appleCheckbox.isSelected()) {
            selectedBrands.add("apple");
            selectedBrands.add("iphone"); // Thêm cả "iphone"
        }
        if (oppoCheckbox != null && oppoCheckbox.isSelected()) selectedBrands.add("oppo");
        if (xiaomiCheckbox != null && xiaomiCheckbox.isSelected()) selectedBrands.add("xiaomi");

        if (!selectedBrands.isEmpty()) {
            compositeFilter.addFilter(new BrandFilter(selectedBrands.toArray(new String[0])));
        }

        // Bộ lọc khoảng giá
        if (priceGroup != null) {
            RadioButton selectedPrice = (RadioButton) priceGroup.getSelectedToggle();
            if (selectedPrice != null && selectedPrice != priceRangeAll) {
                double minPrice = -1;
                double maxPrice = -1;

                if (selectedPrice == priceRangeUnder5M) {
                    minPrice = 0;
                    maxPrice = 5_000_000;
                } else if (selectedPrice == priceRange5To10M) {
                    minPrice = 5_000_000;
                    maxPrice = 10_000_000;
                } else if (selectedPrice == priceRange10To15M) {
                    minPrice = 10_000_000;
                    maxPrice = 15_000_000;
                } else if (selectedPrice == priceRange15To20M) {
                    minPrice = 15_000_000;
                    maxPrice = 20_000_000;
                } else if (selectedPrice == priceRangeOver20M) {
                    minPrice = 20_000_000;
                    maxPrice = -1;
                }

                final double finalMinPrice = minPrice;
                final double finalMaxPrice = maxPrice;

                PhoneFilter priceFilter = new PhoneFilter() {
                    @Override
                    public List<Phone> filter(List<Phone> phones) {
                        return phones.stream()
                                .filter(phone -> {
                                    double price = phone.getPrice();
                                    boolean meetsMinPrice = finalMinPrice < 0 || price >= finalMinPrice;
                                    boolean meetsMaxPrice = finalMaxPrice < 0 || price <= finalMaxPrice;
                                    return meetsMinPrice && meetsMaxPrice;
                                })
                                .collect(Collectors.toList());
                    }

                    @Override
                    public String getDescription() {
                        if (finalMinPrice < 0) {
                            return "Giá <= " + currencyFormat.format(finalMaxPrice);
                        } else if (finalMaxPrice < 0) {
                            return "Giá >= " + currencyFormat.format(finalMinPrice);
                        } else {
                            return currencyFormat.format(finalMinPrice) + " - " + currencyFormat.format(finalMaxPrice);
                        }
                    }

                    @Override
                    public String getFilterId() {
                        return "price_range";
                    }
                };

                compositeFilter.addFilter(priceFilter);
            }
        }

        // Bộ lọc tính năng camera - thêm kiểm tra null
        List<CameraFeature> cameraFeatures = new ArrayList<>();
        if (portraitCheckbox != null && portraitCheckbox.isSelected()) cameraFeatures.add(CameraFeature.PORTRAIT);
        if (wideAngleCheckbox != null && wideAngleCheckbox.isSelected()) cameraFeatures.add(CameraFeature.WIDE_ANGLE);
        if (video4kCheckbox != null && video4kCheckbox.isSelected()) cameraFeatures.add(CameraFeature.VIDEO_4K);
        if (stabilizationCheckbox != null && stabilizationCheckbox.isSelected()) cameraFeatures.add(CameraFeature.STABILIZATION);
        if (zoomCheckbox != null && zoomCheckbox.isSelected()) cameraFeatures.add(CameraFeature.ZOOM);
        if (nightModeCheckbox != null && nightModeCheckbox.isSelected()) cameraFeatures.add(CameraFeature.NIGHT_MODE);
        if (macroCheckbox != null && macroCheckbox.isSelected()) cameraFeatures.add(CameraFeature.MACRO);
        if (aiCameraCheckbox != null && aiCameraCheckbox.isSelected()) cameraFeatures.add(CameraFeature.AI_CAMERA);
        if (motionPhotoCheckbox != null && motionPhotoCheckbox.isSelected()) cameraFeatures.add(CameraFeature.MOTION_PHOTO);

        if (!cameraFeatures.isEmpty()) {
            compositeFilter.addFilter(new CameraFeatureFilter(
                    cameraFeatures.toArray(new CameraFeature[0])
            ));
        }

        // Bộ lọc RAM - thêm kiểm tra null
        List<RamCapacityFilter> ramFilters = new ArrayList<>();
        if (ram4To6GBCheckbox != null && ram4To6GBCheckbox.isSelected()) ramFilters.add(RamCapacityFilter.create4To6GBFilter());
        if (ram8GBCheckbox != null && ram8GBCheckbox.isSelected()) ramFilters.add(RamCapacityFilter.create8GBFilter());
        if (ram8To12GBCheckbox != null && ram8To12GBCheckbox.isSelected()) ramFilters.add(RamCapacityFilter.create8To12GBFilter());
        if (ram12GBPlusCheckbox != null && ram12GBPlusCheckbox.isSelected()) ramFilters.add(RamCapacityFilter.create12GBPlusFilter());

        if (!ramFilters.isEmpty()) {
            CompositeFilter ramComposite = new CompositeFilter(false); // OR filter cho RAM
            for (RamCapacityFilter filter : ramFilters) {
                ramComposite.addFilter(filter);
            }
            compositeFilter.addFilter(ramComposite);
        }

        // Bộ lọc tính năng đặc biệt - thêm kiểm tra null
        List<SpecialFeature> specialFeatures = new ArrayList<>();
        if (support5GCheckbox != null && support5GCheckbox.isSelected()) specialFeatures.add(SpecialFeature.SUPPORT_5G);
        if (fingerprintCheckbox != null && fingerprintCheckbox.isSelected()) specialFeatures.add(SpecialFeature.FINGERPRINT_SECURITY);
        if (faceRecognitionCheckbox != null && faceRecognitionCheckbox.isSelected()) specialFeatures.add(SpecialFeature.FACE_RECOGNITION);
        if (waterResistantCheckbox != null && waterResistantCheckbox.isSelected()) specialFeatures.add(SpecialFeature.WATER_RESISTANT);
        if (dustResistantCheckbox != null && dustResistantCheckbox.isSelected()) specialFeatures.add(SpecialFeature.DUST_RESISTANT);
        if (aiPhoneCheckbox != null && aiPhoneCheckbox.isSelected()) specialFeatures.add(SpecialFeature.AI_PHONE);
        if (wirelessChargingCheckbox != null && wirelessChargingCheckbox.isSelected()) specialFeatures.add(SpecialFeature.WIRELESS_CHARGING);
        if (stylusPenCheckbox != null && stylusPenCheckbox.isSelected()) specialFeatures.add(SpecialFeature.STYLUS_PEN);

        if (!specialFeatures.isEmpty()) {
            compositeFilter.addFilter(new SpecialFeatureFilter(
                    specialFeatures.toArray(new SpecialFeature[0])
            ));
        }

        // Bộ lọc chipset - thêm kiểm tra null
        List<ChipsetType> chipsetTypes = new ArrayList<>();
        if (snapdragonCheckbox != null && snapdragonCheckbox.isSelected()) chipsetTypes.add(ChipsetType.SNAPDRAGON);
        if (appleChipsetCheckbox != null && appleChipsetCheckbox.isSelected()) chipsetTypes.add(ChipsetType.APPLE_A);
        if (exynosCheckbox != null && exynosCheckbox.isSelected()) chipsetTypes.add(ChipsetType.EXYNOS);
        if (mediatekHelioCheckbox != null && mediatekHelioCheckbox.isSelected()) chipsetTypes.add(ChipsetType.MEDIATEK_HELIO);
        if (mediatekDimensityCheckbox != null && mediatekDimensityCheckbox.isSelected()) chipsetTypes.add(ChipsetType.MEDIATEK_DIMENSITY);

        if (!chipsetTypes.isEmpty()) {
            compositeFilter.addFilter(new ChipsetFilter(
                    chipsetTypes.toArray(new ChipsetType[0])
            ));
        }

        // Áp dụng bộ lọc
        searchController.filterPhones(compositeFilter);
    }

    private SortCriteria mapSortCriteria(String sortOption) {
        switch (sortOption) {
            case "Giá thấp đến cao":
                return SortCriteria.PRICE_LOW_TO_HIGH;
            case "Giá cao đến thấp":
                return SortCriteria.PRICE_HIGH_TO_LOW;
            case "Xem nhiều":
                return SortCriteria.MOST_VIEWED;
            case "Mới nhất":
                return SortCriteria.NEWEST;
            default:
                return SortCriteria.PRICE_LOW_TO_HIGH;
        }
    }

    private void updatePhoneGridPane(List<Phone> phones) {
        // Xóa tất cả hiển thị cũ
        phoneGridPane.getChildren().clear();

        // Thiết lập lưới
        int colCount = 3;
        int rowCount = (int) Math.ceil((double) phones.size() / colCount);

        // Thêm các điện thoại vào lưới
        for (int i = 0; i < phones.size(); i++) {
            Phone phone = phones.get(i);
            int col = i % colCount;
            int row = i / colCount;

            VBox phoneBox = createPhoneBox(phone);
            phoneGridPane.add(phoneBox, col, row);
        }
    }

    private VBox createPhoneBox(Phone phone) {
        // Tạo box chứa thông tin điện thoại
        VBox phoneBox = new VBox(10);
        phoneBox.setPadding(new Insets(10));
        phoneBox.setAlignment(Pos.CENTER);
        phoneBox.setPrefWidth(320);
        phoneBox.setMaxWidth(320);
        phoneBox.getStyleClass().add("phone-box");
        phoneBox.setStyle("-fx-border-color: #e0e0e0; -fx-border-radius: 5; -fx-background-color: white;");

        // ===== FIX IMAGE LOADING - THAY THẾ ĐOẠN NÀY =====
        ImageView imageView = new ImageView();
        imageView.setFitWidth(200);
        imageView.setFitHeight(200);
        imageView.setPreserveRatio(true);

        String imageUrl = phone.getImageUrl();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            try {
                // Improved loading with timeout and background loading
                Image image = new Image(imageUrl, 200, 200, true, true, true);

                // Check for immediate errors
                if (image.isError()) {
                    System.out.println("Error loading image immediately: " + imageUrl);
                    setPlaceholderImage(imageView);
                } else {
                    // Set image immediately if loaded
                    imageView.setImage(image);

                    // Handle background loading errors
                    image.progressProperty().addListener((obs, oldProgress, newProgress) -> {
                        if (newProgress.doubleValue() >= 1.0) {
                            if (image.isError()) {
                                System.out.println("Image failed during background loading: " + imageUrl);
                                Platform.runLater(() -> setPlaceholderImage(imageView));
                            }
                        }
                    });

                    // Also listen for error property changes
                    image.errorProperty().addListener((obs, wasError, isError) -> {
                        if (isError) {
                            System.out.println("Image error detected: " + imageUrl);
                            Platform.runLater(() -> setPlaceholderImage(imageView));
                        }
                    });
                }
            } catch (Exception e) {
                System.err.println("Exception loading image: " + imageUrl + " - " + e.getMessage());
                setPlaceholderImage(imageView);
            }
        } else {
            System.out.println("No image URL for phone: " + phone.getName());
            setPlaceholderImage(imageView);
        }
        // ===== END IMAGE LOADING FIX =====

        // Thêm tên điện thoại
        Label nameLabel = new Label(phone.getName());
        nameLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        nameLabel.setWrapText(true);
        nameLabel.setAlignment(Pos.CENTER);
        nameLabel.setMaxWidth(300);

        // Thêm giá
        Label priceLabel = new Label(currencyFormat.format(phone.getPrice()));
        priceLabel.setFont(Font.font("System", 14));
        priceLabel.setTextFill(Color.RED);

        // Thêm nút "Xem chi tiết"
        Button detailButton = new Button("Xem chi tiết");
        detailButton.setOnAction(e -> showPhoneDetail(phone));

        // Thêm các thành phần vào box
        phoneBox.getChildren().addAll(imageView, nameLabel, priceLabel, detailButton);

        // Sự kiện click vào box
        phoneBox.setOnMouseClicked(e -> showPhoneDetail(phone));

        return phoneBox;
    }

    private void showPhoneDetail(Phone phone) {
        // Cập nhật lượt xem
        searchController.incrementPhoneViewCount(phone);

        // Tạo giao diện chi tiết
        VBox detailBox = new VBox(15);
        detailBox.setPadding(new Insets(20));

        // Tiêu đề
        Label titleLabel = new Label(phone.getName());
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 24));
        titleLabel.setWrapText(true);

        // Hình ảnh và thông tin cơ bản
        HBox mainContent = new HBox(20);

        // Phần hình ảnh
        ImageView imageView = new ImageView();
        imageView.setFitWidth(300);
        imageView.setFitHeight(300);
        imageView.setPreserveRatio(true);

        try {
            Image image = new Image(phone.getImageUrl(), true);
            imageView.setImage(image);
        } catch (Exception e) {
            // Nếu không tải được hình, hiển thị hình mặc định
            imageView.setImage(new Image(getClass().getResourceAsStream("/images/phone_placeholder.png")));
        }

        VBox imageBox = new VBox(10, imageView);
        imageBox.setAlignment(Pos.CENTER);

        // Giá và nút mua
        Label priceLabel = new Label(currencyFormat.format(phone.getPrice()));
        priceLabel.setFont(Font.font("System", FontWeight.BOLD, 18));
        priceLabel.setTextFill(Color.RED);

        Button buyButton = new Button("Mua ngay");
        buyButton.setStyle("-fx-background-color: #ff4d4d; -fx-text-fill: white;");
        buyButton.setPrefWidth(200);

        imageBox.getChildren().addAll(priceLabel, buyButton);

        // Phần thông tin cơ bản
        VBox infoBox = new VBox(8);

        PhoneDescription desc = phone.getDescription();

        addInfoRow(infoBox, "Màn hình", desc.getScreenSize() + ", " + desc.getScreenTechnology());
        addInfoRow(infoBox, "Chip", desc.getChipset());
        addInfoRow(infoBox, "RAM", desc.getRam());
        addInfoRow(infoBox, "Bộ nhớ", desc.getStorage());
        addInfoRow(infoBox, "Camera sau", desc.getRearCamera());
        addInfoRow(infoBox, "Camera trước", desc.getFrontCamera());
        addInfoRow(infoBox, "Pin", desc.getAttribute("Pin"));
        addInfoRow(infoBox, "Hệ điều hành", desc.getAttribute("Hệ điều hành"));

        // Kết hợp các phần
        mainContent.getChildren().addAll(imageBox, infoBox);

        // Thông số chi tiết
        TitledPane specPane = new TitledPane();
        specPane.setText("Thông số kỹ thuật chi tiết");

        GridPane specGrid = new GridPane();
        specGrid.setHgap(15);
        specGrid.setVgap(8);
        specGrid.setPadding(new Insets(10));

        Map<String, String> allAttrs = desc.getAllAttributes();
        int row = 0;
        for (Map.Entry<String, String> entry : allAttrs.entrySet()) {
            Label keyLabel = new Label(entry.getKey() + ":");
            keyLabel.setFont(Font.font("System", FontWeight.BOLD, 12));

            Label valueLabel = new Label(entry.getValue());
            valueLabel.setWrapText(true);

            specGrid.add(keyLabel, 0, row);
            specGrid.add(valueLabel, 1, row);
            row++;
        }

        ScrollPane specScroll = new ScrollPane(specGrid);
        specScroll.setFitToWidth(true);
        specScroll.setPrefHeight(300);
        specPane.setContent(specScroll);

        detailBox.getChildren().addAll(titleLabel, mainContent, specPane);

        // Hiển thị trong detailPane
        detailPane.setCenter(new ScrollPane(detailBox));
    }

    private void addInfoRow(VBox container, String label, String value) {
        HBox row = new HBox(10);

        Label labelText = new Label(label + ":");
        labelText.setFont(Font.font("System", FontWeight.BOLD, 12));
        labelText.setMinWidth(100);

        Label valueText = new Label(value);
        valueText.setWrapText(true);

        row.getChildren().addAll(labelText, valueText);
        container.getChildren().add(row);
    }

    private void updateResultCount(int count) {
        resultCountLabel.setText("Tìm thấy " + count + " kết quả");
    }

    private void addChatMessage(String message, boolean isUser) {
        TextFlow textFlow = new TextFlow();

        Text text = new Text(message);
        if (isUser) {
            text.setFill(Color.BLUE);
        } else {
            text.setFill(Color.BLACK);
        }
        textFlow.getChildren().add(text);

        VBox messageBox = new VBox(textFlow);
        messageBox.setPadding(new Insets(10));
        messageBox.setStyle("-fx-background-color: " + (isUser ? "#e6f2ff" : "#f2f2f2") + "; -fx-background-radius: 5;");

        if (isUser) {
            messageBox.setAlignment(Pos.CENTER_RIGHT);
        } else {
            messageBox.setAlignment(Pos.CENTER_LEFT);
        }

        chatHistoryBox.getChildren().add(messageBox);

        // Scroll to bottom
        Platform.runLater(() -> {
            chatHistoryBox.layout();
            for (javafx.scene.Node node : chatHistoryBox.getChildren()) {
                node.getParent().layout();
            }
        });
    }

    private void setPlaceholderImage(ImageView imageView) {
        try {
            // Create a simple colored placeholder image
            javafx.scene.image.WritableImage placeholderImg = new javafx.scene.image.WritableImage(200, 200);
            javafx.scene.image.PixelWriter pw = placeholderImg.getPixelWriter();

            // Fill with light gray color
            javafx.scene.paint.Color bgColor = javafx.scene.paint.Color.LIGHTGRAY;
            javafx.scene.paint.Color borderColor = javafx.scene.paint.Color.GRAY;

            for (int x = 0; x < 200; x++) {
                for (int y = 0; y < 200; y++) {
                    // Create a simple border effect
                    if (x < 5 || x > 194 || y < 5 || y > 194) {
                        pw.setColor(x, y, borderColor);
                    } else {
                        pw.setColor(x, y, bgColor);
                    }
                }
            }

            // Draw a simple phone icon in the center
            javafx.scene.paint.Color iconColor = javafx.scene.paint.Color.DARKGRAY;
            // Draw simple phone shape (rectangle)
            for (int x = 75; x < 125; x++) {
                for (int y = 60; y < 140; y++) {
                    if ((x == 75 || x == 124) || (y == 60 || y == 139)) {
                        pw.setColor(x, y, iconColor);
                    }
                }
            }

            imageView.setImage(placeholderImg);
            System.out.println("Set custom placeholder image");

        } catch (Exception e) {
            System.err.println("Error creating custom placeholder: " + e.getMessage());
            // Fallback: just set background style
            imageView.setStyle("-fx-background-color: #f0f0f0; " +
                    "-fx-border-color: #ccc; " +
                    "-fx-border-width: 1px; " +
                    "-fx-border-radius: 5px;");
        }
    }
}