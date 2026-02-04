package com.jewellery.erp.controller;

import com.jewellery.erp.service.AuthService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class DashboardController {

    @FXML
    private Button logoutButton;
    private final AuthService authService = new AuthService();

    @FXML
    private void handleLogout() throws IOException {
        authService.logout();

        Stage stage = (Stage) logoutButton.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("/view/login.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Jewellery ERP - Login");
        stage.centerOnScreen();
    }
}
