package com.demo.photomanage;

import com.demo.photomanage.controller.ImageViewController;
import com.demo.photomanage.controller.MainViewController;
import com.demo.photomanage.utils.GlobalValue;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;

public class ImageMain extends Application {
    private String path;
    private boolean isPlayNow;
    public static void main(String path, MainViewController parentController, boolean isPlayNow){
        assert (Platform.isFxApplicationThread());
        ImageMain imagemain = new ImageMain(path, isPlayNow);
        Stage stage = new Stage();
        // 设置之后主窗口关闭，它也会关
//        if(parentController != null)
//            stage.initOwner(parentController.getStage());
        try {
            imagemain.start(stage);
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    public ImageMain(String path, boolean isPlayNow){
        this.path = path;
        this.isPlayNow = isPlayNow;
    }
    @Override
    public void start(Stage stage) throws Exception{
        FXMLLoader fxmlLoader = new FXMLLoader(ImageMain.class.getResource("ImageView.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 600);
        stage.setScene(scene);
        stage.setMinHeight(400);
        stage.setMinWidth(600);
        stage.setHeight(600);
        stage.setWidth(900);
        stage.getIcons().add(new Image(Main.class.getResource("icon.png").toExternalForm()));
        ImageViewController controller = fxmlLoader.getController();
        if(!isPlayNow) {
            // show了再init可以让stage自适应scene，先init的话stage的宽高都是0，有bug
            stage.show();
            controller.init(stage, path);
        }
        else{
            controller.init(stage, path);
            controller.PlayByMain();
        }
    }

    /**
     * 测试用，单独跑图片窗口
     */
    public static void main(String[] args){
        GlobalValue.initialize();    // 初始化
//        String tpath = "D:\\picture\\jpgs\\0a485fbe-193d-4918-b798-1f6214aaf495.jpg";
        String tpath = "D:\\picture\\mp4s\\1tt.jpg";
        Platform.runLater(()->main(tpath, null, false));
    }
}
