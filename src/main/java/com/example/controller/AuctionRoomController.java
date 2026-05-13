package com.example.controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.util.Duration;
import java.net.URL;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class AuctionRoomController implements Initializable {

    @FXML private Label lblCountdown;
    @FXML private Label lblCurrentPrice;
    @FXML private LineChart<String, Number> priceChart;
    @FXML private TextArea txtSystemLog;
    @FXML private TextField txtBidAmount;

    private XYChart.Series<String, Number> priceSeries = new XYChart.Series<>();
    private long currentPriceValue = 25000000;
    private int secondsRemaining = 900; // 15 phút đếm ngược

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // 1. Khởi tạo dữ liệu biểu đồ nền móng ban đầu
        priceSeries.setName("Biến động giá thầu");
        priceSeries.getData().add(new XYChart.Data<>("12:00", 24000000));
        priceSeries.getData().add(new XYChart.Data<>("12:02", 24500000));
        priceSeries.getData().add(new XYChart.Data<>("12:05", currentPriceValue));
        priceChart.getData().add(priceSeries);

        // 2. Chạy đồng hồ đếm ngược luồng đồ họa JavaFX (Timeline)
        Timeline countdownTimeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            if (secondsRemaining > 0) {
                secondsRemaining--;
                int min = secondsRemaining / 60;
                int sec = secondsRemaining % 60;
                lblCountdown.setText(String.format("⏳ Thời gian còn lại: %02d:%02d", min, sec));
            } else {
                lblCountdown.setText("❌ Phiên đấu giá đã kết thúc!");
            }
        }));
        countdownTimeline.setCycleCount(Timeline.INDEFINITE);
        countdownTimeline.play();
    }

    @FXML
    void handlePlaceBid(ActionEvent event) {
        String inputPrice = txtBidAmount.getText().trim();
        if (!inputPrice.isEmpty()) {
            try {
                long bidAmount = Long.parseLong(inputPrice);
                // Kiểm tra luật bước giá tối thiểu (ví dụ phải lớn hơn giá cũ tối thiểu 500k)
                if (bidAmount >= currentPriceValue + 500000) {
                    currentPriceValue = bidAmount;
                    lblCurrentPrice.setText(String.format("%,d đ", currentPriceValue));

                    // Vẽ mốc điểm giá mới lên Chart đồ thị thực tế
                    String curTime = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
                    priceSeries.getData().add(new XYChart.Data<>(curTime, currentPriceValue));

                    // In thông báo ra ô Log nhật ký cho người dùng xem công khai
                    txtSystemLog.appendText(String.format("\n[%s] Bạn đã trả giá: %,d đ thành công!", curTime, bidAmount));
                    txtBidAmount.clear();
                } else {
                    txtSystemLog.appendText("\n[Hệ thống] Giá bạn nhập phải cao hơn giá hiện tại ít nhất 500,000 đ!");
                }
            } catch (NumberFormatException e) {
                txtSystemLog.appendText("\n[Lỗi] Hãy nhập số nguyên hợp lệ, không chứa ký tự hay dấu chấm!");
            }
        }
    }
}