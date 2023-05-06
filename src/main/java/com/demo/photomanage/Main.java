package com.demo.photomanage;

import com.demo.photomanage.controller.MainViewController;
import com.demo.photomanage.utils.GlobalValue;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

/**
 * 由于ROOT_FILE，仅适用于Windows(见GlobalValue)
 */
public class Main extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("MainView.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1100, 700);
        stage.setMinHeight(400);
        stage.setMinWidth(700);
        stage.setTitle("图片管理系统");
        stage.getIcons().add(new Image(Main.class.getResource("icon.png").toExternalForm()));
        stage.setScene(scene);
        MainViewController controller = fxmlLoader.getController();
        controller.setStage(stage);
        stage.show();
    }

    public static void main(String[] args) {
        GlobalValue.initialize();    // 初始化
//        System.out.println(System.currentTimeMillis());
        launch();
    }
}

