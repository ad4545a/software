package com.jewellery.erp.app;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import com.jewellery.erp.config.AppConfig;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Initialize Config
        AppConfig.initialize();

        // Basic Foundation Check UI
        Label label = new Label("Jewellery ERP - Phase 0 Foundation");
        StackPane root = new StackPane(label);
        Scene scene = new Scene(root, 800, 600);

        primaryStage.setTitle("Jewellery ERP");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
