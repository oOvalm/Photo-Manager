package com.demo.photomanage.controller;

import com.demo.photomanage.ImageMain;
import com.demo.photomanage.container.RenameData;
import com.demo.photomanage.things.*;
import com.demo.photomanage.utils.GenerateDialog;
import com.demo.photomanage.utils.Tools;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.util.*;

import static com.demo.photomanage.utils.GlobalValue.ROOT_FILE;

public class MainViewController {
    @FXML
    private VBox Mainvbox;

    @FXML
    private TreeView<String> TreeViewFile;

    @FXML
    private Button backButton;

    @FXML
    private Button forwardButton;

    @FXML
    private TextField pathText;

    @FXML
    private ScrollPane previewScrollPane;

    @FXML
    private AnchorPane root;

    @FXML
    private SplitPane splitpane1;

    @FXML
    private Button upwardButton;

    @FXML
    private AnchorPane upperPane;

    @FXML
    private HBox uprightHbox;

    @FXML
    private Label TipInfoLabel;

    @FXML
    private AnchorPane previewAnchorPane;

    @FXML
    private HBox bottomBox;

    private myFlowPane previewFlowPane;
    private ContextMenu menu = new ContextMenu();

    public MainViewController(){}
    @FXML
    public void initialize(){
        initPreviewPane();      // 右侧布局
        initTreeViewFile();     // 目录树
        initMenu();             // 鼠标右键菜单
        autoAdapt();    // 自适应布局
        Handler();
        updateUpperButtons();
        initPathText();
        Platform.runLater(()->root.requestFocus());
    }

    /**
     * 目录树初始化
     */
    private void initTreeViewFile(){
        myTreeItem treeItem = new myTreeItem(ROOT_FILE);
        TreeViewFile.setRoot(treeItem);
        // 鼠标事件（另一种）
//        TreeViewFile.setOnMouseClicked(new EventHandler<MouseEvent>()
//        {
//            @Override
//            public void handle(MouseEvent mouseEvent)
//            {
//                if(mouseEvent.getClickCount() == 2)
//                {
//                    TreeItem<String> item = TreeViewFile.getSelectionModel().getSelectedItem();
//                    System.out.println("Selected Text : " + item.getValue());
//                }
//            }
//        });
    }

    /**
     *预览模块初始化
     */
    private void initPreviewPane(){
        previewFlowPane = new myFlowPane(this, previewScrollPane);
        previewAnchorPane.getChildren().add(previewFlowPane);      // 把FlowPane加到ScrollPane中
        previewAnchorPane.getChildren().add(previewFlowPane.getRectangle());
        previewFlowPane.setPadding(new Insets(10,10,10,10));    // 设置控件之间的间隔
//        previewFlowPane.setPadding(new Insets(10));
    }

    /**
     * 自适应布局(solved)
     * 用属性绑定或监听来做
     * https://blog.csdn.net/qq_26486347/article/details/96714424
     */
    private void autoAdapt(){
        Mainvbox.prefHeightProperty().bind(root.heightProperty());
        Mainvbox.prefWidthProperty().bind(root.widthProperty());


        previewFlowPane.heightProperty().addListener((observable, oldValue, newValue) -> {
            previewAnchorPane.setMinHeight(previewFlowPane.getHeight());
        });

        previewScrollPane.viewportBoundsProperty().addListener(((observableValue, oldValue, newValue) -> {
            previewFlowPane.setPrefWidth(newValue.getWidth());
            if (previewFlowPane.getHeight() < previewScrollPane.getHeight()) {
                previewFlowPane.setPrefHeight(previewScrollPane.getHeight());
            }
        }));

        root.heightProperty().addListener((observable, oldValue, newValue) -> {
            double tmp = upperPane.getPrefHeight() + bottomBox.getPrefHeight();
            splitpane1.setPrefHeight(newValue.doubleValue() - tmp);
        });
        root.widthProperty().addListener((observable, oldValue, newValue) -> {
            double width = newValue.doubleValue();

            // 顶部文本框那行
            upperPane.setPrefWidth(width);
            pathText.setPrefWidth(Math.min(pathText.getMaxWidth(), width-3*backButton.getPrefWidth()-80));

        });
    }

    private void initMenu(){
        MenuItem copyButton = new MenuItem("复制");
        MenuItem pasteButton = new MenuItem("粘贴");
        MenuItem refreshButton = new MenuItem("刷新");
        MenuItem deleteButton = new MenuItem("删除");
        MenuItem renameButton = new MenuItem("重命名");
        MenuItem playAllButton = new MenuItem("播放所有图片");
        // 如果此处有增删改，查一下menu.getItems()，下面好像有直接访问下标的，要对上。
        menu.getItems().addAll(copyButton, pasteButton, refreshButton, deleteButton, renameButton, playAllButton);
        // 都写成成员函数(不要写lambda，要复用)
        copyButton.setOnAction(e->CopyImage());
        pasteButton.setOnAction(e->PasteImage());
        refreshButton.setOnAction(e->refresh());
        deleteButton.setOnAction(e->DeleteImage());
        renameButton.setOnAction(e->RenameImage());
        playAllButton.setOnAction(e-> play());
    }

    /**
     * 控制器，监听
     */
    private void Handler(){
        // 左侧目录树
        TreeViewFile.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            chooseDirectory(oldValue, newValue);
            updateUpperButtons();
        });

        // 右侧缩略图 (鼠标按下触发)  在myFlowPane中
//        previewFlowPane.setOnMousePressed(e->{
//            Node clickNode = e.getPickResult().getIntersectedNode();     // 获取鼠标点到的东西
//            if(e.getButton() == MouseButton.PRIMARY){   // 左键
//                // 没点到缩略图
//                if(!(clickNode instanceof ImageView || clickNode instanceof ThumbnailPane)){
//                    previewFlowPane.unSelectAll();
//                    updateTipInfoLabel();
//                }
//            }
//        });

        // 右侧缩略图 (按键盘触发，监听FlowPane无效(why))
        // FlowPane的setOnMousePressed在myFlowPane中
        // 放到initPreviewPane好像也合理
        previewScrollPane.setOnKeyPressed(e->{
//            System.out.println(e.getCode());
            if(e.isControlDown()){
                if(e.getCode() == KeyCode.A){
                    previewFlowPane.SelectAll();
                    updateTipInfoLabel();
                }
                else if(e.getCode() == KeyCode.C){
                    CopyImage();
                }
                else if(e.getCode() == KeyCode.V){
                    PasteImage();
                }
            }
            else if(e.getCode() == KeyCode.F5){
                refresh();
            }
            else if(e.getCode() == KeyCode.DELETE){
                DeleteImage();
            }
        });
//        System.out.println("Handler loaded");
    }

    private void chooseDirectory(TreeItem<String> oldValue, TreeItem<String> newValue){
        if (newValue != null) {     // 可能不用判？
//                System.out.println(newValue.getClass());
//                System.out.println("select: " + newValue.getValue());
            pathText.setText(((myTreeItem)newValue).getFile().getPath());   // 更新顶部文本
            pathTextCurrent = pathText.getText();
            previewFlowPane.UpdateView(((myTreeItem)newValue).getFile());   // 更新右侧缩略图
            // 维护前进后退栈信息
            backStack.push((myTreeItem) newValue);
            foreStack.clear();
        }
    }

    /**
     * 更新底部标签
     */
    public void updateTipInfoLabel(int total, String totalsize, int select, String selectsize, double... size){
        if(select == 0)
            TipInfoLabel.setText(String.format("%d张照片 (%s)", total, totalsize));
        else if(select == 1) {
            assert (size.length == 2);
            TipInfoLabel.setText(String.format("%d张照片 (%s)\t选中 %d 张图片 \t%s (%.0fpx ×%.0fpx)",
                                        total, totalsize, select, selectsize, size[0], size[1]));
        }
        else
            TipInfoLabel.setText(String.format("%d张照片 (%s)\t已选择 %d 张图片 (%s)", total, totalsize, select, selectsize));
    }
    public void updateTipInfoLabel() {
        int selectsize = previewFlowPane.getChoseImages().size();
        if(selectsize != 1)
            updateTipInfoLabel(previewFlowPane.getImageNum(), previewFlowPane.getImageSize(),
                    previewFlowPane.getChoseImageNum(), previewFlowPane.getChoseImageSize());
        else {
            double width = previewFlowPane.getChoseImages().get(0).getActualWidth();
            double height = previewFlowPane.getChoseImages().get(0).getActualHeight();
            updateTipInfoLabel(previewFlowPane.getImageNum(), previewFlowPane.getImageSize(),
                    previewFlowPane.getChoseImageNum(), previewFlowPane.getChoseImageSize(), width, height);
        }
    }

    /**
     * 鼠标右键菜单有关的
     */
    public void ShowMenu(double x, double y){
        updateMenuButtonAble();
        // 右键菜单坐标没对，做好窗口自适应再调(solved)
        // TODO:(add)当鼠标靠近窗口右侧(下侧)右键时菜单出现在鼠标左侧(上侧)
        menu.show(previewAnchorPane, x, y);
    }
    public void updateMenuButtonAble(){
        if(previewFlowPane.getChoseImageNum() > 0){
            for(MenuItem item:menu.getItems()){
                item.setDisable(false);
            }
        }
        else{
            for(MenuItem item:menu.getItems()){
                item.setDisable(true);
            }
        }
        // 通过剪切板有无内容判断"粘贴"要不要disable
        menu.getItems().get(1).setDisable(!Clipboard.getSystemClipboard().hasFiles());
        menu.getItems().get(2).setDisable(false);   // 刷新
        menu.getItems().get(5).setDisable(false);   // 播放图片
    }
    public void HideMenu(){menu.hide();}
    public void refresh(){previewFlowPane.UpdateView(previewFlowPane.getCurrentDirectory());}
    public void CopyImage(){
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        ArrayList<File> files = new ArrayList<>();
        for(ThumbnailPane img:previewFlowPane.getChoseImages()){
            files.add(img.getImageFile());
        }
        content.putFiles(files);
        clipboard.setContent(content);
    }
    public void PasteImage(){
        Clipboard clipboard = Clipboard.getSystemClipboard();
        if(!clipboard.hasFiles())return;
        Set<String> nameset = previewFlowPane.getNameset();

        List<File> files = clipboard.getFiles();
        for(File f:files) if(!f.isDirectory()){
            String newname = Tools.getAvailableName(f, nameset);
            File target = new File(previewFlowPane.getCurrentDirectory() + "\\" + newname);
//            System.out.println(target.getName());
            try {
                Files.copy(f.toPath(), target.toPath());
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        refresh();
    }
    public void DeleteImage(){
        ArrayList<ThumbnailPane> images = previewFlowPane.getChoseImages();
        if(images.size() == 0)return;
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "",
                            ButtonType.YES, ButtonType.NO);
        alert.setHeaderText("确认删除这"+images.size()+"张图片吗？");
        var res = alert.showAndWait();
        if(res.isPresent() && res.get() == ButtonType.YES) {
            Platform.runLater(()-> {
                for (ThumbnailPane img : images) {
                    File file = img.getImageFile();
                    // 此方法删除不可在回收站找回
                    if (!file.delete()) {   // 删除失败
                        // 不懂咋测
                        Alert alert1 = new Alert(Alert.AlertType.ERROR, "照片" + file.getName() + "删除失败");
                        alert1.showAndWait();
                    }
                }
                refresh();
            });
        }
    }
    public void RenameImage(){
        if(previewFlowPane.getChoseImageNum() == 0){
            // 正常情况进不来，没选择图片，重命名按钮会disable
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("你怎么点进来的 ERROR in MainViewController.RenameImage()");
            alert.showAndWait();
        }
        else if(previewFlowPane.getChoseImageNum() == 1) {
            previewFlowPane.getChoseImages().get(0).rename();
        }
        else{
            Dialog<RenameData> dialog = GenerateDialog.NewMultiRenameDialog(previewFlowPane.getChoseImageNum());
            Optional<RenameData> option = dialog.showAndWait();
            // 如果option数据非空
            if(option.isPresent()){
                RenameData data = option.get();
                String prefix = data.getPrefix();
                int start = data.getStart(), digitNum = data.getDigitNum();
                ArrayList<ThumbnailPane> images = previewFlowPane.getChoseImages();
                int fail = 0;
                for(ThumbnailPane img:images){
                    String newName = Tools.RenameFormat(prefix, start, digitNum, Tools.getFileType(img.getImageFile()));
                    fail += img.renameOneFile(newName, false);
                    start++;
                }
                Alert alert = new Alert(Alert.AlertType.INFORMATION,"",ButtonType.OK);
                alert.setHeaderText("重命名完成");
                alert.setContentText("共重命名: " + images.size() + "\n失败: " + fail);
                alert.showAndWait();
                refresh();
            }
        }
//        refresh();
    }
    private void play(){
        ThumbnailPane image = previewFlowPane.getImages().get(0);
        Platform.runLater(() -> ImageMain.main(image.getImageFile().getPath(), this, true));
    }

    private Stage stage;
    /**
     * 给Main设置stage
     */
    public void setStage(Stage stage){
        this.stage = stage;
//        root.prefHeightProperty().bind(stage.heightProperty());
//        root.prefWidthProperty().bind(stage.widthProperty());
    }
    public Stage getStage(){return stage;}


    /**
     * 目录的前进后退，上一级
     */
    // backStack的栈顶是当前打开的文件夹
    private Stack<myTreeItem> backStack = new Stack<>();
    private Stack<myTreeItem> foreStack = new Stack<>();
    private void updateUpperButtons(){
        backButton.setDisable(backStack.size() < 2);
//        System.out.println(backStack.size() < 2);
        forwardButton.setDisable(foreStack.size() == 0);
//        System.out.println(foreStack.size() == 0);
        upwardButton.setDisable(previewFlowPane.getCurrentDirectory() == null ||
                previewFlowPane.getCurrentDirectory().getParentFile() == ROOT_FILE);
//        System.out.println(previewFlowPane.getCurrentDirectory() == null ||
//                previewFlowPane.getCurrentDirectory().getParentFile() == ROOT_FILE);
    }
    @FXML
    private void backward(){
        if(backStack.size() < 2) return;
        foreStack.push(backStack.pop());
        myTreeItem top = backStack.pop();
        // 浅克隆
        Stack<myTreeItem> tmp = (Stack<myTreeItem>)foreStack.clone();
        // 选择top这个文件夹
        TreeViewFile.getSelectionModel().select(top);
        foreStack = tmp;
        updateUpperButtons();
    }
    @FXML
    private void foreward(){
        if(foreStack.empty()) return;
        myTreeItem top = foreStack.pop();
        // 浅克隆
        Stack<myTreeItem> tmp = (Stack<myTreeItem>)foreStack.clone();
        // 选择top这个文件夹
        TreeViewFile.getSelectionModel().select(top);
        foreStack = tmp;
        updateUpperButtons();
    }
    @FXML
    private void upward(){
        TreeViewFile.getSelectionModel().select(new myTreeItem(previewFlowPane.getCurrentDirectory().getParentFile()));
    }


    /**
     *  文件路径文本框相关
     *  pathText
     */
    private String pathTextCurrent;
    private void initPathText(){
        pathText.setOnKeyPressed(e->{
            if(e.getCode() == KeyCode.ENTER){
                root.requestFocus();
                String path = pathText.getText();
                File file = new File(path);
                if(!file.exists()) {
                    pathText.setText(pathTextCurrent);
                    Alert alert = new Alert(Alert.AlertType.ERROR, "文件路径 " + path + "不存在");
                    alert.showAndWait();
                }
                else if(file.isDirectory()){
                    TreeViewFile.getSelectionModel().select(new myTreeItem(file));
                    pathTextCurrent = path;
                }
                else if(Tools.isLegalFileType(file)) {
                    Platform.runLater(()->ImageMain.main(path, this, false));
                    pathText.setText(pathTextCurrent);
                }
            }
        });

    }
}