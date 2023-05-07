package com.demo.photomanage.controller;

import com.demo.photomanage.container.PlayData;
import com.demo.photomanage.utils.GenerateDialog;
import com.demo.photomanage.utils.GlobalValue;
import com.demo.photomanage.utils.Tools;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;
import java.io.File;
import java.net.URL;
import java.security.Key;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

import javafx.util.Duration;
import org.controlsfx.control.Notifications;

import static java.lang.Math.*;

public class ImageViewController implements Initializable {
    @FXML
    private BorderPane borderpane;
    @FXML
    private StackPane imagepane;
    @FXML
    private ImageView imageview;
    @FXML
    private HBox buttonbox;
    @FXML
    private Button nextimage;

    @FXML
    private Button playppt;


    @FXML
    private Button previmage;

    @FXML
    private Button zoomin;

    @FXML
    private Button zoomout;

    @FXML
    private Button resetButton;

    private File directory;
    private Stage stage;
    private Image curimage;
    private int curimageidx;
    private ArrayList<File> images = new ArrayList<>();
    private long TimeStamp = 0;
    private final static double GAP = 20;   // 图片和边缘的距离gap/2 好像有问题
    private double imageScale;
    private final static double MAX_SCALE = 1000;
    private final static double MIN_SCALE = 10;
    @Override
    public void initialize(URL location, ResourceBundle resources){}
    public void init(Stage stage, String path){
        this.stage = stage;
        autoAdapt();
//        System.out.println(stage.getWidth());
//        System.out.println(stage.getHeight());
//        System.out.println(stage.widthProperty());
//        System.out.println(stage.heightProperty());
        File file = new File(path);
        directory = file.getParentFile();
        TimeStamp = directory.lastModified();
        curimage = new Image(path);
        images.clear();
        images = Tools.getAvailableImageFiles(directory.listFiles());
        curimageidx = -1;
        for(int i = 0; i < images.size(); i++){
            if(file.equals(images.get(i)))curimageidx = i;
        }
        updateImageView();
        imagepane.setOnKeyPressed(this::normalImagePaneHandle);
        imagepane.requestFocus();

    }
    private void updateImageView(){
//        if(curimage == null){
//            System.out.println("curimage is null");
//        }
        if(images.size() == 0){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("该文件夹下无图片");
            alert.showAndWait();
            stage.close();
        }
        curimage = new Image(images.get(curimageidx).getPath());
        imageview.getTransforms().clear();
        imageScale = 100;
        imageview.setImage(curimage);
        stage.setTitle(Tools.getImageName(curimage));
        double width = curimage.getWidth(), height = curimage.getHeight();
//        System.out.println(imagepane.widthProperty());
//        System.out.println(imagepane.heightProperty());
        // 放得下直接放
        if(width <= imagepane.getWidth()-GAP && height <= imagepane.getHeight()-GAP){
            imageview.fitWidthProperty().bind(imagepane.widthProperty());
            imageview.fitHeightProperty().bind(imagepane.heightProperty());
        }
        else{   // 放不下限制长宽最大大小
            imageview.fitWidthProperty().bind(imagepane.widthProperty().subtract(GAP));
            imageview.fitHeightProperty().bind(imagepane.heightProperty().subtract(GAP));
        }
    }
    /**
     * 更新图片列表，将原图片位置匹配新图片位置(curimageidx 必须是有效的)
     */
    private boolean updateImageArray() {
        if(directory.lastModified() == TimeStamp)return false;
        TimeStamp = directory.lastModified();
        ArrayList<File> upd = Tools.getAvailableImageFiles(directory.listFiles());
        if(upd.equals((images)))return false;
        images = upd;
        // TODO: 用最佳匹配位置代替开头(有点hard)
        curimageidx = 0;
        Notifications.create()
                .text("文件夹发生改变，已回到第一张图片位置")
                .owner(stage)
                .position(Pos.TOP_CENTER)
                .hideAfter(Duration.seconds(5))
                .show();
        return true;
    }

    /**
     * 自适应(may solved)
     */
    private void autoAdapt(){
        // 初始化执行
        borderpane.prefHeightProperty().bind(stage.heightProperty());
        borderpane.prefWidthProperty().bind(stage.widthProperty());
        buttonbox.prefHeightProperty().bind(stage.heightProperty().multiply(0.2));
        buttonbox.prefWidthProperty().bind(stage.widthProperty());
        imagepane.prefWidthProperty().bind(stage.widthProperty());
        imagepane.prefHeightProperty().bind(stage.heightProperty().multiply(0.8));
    }

    @FXML
    private void NextImage() {
        if(!updateImageArray()){
            curimageidx++;
            if (curimageidx == images.size()) {
                curimageidx = 0;
                Notifications.create()
                        .text("这是第一张")
                        .owner(stage)
                        .position(Pos.TOP_CENTER)
                        .hideAfter(Duration.seconds(3))
                        .show();
            }
        }
        updateImageView();
        imagepane.requestFocus();
    }
    @FXML
    private void PrevImage() {
        if(!updateImageArray()){
            curimageidx--;
            if (curimageidx < 0) {
                curimageidx = images.size() - 1;
                Notifications.create()
                        .text("这是最后一张")
                        .owner(stage)
                        .position(Pos.TOP_CENTER)
                        .hideAfter(Duration.seconds(3))
                        .show();
            }
        }
        updateImageView();
        imagepane.requestFocus();
    }

    /**
     * 图片缩放
     * @param x  缩放中心x
     * @param y  缩放中心y
     * @param delta   缩放大小
     */
    private void Zoom(double x, double y, double delta){
        if(delta > 1 && imageScale < MAX_SCALE) {
            double del = (delta-1)*100;
            delta = min(delta, (min(MAX_SCALE, imageScale+del)-imageScale)/100+1);
            imageScale = min(MAX_SCALE, imageScale+del);
            // 放大，中心为(x, y)
            imageview.getTransforms().add(new Scale(delta, delta, x, y));
        }
        else if(delta < 1 && imageScale > MIN_SCALE){
            double del = (1-delta)*100;
            delta = max(delta, 1-(imageScale - max(MIN_SCALE, imageScale-del))/100);
            imageScale = max(MIN_SCALE, imageScale-del);
            // 放大，中心为(x, y)
            imageview.getTransforms().add(new Scale(delta, delta, x, y));
        }
    }
    @FXML
    private void ZoomIn() {
        Zoom(imageview.prefWidth(-1) / 2, imageview.prefHeight(-1) / 2, 1.1);
        imagepane.requestFocus();
    }

    @FXML
    private void ZoomOut() {
        Zoom(imageview.prefWidth(-1) / 2, imageview.prefHeight(-1) / 2, 0.9);
        imagepane.requestFocus();
    }
    @FXML
    private void ResetImage(){
        // 清空缩放位移效果
        imageview.getTransforms().clear();
        imagepane.requestFocus();
    }
    /**
     * 鼠标滚动
     */
    @FXML
    private void ScrollZoom(ScrollEvent e){
        double delta = e.getDeltaY();
//        System.out.println(delta);
        int times = 1;
        if(e.isAltDown())times = 4;
        for(int i = 0; i < times; i++) {
            Zoom(e.getX(), e.getY(), 1 + delta / 320);
        }
//        System.out.println(imageScale);
//        if(delta > 0){
//        }
//        else if(delta < 0){
//            Zoom(e.getX(), e.getY(), 0.9);
//        }
    }

    /**
     * 幻灯片相关
     */
    @FXML
    private void Play() {
        Dialog<PlayData> dialog = GenerateDialog.NewPlayDialog();
        Optional<PlayData> optional = dialog.showAndWait();
        if(optional.isPresent()) {
            PlayData data = optional.get();
            playIt(data.getTime());
        }
    }
    public void PlayByMain(){
        Dialog<PlayData> dialog = GenerateDialog.NewPlayDialog();
        Optional<PlayData> optional = dialog.showAndWait();
        if(optional.isPresent()){
            stage.show();
            stage.setMaximized(false);
            playIt(optional.get().getTime());
        }
        else{
            // 直接结束
            stage.close();
        }
    }
    private void playIt(double gap){
//        boolean oriView = stage.isMaximized();
        stage.setFullScreen(true);  // 全屏
        imagepane.requestFocus();   // 设置焦点
        updateImageView();
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(gap), e->{
            imagepane.requestFocus();
            NextImage();
            updateImageView();
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);    // 循环无限次
        timeline.play();
        Notifications.create()
                .text("方向键可以切换照片")
                .owner(stage)
                .position(Pos.TOP_CENTER)
                .hideAfter(Duration.seconds(5))
                .show();
        imagepane.setOnKeyPressed(e->{
            if(e.getCode() == KeyCode.ESCAPE){
                timeline.stop();
                stage.setFullScreen(false);
                imagepane.setOnKeyPressed(this::normalImagePaneHandle);
            }
            else if(e.getCode() == KeyCode.LEFT){
                timeline.stop();
                PrevImage();
                updateImageView();
                timeline.play();    // 重置计时
            }
            else if(e.getCode() == KeyCode.RIGHT){
                timeline.stop();
                NextImage();
                updateImageView();
                timeline.play();
            }
        });
    }

    private void normalImagePaneHandle(KeyEvent e){
        if(e.getCode() == KeyCode.LEFT){
            PrevImage();
        }
        else if(e.getCode() == KeyCode.RIGHT){
            NextImage();
        }
    }

    /**
     * 鼠标拖动相关
     */
    private double pressX, pressY;
    @FXML
    private void MousePressed(MouseEvent e){
        pressX = e.getX();
        pressY = e.getY();
    }
    @FXML
    private void MouseDragged(MouseEvent e){
        imageview.getTransforms().add(new Translate(e.getX() - pressX, e.getY() - pressY));
    }
}
