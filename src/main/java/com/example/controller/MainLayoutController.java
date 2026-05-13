package com.example.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainLayoutController implements Initializable {

    @FXML private StackPane contentArea;
    @FXML private Label lblSidebarNotification;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Vừa đăng nhập thành công: Mặc định nạp thẳng giao diện danh sách sản phẩm (Dashboard)
        loadSubView("/com/example/fxml/dashboard.fxml");
        lblSidebarNotification.setText("Hệ thống: Chào mừng bạn tham gia đấu giá!");
    }

    // Hàm dùng chung để hoán đổi màn hình con trong vùng Center
    private void loadSubView(String fxmlPath) {
        try {
            Parent view = FXMLLoader.load(getClass().getResource(fxmlPath));
            contentArea.getChildren().clear();
            contentArea.getChildren().add(view);
        } catch (IOException e) {
            System.err.println("Không thể nạp view con: " + fxmlPath);
            e.printStackTrace();
        }
    }

    @FXML void onDashboardClick(ActionEvent event) { loadSubView("/com/example/fxml/dashboard.fxml"); }

    @FXML void onCreateAuctionClick(ActionEvent event) { loadSubView("/com/example/fxml/CreateAuction.fxml"); }

    @FXML
    void onLogoutClick(ActionEvent event) {
        try {
            Parent loginRoot = FXMLLoader.load(getClass().getResource("/com/example/fxml/loginORregister.fxml"));
            Stage stage = (Stage) contentArea.getScene().getWindow();
            stage.setScene(new Scene(loginRoot));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}