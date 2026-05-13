package com.example.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.IOException;

public class LoginRegisterController {

    // Hãy chắc chắn bạn đã đặt fx:id cho 2 ô nhập liệu này trong Scene Builder
    @FXML private TextField txtUsername;
    @FXML private PasswordField txtPassword;

    /**
     * Xử lý sự kiện khi nhấn nút ĐĂNG NHẬP
     * Thực hiện chuyển đổi sang cửa sổ chính (Main.fxml)
     */
    @FXML
    void handleLogin(ActionEvent event) {
        // Lấy dữ liệu người dùng nhập vào
        String username = txtUsername != null ? txtUsername.getText().trim() : "";
        String password = txtPassword != null ? txtPassword.getText().trim() : "";

        // Giả lập kiểm tra dữ liệu không trống (bạn có thể thêm logic xác thực CSDL ở đây)
        if (!username.isEmpty() && !password.isEmpty()) {
            try {
                // 1. Nạp file bố cục tổng thể Main.fxml (chứa Sidebar + vùng nội dung)
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/fxml/MainLayout.fxml"));
                Parent mainRoot = loader.load();

                // 2. Lấy Stage (Cửa sổ ngoài cùng) hiện tại từ sự kiện click
                Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

                // 3. Tạo Scene mới từ Main.fxml và gán vào Stage để chuyển màn hình
                Scene mainScene = new Scene(mainRoot);
                currentStage.setScene(mainScene);

                // Căn giữa lại màn hình sau khi đổi giao diện lớn hơn
                currentStage.centerOnScreen();
                currentStage.show();

                System.out.println("Đăng nhập thành công! Đang chuyển sang Main.fxml...");

            } catch (IOException e) {
                System.err.println("Lỗi: Không tìm thấy hoặc không thể nạp file Main.fxml!");
                e.printStackTrace();
            }
        } else {
            System.out.println("Vui lòng nhập đầy đủ tài khoản và mật khẩu để Đăng nhập!");
        }
    }

    /**
     * Xử lý sự kiện khi nhấn nút ĐĂNG KÝ (khớp với ảnh On Action #handleRegister bạn đang chọn)
     */
    @FXML
    void handleRegister(ActionEvent event) {
        String username = txtUsername != null ? txtUsername.getText().trim() : "";
        String password = txtPassword != null ? txtPassword.getText().trim() : "";

        if (!username.isEmpty() && !password.isEmpty()) {
            // Thực hiện logic thêm tài khoản vào cơ sở dữ liệu của bạn tại đây
            System.out.println("Xử lý Đăng ký hệ thống cho tài khoản: " + username);

            // Xóa sạch ô nhập liệu để thông báo người dùng có thể bấm Đăng nhập ngay
            if (txtUsername != null) txtUsername.clear();
            if (txtPassword != null) txtPassword.clear();

        } else {
            System.out.println("Vui lòng điền tài khoản và mật khẩu để tiến hành Đăng ký mới!");
        }
    }
}