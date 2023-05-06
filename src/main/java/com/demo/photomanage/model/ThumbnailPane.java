package com.demo.photomanage.model;

import com.demo.photomanage.ImageMain;
import com.demo.photomanage.controller.MainViewController;
import com.demo.photomanage.utils.GenerateDialog;
import com.demo.photomanage.utils.Tools;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;

import java.io.File;
import java.util.Optional;

// https://www.yiibai.com/javafx/javafx_borderpane.html
public class ThumbnailPane extends BorderPane {
    private ImageView imageView;
    private double actualWidth;
    private double actualHeight;
    private File imageFile;
    private Text imageName;
    private TextField textField;    // discarded
    private boolean isSelect;

    private static final double SIZE = 100;  // 缩略图大小 (小于加载图片的大小)
    private static final int MAX_LEN = 17;  // 显示文字长度
    private static final Insets INSETS = new Insets(5, 5, 0, 5);
    private MainViewController mainController;

    public ThumbnailPane(File file, MainViewController mainViewController){
        this.setCache(false);
        this.setMaxSize(SIZE + 10, SIZE + 50);
        this.setMinSize(SIZE + 10, SIZE + 50);
        this.setPadding(INSETS);    //设置边距

        imageFile = file;
        mainController = mainViewController;
        Thread thread = new Thread(()->{
            Image actualImage = new Image(file.getPath());
            actualWidth = actualImage.getWidth();
            actualHeight = actualImage.getHeight();
//            System.out.println("OK");
        });
        // Image(路径，宽，高，保持比例，smooth，后台加载(开了无图(solved)))
        // 图片比例好像还是有问题(solved)
        Platform.runLater(()->{
            imageView = new ImageView(new Image(file.toPath().toUri().toString(), 120, 120, true, true, true));
//            imageView = new ImageView(new Image(file.toPath().toUri().toString()));
            imageView.setFitWidth(SIZE);
            imageView.setFitHeight(SIZE);
            imageView.setPreserveRatio(true);
            this.setCenter(imageView);
            thread.start();
        });
        imageName = new Text(Tools.shortenString(file.getName(), MAX_LEN));
        BorderPane.setAlignment(imageName, Pos.CENTER); // 文字居中
        // 设置容器大小
        this.setPrefSize(SIZE+30, SIZE+50);
        this.setBottom(imageName);

        textField = new TextField();
        textField.setMaxHeight(40);
//        textField.setWrapText(true);    // 自动换行
        textField.setDisable(true);
        // 鼠标点击事件
        setOnMouseClicked(e->{
            myFlowPane father = (myFlowPane) this.getParent();  // 获取他爹
            // 左键单击
            if(e.getButton() == MouseButton.PRIMARY && e.getClickCount() == 1){
                if(e.isControlDown()) {     // 按下Ctrl
                    if(getisSelect())father.unSelect(this);
                    else father.Select(this);
                }
                else {
                    father.unSelectAll();   //清空选择
                    father.Select(this);
                }
                father.updateTipInfoLabel();
            }
            else if(e.getButton() == MouseButton.PRIMARY && e.getClickCount() == 2){
                Platform.runLater(()->ImageMain.main(imageFile.getAbsolutePath(), mainController, false));
            }
        });

    }

    public void Select(){
        this.setStyle("-fx-background-color: #cce8ff");
        isSelect = true;
    }
    public void unSelect(){
        this.setStyle("-fx-background-color: transparent");
        isSelect = false;
    }
    public boolean getisSelect(){return isSelect;}
    public File getImageFile(){return imageFile;}
    public Image getImage(){return imageView.getImage();}
    public long length(){return getImageFile().length();}

    /**
     * 单独对当前重命名
     */
    public void rename(){
        Dialog<String> dialog = GenerateDialog.NewOneRenameDialog(imageFile.getName());
        Optional<String> option = dialog.showAndWait();
        option.ifPresent(s -> {
            renameOneFile(s, true);
            mainController.refresh();
        });
    }
    public int renameOneFile(String destName, boolean isInform){
        if(destName.equals(imageFile.getName()))    // 名字没变直接返回
            return 0;
        File dest = new File(imageFile.getParentFile().getAbsolutePath() + "\\" + destName);
        if(dest.exists()){  // 该名字已存在
            if(isInform) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText("该文件名已存在");
                alert.showAndWait();
            }
            return 1;
        }
        if(!imageFile.renameTo(dest)){
            if(isInform){
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText("重命名失败");
                alert.showAndWait();
            }
            return 1;
        }
        if(isInform){
            Notifications.create()
                    .text("重命名成功")
                    .owner(mainController.getStage())
                    .position(Pos.TOP_CENTER)
                    .hideAfter(Duration.seconds(3))
                    .show();
        }
        return 0;
    }
    public double getActualWidth(){return actualWidth;}
    public double getActualHeight(){return actualHeight;}
}
