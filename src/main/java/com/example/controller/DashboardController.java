package com.example.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {

    @FXML private GridPane productGrid; // Khớp với fx:id trong file FXML ở trên

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        int columns = 0;
        int rows = 1;

        try {
            // Giả lập hiển thị 9 sản phẩm lên sàn
            for (int i = 0; i < 9; i++) {
                // 1. Nạp file FXML của thẻ sản phẩm con
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/fxml/ProductCard.fxml"));
                AnchorPane productCard = loader.load();

                // Nếu bạn có viết hàm nhận dữ liệu trong ProductCardController thì truyền vào đây:
                // ProductCardController cardController = loader.getController();
                // cardController.setData(productName, price);

                // 2. Tính toán vị trí tự động xuống dòng sau mỗi 3 sản phẩm (3 cột)
                if (columns == 3) {
                    columns = 0;
                    rows++;
                }

                // 3. Đặt thẻ sản phẩm vào đúng tọa độ (Cột, Hàng) trên lưới
                productGrid.add(productCard, columns++, rows);
            }
        } catch (IOException e) {
            System.err.println("Lỗi nạp file ProductCard.fxml, hãy kiểm tra đường dẫn tài nguyên!");
            e.printStackTrace();
        }
    }
}