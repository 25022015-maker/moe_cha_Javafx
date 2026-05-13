package com.example.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import java.io.IOException;

public class ProductCardController {

    @FXML
    private Button btnBid; // Khớp với fx:id="btnBid" trong file ProductCard.fxml

    /**
     * Sự kiện khi người dùng click vào nút "Tham Gia" trên thẻ sản phẩm
     */
    @FXML
    void handleBidAction(ActionEvent event) {
        try {
            // 1. Tìm kiếm vùng chứa nội dung trung tâm (StackPane) dựa trên ID "contentArea" đã đặt ở MainLayout
            // Lệnh lookup này giúp dò ngược từ nút bấm hiện tại lên toàn bộ Scene để tìm ra cái khung chứa
            StackPane contentArea = (StackPane) btnBid.getScene().lookup("#contentArea");

            if (contentArea != null) {
                // 2. Định nghĩa chính xác đường dẫn nạp file phòng đấu giá theo cấu trúc thư mục của bạn
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/fxml/AuctionRoom.fxml"));
                Parent auctionRoomView = loader.load();

                // 3. Nếu bạn muốn truyền dữ liệu sản phẩm qua phòng đấu giá (ví dụ: tên, giá hiện tại)
                // AuctionRoomController roomController = loader.getController();
                // roomController.initData(tên_sản_phẩm, giá_sản_phẩm);

                // 4. Xóa màn hình Dashboard cũ ở vùng giữa và thế chỗ bằng giao diện Phòng đấu giá mới
                contentArea.getChildren().clear();
                contentArea.getChildren().add(auctionRoomView);

                System.out.println("Đang chuyển hướng người dùng vào phòng đấu giá trực tuyến...");
            } else {
                System.err.println("Lỗi: Không tìm thấy ID 'contentArea' trong MainLayout để hoán đổi màn hình!");
            }

        } catch (IOException e) {
            System.err.println("Gặp lỗi khi nạp file AuctionRoom.fxml. Vui lòng kiểm tra lại đường dẫn!");
            e.printStackTrace();
        }
    }
}