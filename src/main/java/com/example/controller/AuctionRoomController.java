package com.example.controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.util.Duration;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class AuctionRoomController {

    // --- Khai báo linh kiện chung ---
    @FXML private Label lblTitle;
    @FXML private Label lblTimeRemaining;
    @FXML private LineChart<String, Number> priceChart;
    @FXML private TextArea txtAreaLog;

    // --- Khu vực Đặt giá thủ công ---
    @FXML private Label lblCurrentPrice;
    @FXML private Label lblMinBidStep;
    @FXML private TextField txtBidInput;
    @FXML private Label lblManualMessage; // Thông báo handlePlacedBid

    // --- Khu vực Đặt giá tự động ---
    @FXML private TextField txtAutoMaxPrice;
    @FXML private TextField txtAutoBidStep;
    @FXML private CheckBox chkEnableAutoBid;
    @FXML private Label lblAutoMessage; // Thông báo auto-bid

    // --- Các biến bổ trợ xử lý logic ---
    private ProductModel activeProduct;
    private XYChart.Series<String, Number> chartDataSeries = new XYChart.Series<>();
    private Timeline clockTimeline;

    /**
     * Nhận và khởi động dữ liệu phòng đấu giá từ Dashboard/Card truyền sang
     */
    public void initAuctionData(ProductModel product) {
        this.activeProduct = product;

        // 1. Gắn dữ liệu lên giao diện hiển thị
        lblTitle.setText(product.getName());
        updatePriceDisplay();
        lblMinBidStep.setText(String.format("+ %,.0f đ", product.getMinBid()));

        // Xóa sạch các ô thông báo ẩn ban đầu
        lblManualMessage.setText("");
        lblAutoMessage.setText("");

        // 2. Cài đặt biểu đồ nhảy giá
        chartDataSeries.setName("Biến động giá");
        priceChart.getData().clear();
        priceChart.getData().add(chartDataSeries);
        addPricePointToChart(product.getCurrentPrice());

        // 3. Ghi log khởi tạo
        txtAreaLog.setText("[Hệ thống] Phòng đấu giá mở công khai. Giá khởi điểm: " + formatCurrency(product.getCurrentPrice()));

        // 4. Khởi chạy bộ đếm ngược
        startLiveClock();
    }

    /**
     * Cập nhật số tiền hiển thị trên màn hình
     */
    private void updatePriceDisplay() {
        lblCurrentPrice.setText(formatCurrency(activeProduct.getCurrentPrice()));
    }

    private String formatCurrency(double amount) {
        return String.format("%,.0f đ", amount);
    }

    private void addPricePointToChart(double price) {
        String timestamp = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        chartDataSeries.getData().add(new XYChart.Data<>(timestamp, price));
    }

    /**
     * Logic đếm ngược thời gian thực (Mỗi giây lặp 1 lần)
     */
    private void startLiveClock() {
        if (clockTimeline != null) clockTimeline.stop();

        clockTimeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            int remaining = activeProduct.getSecondsRemaining();
            if (remaining > 0) {
                activeProduct.setSecondsRemaining(remaining - 1);
                int hour = remaining / 3600;
                int min = (remaining % 3600) / 60;
                int sec = remaining % 60;
                lblTimeRemaining.setText(String.format("⏳ Thời gian còn lại: %02d:%02d:%02d", hour, min, sec));
            } else {
                lblTimeRemaining.setText("❌ Phiên đấu giá đã kết thúc!");
                txtBidInput.setDisable(true);
                chkEnableAutoBid.setSelected(false);
                chkEnableAutoBid.setDisable(true);
                clockTimeline.stop();
            }
        }));
        clockTimeline.setCycleCount(Timeline.INDEFINITE);
        clockTimeline.play();
    }

    /**
     * Chức năng 1: Xử lý ĐẶT GIÁ THẦU (Thủ công)
     */
    @FXML
    void handlePlaceBid(ActionEvent event) {
        lblManualMessage.setText(""); // Reset thông báo cũ
        String input = txtBidInput.getText().trim();

        if (input.isEmpty()) {
            setErrorMessage(lblManualMessage, "Vui lòng nhập số tiền muốn thầu!");
            return;
        }

        try {
            double bidAmount = Double.parseDouble(input);
            double nextValidPrice = activeProduct.getCurrentPrice() + activeProduct.getMinBid();

            if (bidAmount < nextValidPrice) {
                setErrorMessage(lblManualMessage, "Giá thầu phải lớn hơn hoặc bằng: " + formatCurrency(nextValidPrice));
                return;
            }

            // Đặt thầu thành công
            executeNewBid(bidAmount, "Bạn");
            setSuccessMessage(lblManualMessage, "Gửi giá thầu thành công!");
            txtBidInput.clear();

            // Kích hoạt bot tự động cạnh tranh ngay sau khi bạn đặt giá (Nếu giả lập hệ thống)
            checkAndTriggerAutoBid();

        } catch (NumberFormatException e) {
            setErrorMessage(lblManualMessage, "Vui lòng chỉ nhập số ký tự nguyên!");
        }
    }

    /**
     * Chức năng 2: Bật/Tắt chế độ NÂNG CAO (Tự động đặt giá)
     */
    @FXML
    void handleToggleAutoBid(ActionEvent event) {
        lblAutoMessage.setText("");

        if (chkEnableAutoBid.isSelected()) {
            try {
                double maxPrice = Double.parseDouble(txtAutoMaxPrice.getText().trim());
                double step = Double.parseDouble(txtAutoBidStep.getText().trim());

                if (maxPrice <= activeProduct.getCurrentPrice()) {
                    setErrorMessage(lblAutoMessage, "Giá tối đa phải lớn hơn giá hiện tại!");
                    chkEnableAutoBid.setSelected(false);
                    return;
                }
                if (step < activeProduct.getMinBid()) {
                    setErrorMessage(lblAutoMessage, "Bước nhảy không được nhỏ hơn bước giá sàn!");
                    chkEnableAutoBid.setSelected(false);
                    return;
                }

                setSuccessMessage(lblAutoMessage, "Đã kích hoạt hệ thống tự động đặt giá.");
                txtAutoMaxPrice.setDisable(true);
                txtAutoBidStep.setDisable(true);

                // Kiểm tra xem có cần tự động nâng giá ngay lập tức không
                checkAndTriggerAutoBid();

            } catch (NumberFormatException e) {
                setErrorMessage(lblAutoMessage, "Nhập sai định dạng cấu hình!");
                chkEnableAutoBid.setSelected(false);
            }
        } else {
            // Tắt chế độ
            txtAutoMaxPrice.setDisable(false);
            txtAutoBidStep.setDisable(false);
            setSuccessMessage(lblAutoMessage, "Đã hủy tự động đặt giá.");
        }
    }

    /**
     * Vận hành lõi xử lý Đấu giá tự động nâng cao
     */
    private void checkAndTriggerAutoBid() {
        if (!chkEnableAutoBid.isSelected()) return;

        double maxPrice = Double.parseDouble(txtAutoMaxPrice.getText().trim());
        double step = Double.parseDouble(txtAutoBidStep.getText().trim());
        double nextPriceNeeded = activeProduct.getCurrentPrice() + activeProduct.getMinBid();

        // Nếu giá hiện tại của người khác chiếm ưu thế và hệ thống tự động của bạn vẫn nằm trong ngân sách chịu đựng
        if (nextPriceNeeded <= maxPrice) {
            // Tự động tính toán đẩy giá lên một bước nhảy cấu hình sẵn
            double autoBidTarget = activeProduct.getCurrentPrice() + step;
            if (autoBidTarget > maxPrice) {
                autoBidTarget = nextPriceNeeded; // Nếu vượt quá max thì dùng mức tối thiểu để tiết kiệm
            }

            executeNewBid(autoBidTarget, "Hệ thống tự động (Bạn)");
            setSuccessMessage(lblAutoMessage, "Hệ thống vừa tự động nâng giá cho bạn.");
        } else {
            setErrorMessage(lblAutoMessage, "Giá sàn vượt quá giới hạn tối đa của bạn. Dừng bot!");
            chkEnableAutoBid.setSelected(false);
            txtAutoMaxPrice.setDisable(false);
            txtAutoBidStep.setDisable(false);
        }
    }

    /**
     * Đồng bộ hóa việc ghi nhận mức giá kỷ lục mới
     */
    private void executeNewBid(double finalAmount, String bidderName) {
        activeProduct.setCurrentPrice(finalAmount);
        updatePriceDisplay();
        addPricePointToChart(finalAmount);

        String timeStr = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        txtAreaLog.appendText(String.format("\n[%s] -> %s đã trả mức giá: %s", timeStr, bidderName, formatCurrency(finalAmount)));
    }

    // --- Các hàm tiện ích đổi màu sắc chữ thông báo nhanh ---
    private void setErrorMessage(Label label, String msg) {
        label.setText("❌ " + msg);
        label.setStyle("-fx-text-fill: #e74c3c; -fx-font-size: 13px;");
    }

    private void setSuccessMessage(Label label, String msg) {
        label.setText("✔️ " + msg);
        label.setStyle("-fx-text-fill: #2ecc71; -fx-font-size: 13px;");
    }
}