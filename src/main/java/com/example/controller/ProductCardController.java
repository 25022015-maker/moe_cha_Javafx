package com.example.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ProductCardController {
    @FXML private ImageView imgProduct;
    @FXML private Label lblProductName;
    @FXML private Label lblProductPrice;
    @FXML private Label lblStatus;

    // Hàm nhận dữ liệu từ Dashboard để đổ vào giao diện Card
    public void setData(String name, String price, String imagePath) {
        lblProductName.setText(name);
        lblProductPrice.setText(price);
        try {
            imgProduct.setImage(new Image(getClass().getResourceAsStream(imagePath)));
        } catch (Exception e) {
            System.out.println("Không tìm thấy ảnh sản phẩm!");
        }
    }

    @FXML
    void handleBidAction() {
        System.out.println("Đã kích hoạt hành động đặt giá cho: " + lblProductName.getText());
    }
}