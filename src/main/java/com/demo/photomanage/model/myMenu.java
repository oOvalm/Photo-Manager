package com.demo.photomanage.model;

import com.demo.photomanage.ImageMain;
import com.demo.photomanage.container.RenameData;
import com.demo.photomanage.controller.MainViewController;
import com.demo.photomanage.utils.GenerateDialog;
import com.demo.photomanage.utils.Tools;
import javafx.application.Platform;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class myMenu extends ContextMenu {
    private MenuItem pasteButton;
    private MenuItem copyButton;
    private MenuItem refreshButton;
    private MenuItem deleteButton;
    private MenuItem renameButton;
    private MenuItem playAllButton;
    private MainViewController mainviewcontroller;
    private myFlowPane previewFlowPane;
    public myMenu(MainViewController mainViewController, myFlowPane previewFlowPane){
        super();
        mainviewcontroller = mainViewController;
        this.previewFlowPane = previewFlowPane;
        copyButton = new MenuItem("复制");
        pasteButton = new MenuItem("粘贴");
        refreshButton = new MenuItem("刷新");
        deleteButton = new MenuItem("删除");
        renameButton = new MenuItem("重命名");
        playAllButton = new MenuItem("播放所有图片");
        // 如果此处有增删改，查一下menu.getItems()，下面好像有直接访问下标的，要对上。
        this.getItems().addAll(copyButton, pasteButton, refreshButton, deleteButton, renameButton, playAllButton);
        // 都写成成员函数(不要写lambda，要复用)
        copyButton.setOnAction(e->CopyImage());
        pasteButton.setOnAction(e->PasteImage());
        refreshButton.setOnAction(e->mainviewcontroller.refresh());
        deleteButton.setOnAction(e->DeleteImage());
        renameButton.setOnAction(e->RenameImage());
        playAllButton.setOnAction(e-> play());
    }
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
        mainviewcontroller.refresh();
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
                        Alert alert1 = new Alert(Alert.AlertType.ERROR, "照片" + file.getName() + "删除失败");
                        alert1.showAndWait();
                    }
                }
                mainviewcontroller.refresh();
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
                mainviewcontroller.refresh();
            }
        }
//        refresh();
    }
    private void play(){
        ThumbnailPane image = previewFlowPane.getImages().get(0);
        Platform.runLater(() -> ImageMain.main(image.getImageFile().getPath(), mainviewcontroller, true));
    }

    public MenuItem getPasteButton() {return pasteButton;}
    public MenuItem getCopyButton () {return copyButton;}
    public MenuItem getRefreshButton () {return refreshButton;}
    public MenuItem getDeleteButton () {return deleteButton;}
    public MenuItem getRenameButton () {return renameButton;}
    public MenuItem getPlayAllButton () {return playAllButton;}
}
