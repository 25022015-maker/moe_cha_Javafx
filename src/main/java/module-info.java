module com.example.app {
    // 1. Khai báo bắt buộc các thành phần cốt lõi của JavaFX
    requires javafx.controls;
    requires javafx.fxml;

    // Khai báo module cơ sở của Java (luôn có sẵn)
    requires java.base;
    // Khai báo module liên quan đến database :vvv
    requires java.sql;

    // 2. CẤP QUYỀN TRUY CẬP (Rất quan trọng):

    // Mở quyền package "com.example.app" cho JavaFX để class App có thể khởi chạy
    opens com.example.app to javafx.fxml;

    // Mở quyền package "com.example.controller" để cơ chế FXMLLoader của JavaFX
    // có thể ánh xạ (Reflection) và tương tác với các nút bấm, ô nhập liệu... trong các Controller của bạn
    opens com.example.controller to javafx.fxml;

    // 3. XUẤT PACKAGE RA NGOÀI:
    // Cho phép hệ thống biên dịch và JVM nhìn thấy lớp khởi chạy chính của ứng dụng
    exports com.example.app;
}