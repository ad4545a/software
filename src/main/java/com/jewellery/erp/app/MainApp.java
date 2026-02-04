package com.jewellery.erp.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import com.jewellery.erp.config.AppConfig;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // Initialize Config (DB, Hibernate)
            AppConfig.initialize();

            // Load Login View
            Parent root = FXMLLoader.load(getClass().getResource("/view/login.fxml"));
            Scene scene = new Scene(root);

            primaryStage.setTitle("Jewellery ERP - Login");
            primaryStage.setScene(scene);
            primaryStage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
