package com.demo.photomanage.model;

import com.demo.photomanage.controller.MainViewController;
import com.demo.photomanage.utils.Tools;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.FlowPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static java.lang.Math.abs;
import static java.lang.Math.min;

public class myFlowPane extends FlowPane {
    private File curfolder;
    private ArrayList<ThumbnailPane> images = new ArrayList<>();
    private ArrayList<ThumbnailPane> choseimage = new ArrayList<>();
    private Set<String> nameset = new HashSet<>();
    private MainViewController mainviewcontroller;
    private ScrollPane scrollpane;
    private double xPressed, yPressed;
    private Rectangle rectangle = new Rectangle();    // 鼠标框选
    private int scrolldirection;    // -1,0,1 上，不滚，下

    public myFlowPane(MainViewController mainViewController, ScrollPane scrollPane){
        super();
        curfolder = null;
        this.mainviewcontroller = mainViewController;
        this.scrollpane = scrollPane;
        this.setVgap(5);
        this.setHgap(5);

        // (0,120,215)  (r,g,b,透明度) TODO:有没有机会设边框
        rectangle.setFill(Color.rgb(0,120,215, 0.5));
        rectangle.setVisible(false);
        scrolldirection = 0;

        this.setOnMousePressed(e->{
            // 按下的坐标
            xPressed = e.getX();
            yPressed = e.getY();
            mainviewcontroller.HideMenu();

            Node clickNode = e.getPickResult().getIntersectedNode();     // 获取鼠标点到的东西
            if(e.getButton() == MouseButton.PRIMARY){   // 左键
                // 没点到缩略图
                if(!(clickNode instanceof ImageView || clickNode instanceof ThumbnailPane)){
                    unSelectAll();
                    updateTipInfoLabel();
                }
            }
            else if(e.getButton() == MouseButton.SECONDARY){    // 右键
                mainviewcontroller.ShowMenu(e.getScreenX(), e.getScreenY());
//                System.out.print(String.format("%f %f %f %f %f %f\n", xPressed, yPressed, e.getSceneX(), e.getSceneY(), e.getScreenX(), e.getScreenY()));
            }
        });


        // 矩形框选
        this.setOnMouseDragged(e -> {
            double curx = e.getX(), cury = e.getY();    // 当前鼠标坐标
//            System.out.printf("%f %f %f %f%n", xPressed,yPressed,curx, cury);
            // 左上角
            rectangle.setX(min(curx, xPressed));
            rectangle.setY(min(cury, yPressed));
            rectangle.setWidth(abs(curx - xPressed));
            rectangle.setHeight(abs(cury - yPressed));
            rectangle.setVisible(true);


            // 矩阵有点大小就将矩阵里的选上
            if(rectangle.getWidth() > 1 && rectangle.getHeight() > 1){
                unSelectAll();
                for(ThumbnailPane img:images){
                    if(rectangle.intersects(img.getBoundsInParent())){
                        Select(img);
                    }
                }
            }

            // TODO: 在MainViewController.Handler()开个线程(或者Platform.runLater)通过scrolldirection的值上下滚
            // 在这直接滚，如果鼠标不动就不会滚。
            // todo是一个可能的解决办法
            if(e.getSceneY() < 0) {
                scrollpane.setVvalue(scrollpane.getVvalue() - 0.001);
                scrolldirection = -1;
            }
            else if(e.getSceneY() > scrollpane.getHeight()) {
                scrollpane.setVvalue(scrollpane.getVvalue() + 0.001);
                scrolldirection = 1;
            }
            else scrolldirection = 0;
        });

        this.setOnMouseReleased(e -> {
            rectangle.setVisible(false);
            mainviewcontroller.updateMenuButtonAble();
        });
    }
    public void UpdateView(File folder){
//        if(folder.equals(curfolder))return;   刷新要调这个函数
        curfolder = folder;
        images.clear();
        choseimage.clear();
        nameset.clear();
        for(File f:Tools.getAvailableImageFiles(folder.listFiles())){
            images.add(new ThumbnailPane(f, mainviewcontroller));
            nameset.add(f.getName());
        }
        this.getChildren().setAll(images);
        updateTipInfoLabel();
    }

    public void Select(ThumbnailPane img){
        choseimage.add(img);
        img.Select();
    }
    public void unSelect(ThumbnailPane img){
        choseimage.remove(img);
        img.unSelect();
    }
    public void unSelectAll(){  //清空所有选择的
        for(ThumbnailPane img:choseimage){
            img.unSelect();
        }
        choseimage.clear();
    }
    public void SelectAll(){  //选择所有 (效率小低)
        choseimage.clear();
        for(ThumbnailPane img:images){
            img.Select();
            choseimage.add(img);
        }
    }
    public ArrayList<ThumbnailPane> getImages(){return images;}
    public int getImageNum(){return images.size();}
    public int getChoseImageNum(){return choseimage.size();}
    // 图片总大小
    public String getImageSize(){return Tools.calcImgsSize(images);}
    public String getChoseImageSize(){return Tools.calcImgsSize(choseimage);}

    public ArrayList<ThumbnailPane> getChoseImages() {return choseimage;}

    public void updateTipInfoLabel(){mainviewcontroller.updateTipInfoLabel();}
    public Rectangle getRectangle(){return rectangle;}
    public int getScrollDirection(){return scrolldirection;}
    public File getCurrentDirectory(){return curfolder;}
    public Set<String> getNameset(){return nameset;}
}
