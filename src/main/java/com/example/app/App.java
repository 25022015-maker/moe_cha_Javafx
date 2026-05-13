package com.example.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // 1. Đường dẫn nạp file FXML đầu tiên khi mở ứng dụng lên (Màn hình đăng nhập)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/fxml/LoginRegister.fxml"));
            Parent root = loader.load();

            // 2. Khởi tạo Scene với kích thước khớp form thiết kế
            Scene scene = new Scene(root, 800, 550);

            // 3. Thiết lập thông tin cửa sổ ứng dụng (Stage)
            primaryStage.setTitle("Hệ Thống Đấu Giá Trực Tuyến - JavaFX");
            primaryStage.setScene(scene);
            primaryStage.setResizable(false); // Cố định kích thước màn hình login
            primaryStage.centerOnScreen();    // Hiển thị ứng dụng ngay chính giữa màn hình
            primaryStage.show();              // Kích hoạt hiển thị cửa sổ

        } catch (Exception e) {
            System.err.println("Gặp lỗi nghiêm trọng khi khởi chạy ứng dụng!");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // Hàm kích hoạt toàn bộ vòng đời của một ứng dụng JavaFX
        launch(args);
    }
}