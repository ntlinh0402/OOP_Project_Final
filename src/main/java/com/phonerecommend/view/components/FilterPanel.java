package com.phonerecommend.view.components;

import com.phonerecommend.service.filter.PhoneFilter;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Component panel cho các bộ lọc
 */
public class FilterPanel extends ScrollPane {

    private VBox mainContainer;
    private Consumer<List<PhoneFilter>> onFiltersChanged;

    public FilterPanel() {
        initializePanel();
    }

    private void initializePanel() {
        // Thiết lập ScrollPane
        this.setFitToWidth(true);
        this.setPrefWidth(300);

        // Tạo container chính
        mainContainer = new VBox(10);
        mainContainer.setPadding(new Insets(10));

        // Tạo tiêu đề
        Label titleLabel = new Label("CHỌN THEO TIÊU CHÍ");
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        mainContainer.getChildren().add(titleLabel);

        // Thêm các nhóm bộ lọc
        addUsageFilters();
        addBrandFilters();
        addPriceFilters();
        addCameraFilters();
        addSpecialFeatureFilters();
        addClearButton();

        // Thiết lập content cho ScrollPane
        this.setContent(mainContainer);
    }

    private void addUsageFilters() {
        TitledPane usagePane = new TitledPane();
        usagePane.setText("Nhu cầu sử dụng");
        usagePane.setExpanded(false);

        VBox usageBox = new VBox(5);

        CheckBox gamingCheckbox = new CheckBox("Chơi game");
        CheckBox longBatteryCheckbox = new CheckBox("Pin trâu");
        CheckBox highSpecCheckbox = new CheckBox("Cấu hình cao");
        CheckBox compactSizeCheckbox = new CheckBox("Nhỏ gọn, dễ cầm");
        CheckBox livestreamCheckbox = new CheckBox("Livestream");

        usageBox.getChildren().addAll(
                gamingCheckbox, longBatteryCheckbox, highSpecCheckbox,
                compactSizeCheckbox, livestreamCheckbox
        );

        usagePane.setContent(usageBox);
        mainContainer.getChildren().add(usagePane);
    }

    private void addBrandFilters() {
        TitledPane brandPane = new TitledPane();
        brandPane.setText("Hãng sản xuất");
        brandPane.setExpanded(false);

        VBox brandBox = new VBox(5);

        CheckBox samsungCheckbox = new CheckBox("Samsung");
        CheckBox appleCheckbox = new CheckBox("Apple");
        CheckBox oppoCheckbox = new CheckBox("Oppo");
        CheckBox xiaomiCheckbox = new CheckBox("Xiaomi");

        brandBox.getChildren().addAll(
                samsungCheckbox, appleCheckbox, oppoCheckbox, xiaomiCheckbox
        );

        brandPane.setContent(brandBox);
        mainContainer.getChildren().add(brandPane);
    }

    private void addPriceFilters() {
        TitledPane pricePane = new TitledPane();
        pricePane.setText("Giá");
        pricePane.setExpanded(false);

        VBox priceBox = new VBox(5);
        ToggleGroup priceGroup = new ToggleGroup();

        RadioButton priceRangeAll = new RadioButton("Tất cả");
        RadioButton priceRangeUnder5M = new RadioButton("Dưới 5 triệu");
        RadioButton priceRange5To10M = new RadioButton("5 - 10 triệu");
        RadioButton priceRange10To15M = new RadioButton("10 - 15 triệu");
        RadioButton priceRange15To20M = new RadioButton("15 - 20 triệu");
        RadioButton priceRangeOver20M = new RadioButton("Trên 20 triệu");

        priceRangeAll.setToggleGroup(priceGroup);
        priceRangeUnder5M.setToggleGroup(priceGroup);
        priceRange5To10M.setToggleGroup(priceGroup);
        priceRange10To15M.setToggleGroup(priceGroup);
        priceRange15To20M.setToggleGroup(priceGroup);
        priceRangeOver20M.setToggleGroup(priceGroup);

        priceRangeAll.setSelected(true);

        priceBox.getChildren().addAll(
                priceRangeAll, priceRangeUnder5M, priceRange5To10M,
                priceRange10To15M, priceRange15To20M, priceRangeOver20M
        );

        pricePane.setContent(priceBox);
        mainContainer.getChildren().add(pricePane);
    }

    private void addCameraFilters() {
        TitledPane cameraPane = new TitledPane();
        cameraPane.setText("Tính năng camera");
        cameraPane.setExpanded(false);

        VBox cameraBox = new VBox(5);

        CheckBox portraitCheckbox = new CheckBox("Chụp xóa phông");
        CheckBox wideAngleCheckbox = new CheckBox("Chụp góc rộng");
        CheckBox video4kCheckbox = new CheckBox("Quay video 4K");
        CheckBox stabilizationCheckbox = new CheckBox("Chống rung");
        CheckBox zoomCheckbox = new CheckBox("Chụp zoom xa");
        CheckBox nightModeCheckbox = new CheckBox("Chụp đêm");
        CheckBox macroCheckbox = new CheckBox("Chụp macro");
        CheckBox aiCameraCheckbox = new CheckBox("Camera AI");

        cameraBox.getChildren().addAll(
                portraitCheckbox, wideAngleCheckbox, video4kCheckbox,
                stabilizationCheckbox, zoomCheckbox, nightModeCheckbox,
                macroCheckbox, aiCameraCheckbox
        );

        cameraPane.setContent(cameraBox);
        mainContainer.getChildren().add(cameraPane);
    }

    private void addSpecialFeatureFilters() {
        TitledPane specialPane = new TitledPane();
        specialPane.setText("Tính năng đặc biệt");
        specialPane.setExpanded(false);

        VBox specialBox = new VBox(5);

        CheckBox support5GCheckbox = new CheckBox("Hỗ trợ 5G");
        CheckBox fingerprintCheckbox = new CheckBox("Bảo mật vân tay");
        CheckBox faceRecognitionCheckbox = new CheckBox("Nhận diện khuôn mặt");
        CheckBox waterResistantCheckbox = new CheckBox("Kháng nước");
        CheckBox dustResistantCheckbox = new CheckBox("Kháng bụi");
        CheckBox aiPhoneCheckbox = new CheckBox("Điện thoại AI");
        CheckBox wirelessChargingCheckbox = new CheckBox("Sạc không dây");
        CheckBox stylusPenCheckbox = new CheckBox("Đi kèm bút cảm ứng");

        specialBox.getChildren().addAll(
                support5GCheckbox, fingerprintCheckbox, faceRecognitionCheckbox,
                waterResistantCheckbox, dustResistantCheckbox, aiPhoneCheckbox,
                wirelessChargingCheckbox, stylusPenCheckbox
        );

        specialPane.setContent(specialBox);
        mainContainer.getChildren().add(specialPane);
    }

    private void addClearButton() {
        Button clearButton = new Button("Xóa tất cả bộ lọc");
        clearButton.setMaxWidth(Double.MAX_VALUE);
        clearButton.setOnAction(e -> clearAllFilters());

        mainContainer.getChildren().add(clearButton);
    }

    private void clearAllFilters() {
        // Xóa tất cả checkbox đã chọn
        mainContainer.getChildren().stream()
                .filter(node -> node instanceof TitledPane)
                .map(node -> (TitledPane) node)
                .forEach(this::clearTitledPaneCheckboxes);

        // Notify về thay đổi bộ lọc
        if (onFiltersChanged != null) {
            onFiltersChanged.accept(new ArrayList<>());
        }
    }

    private void clearTitledPaneCheckboxes(TitledPane pane) {
        if (pane.getContent() instanceof VBox) {
            VBox content = (VBox) pane.getContent();
            content.getChildren().stream()
                    .filter(node -> node instanceof CheckBox)
                    .map(node -> (CheckBox) node)
                    .forEach(checkbox -> checkbox.setSelected(false));

            content.getChildren().stream()
                    .filter(node -> node instanceof RadioButton)
                    .map(node -> (RadioButton) node)
                    .forEach(radio -> {
                        if (radio.getText().equals("Tất cả")) {
                            radio.setSelected(true);
                        } else {
                            radio.setSelected(false);
                        }
                    });
        }
    }

    /**
     * Thiết lập callback khi các bộ lọc thay đổi
     * @param onFiltersChanged Callback function
     */
    public void setOnFiltersChanged(Consumer<List<PhoneFilter>> onFiltersChanged) {
        this.onFiltersChanged = onFiltersChanged;
    }
}