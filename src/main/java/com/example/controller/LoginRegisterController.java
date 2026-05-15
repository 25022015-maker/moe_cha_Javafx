package com.example.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class LoginRegisterController {

    @FXML private TextField txtUsername;
    @FXML private PasswordField txtPassword;

    // 2 Nhãn hiển thị thông báo động theo ảnh thiết kế mới của bạn
    @FXML private Label lblLoginMessage;
    @FXML private Label lblRegisterMessage;

    // Giả lập một Database cơ sở dữ liệu dạng Map để lưu tài khoản chạy thật trong bộ nhớ app
    private static final Map<String, String> userDatabase = new HashMap<>();

    static {
        // Tài khoản hệ thống mặc định sẵn có
        userDatabase.put("admin", "123456");
        userDatabase.put("user1", "password");
    }

    /**
     * NÚT ĐĂNG NHẬP: Có 2 trường hợp chính
     */
    @FXML
    void handleLogin(ActionEvent event) {
        // Reset thông báo cũ
        clearMessages();

        String username = txtUsername.getText().trim();
        String password = txtPassword.getText().trim();

        // Kiểm tra validation cơ bản
        if (username.isEmpty() || password.isEmpty()) {
            showError(lblLoginMessage, "Tài khoản/Mật khẩu không được để trống!");
            return;
        }

        // TRƯỜNG HỢP 1: Tài khoản không tồn tại hoặc Sai mật khẩu (Gộp chung thành thông báo Bảo mật)
        if (!userDatabase.containsKey(username) || !userDatabase.get(username).equals(password)) {
            showError(lblLoginMessage, "Tài khoản không tồn tại hoặc sai mật khẩu!");
            return;
        }

        // TRƯỜNG HỢP 2: Đăng nhập thành công -> Hiện chữ xanh và chuyển màn hình Main Layout
        showSuccess(lblLoginMessage, "Đăng nhập thành công! Đang chuyển hướng...");

        // Tạo hiệu ứng trễ nhỏ hoặc chuyển luôn sang giao diện chính
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/fxml/MainLayout.fxml"));
            Parent mainRoot = loader.load();

            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene mainScene = new Scene(mainRoot);
            currentStage.setScene(mainScene);
            currentStage.centerOnScreen();
            currentStage.show();
        } catch (IOException e) {
            showError(lblLoginMessage, "Lỗi hệ thống: Không thể nạp MainLayout.fxml!");
            e.printStackTrace();
        }
    }

    /**
     * NÚT ĐĂNG KÝ: Có 2 trường hợp chính
     */
    @FXML
    void handleRegister(ActionEvent event) {
        // Reset thông báo cũ
        clearMessages();

        String username = txtUsername.getText().trim();
        String password = txtPassword.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            showError(lblRegisterMessage, "Vui lòng điền đủ thông tin để đăng ký!");
            return;
        }

        // TRƯỜNG HỢP 1: Tài khoản đã tồn tại trên sàn hệ thống
        if (userDatabase.containsKey(username)) {
            showError(lblRegisterMessage, "Đăng ký thất bại! Tài khoản này đã tồn tại.");
            return;
        }

        // TRƯỜNG HỢP 2: Đăng ký thành công -> Đút vào DB và ép tiến thẳng vào trạng thái Đăng Nhập luôn
        userDatabase.put(username, password);

        showSuccess(lblRegisterMessage, "Đăng ký thành công! Hệ thống tự động đăng nhập...");

        // Tiến hành tự động đăng nhập trực tiếp luôn cho người dùng
        txtUsername.setText(username);
        txtPassword.setText(password);

        // Gọi trực tiếp hàm đăng nhập luôn giúp người dùng không phải bấm lại nút Đăng Nhập lần nữa
        handleLogin(event);
    }

    // --- Các hàm tiện ích trang trí màu chữ thông báo nhanh ---

    private void clearMessages() {
        if (lblLoginMessage != null) lblLoginMessage.setText("");
        if (lblRegisterMessage != null) lblRegisterMessage.setText("");
    }

    private void showError(Label label, String text) {
        if (label != null) {
            label.setText("❌ " + text);
            label.setStyle("-fx-text-fill: #e74c3c; -fx-font-size: 13px; -fx-font-weight: bold;"); // Chữ đỏ khi lỗi
        }
    }

    private void showSuccess(Label label, String text) {
        if (label != null) {
            label.setText("✔️ " + text);
            label.setStyle("-fx-text-fill: #2ecc71; -fx-font-size: 13px; -fx-font-weight: bold;"); // Chữ xanh khi thành công
        }
    }
}