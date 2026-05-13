package com.example.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ResourceBundle;

public class CreateAuctionController implements Initializable {

    // --- Khai báo các linh kiện từ file FXML ---
    @FXML private Label lblStatus;
    @FXML private TextField txtProductName;
    @FXML private TextField txtStartPrice;
    @FXML private TextField txtMinBid;
    @FXML private TextArea txtDescription;
    @FXML private ImageView imgPreview;

    @FXML private DatePicker startDatePicker;
    @FXML private Spinner<Integer> startHourSpinner;
    @FXML private Spinner<Integer> startMinSpinner;

    @FXML private DatePicker endDatePicker;
    @FXML private Spinner<Integer> endHourSpinner;
    @FXML private Spinner<Integer> endMinSpinner;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Khởi tạo giới hạn bảo vệ cho các Spinner thời gian ngay khi mở màn hình
        setupTimeSpinners();
    }

    /**
     * Cấu hình khoảng chạy (0-23h cho Giờ và 0-59m cho Phút)
     */
    private void setupTimeSpinners() {
        // Cấu hình thời gian bắt đầu (Mặc định gợi ý: 08 giờ 00 phút)
        startHourSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, 8));
        startMinSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 0));

        // Cấu hình thời gian kết thúc (Mặc định gợi ý: 17 giờ 30 phút)
        endHourSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, 17));
        endMinSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 30));

        // Cho phép người dùng gõ số trực tiếp từ bàn phím cho nhanh
        startHourSpinner.setEditable(true);
        startMinSpinner.setEditable(true);
        endHourSpinner.setEditable(true);
        endMinSpinner.setEditable(true);
    }

    /**
     * Xử lý sự kiện khi bấm nút "Thêm ảnh"
     */
    @FXML
    void handleSelectImage(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Chọn ảnh tài sản đấu giá");

        // Bộ lọc chỉ hiển thị file định dạng hình ảnh
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("File Ảnh (*.png, *.jpg, *.jpeg)", "*.png", "*.jpg", "*.jpeg")
        );

        // Mở cửa sổ duyệt file hệ thống
        Stage currentStage = (Stage) imgPreview.getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(currentStage);

        if (selectedFile != null) {
            // Nạp dữ liệu ảnh và đưa vào khung hiển thị xem trước ImageView
            Image image = new Image(selectedFile.toURI().toString());
            imgPreview.setImage(image);
            System.out.println("Đã tải ảnh lên thành công: " + selectedFile.getName());
        }
    }

    /**
     * Xử lý sự kiện khi bấm nút "KÍCH HOẠT LÊN SÀN"
     */
    @FXML
    void handlePublishAuction(ActionEvent event) {
        // 1. Kiểm tra tính hợp lệ dữ liệu chữ/số cơ bản
        if (txtProductName.getText().trim().isEmpty() || txtStartPrice.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Cảnh báo dữ liệu", "Vui lòng nhập tên tài sản và giá khởi điểm!");
            return;
        }

        // 2. Kiểm tra việc chọn lịch thời gian
        if (startDatePicker.getValue() == null || endDatePicker.getValue() == null) {
            showAlert(Alert.AlertType.WARNING, "Cảnh báo thời gian", "Bạn chưa chọn ngày bắt đầu hoặc ngày kết thúc!");
            return;
        }

        try {
            // Đọc dữ liệu từ các ô nhập số
            double startPrice = Double.parseDouble(txtStartPrice.getText().trim());
            double minBid = Double.parseDouble(txtMinBid.getText().trim());

            // Gộp ngày (DatePicker) và Giờ:Phút (Spinner) thành mốc thời gian hoàn chỉnh
            LocalDateTime startAuctionDateTime = LocalDateTime.of(
                    startDatePicker.getValue(),
                    LocalTime.of(startHourSpinner.getValue(), startMinSpinner.getValue())
            );

            LocalDateTime endAuctionDateTime = LocalDateTime.of(
                    endDatePicker.getValue(),
                    LocalTime.of(endHourSpinner.getValue(), endMinSpinner.getValue())
            );

            // Kiểm tra logic: Thời gian khóa sổ không được phép trước thời gian mở phòng
            if (endAuctionDateTime.isBefore(startAuctionDateTime) || endAuctionDateTime.isEqual(startAuctionDateTime)) {
                showAlert(Alert.AlertType.ERROR, "Lỗi thời gian", "Thời gian kết thúc phiên phải sau thời gian bắt đầu!");
                return;
            }

            // 3. Nếu mọi điều kiện thỏa mãn thành công:
            // Cập nhật nhãn trạng thái từ "Bản nháp" thành "Đã kích hoạt"
            lblStatus.setText("Trạng thái: ĐÃ KÍCH HOẠT");
            lblStatus.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-padding: 5 15; -fx-background-radius: 15; -fx-font-weight: bold;");

            showAlert(Alert.AlertType.INFORMATION, "Thành công", "Tài sản đấu giá đã được đẩy lên hệ thống sàn thành công!");

            // In kiểm tra hệ thống log
            System.out.println("Phiên đấu giá mới đã hoạt động: " + txtProductName.getText());

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Lỗi định dạng", "Giá khởi điểm và bước giá bắt buộc phải nhập dạng số nguyên/số thập phân!");
        }
    }

    /**
     * Hàm tiện ích bật hộp thoại cảnh báo nhanh
     */
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}