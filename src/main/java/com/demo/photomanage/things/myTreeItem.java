package com.demo.photomanage.things;

import com.demo.photomanage.utils.Tools;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;

import javax.swing.filechooser.FileSystemView;
import java.io.File;

public class myTreeItem extends TreeItem<String> {
    private boolean isinited = false;   // 它儿子加载过没
    private final File file;
    public myTreeItem(File file){
        super(Tools.getFileName(file));
        this.file = file;
    }

    @Override
    public ObservableList<TreeItem<String>> getChildren() {         // 展开时发生事件

        ObservableList<TreeItem<String>> children = super.getChildren();

        if (!isinited && isExpanded()) {      // 没有加载子目录
            isinited = true;
            if (file.isDirectory())
                for (File f : file.listFiles())     // 将子文件夹的夹到儿子里
                    if (f.isDirectory()) children.add(new myTreeItem(f));
        }
        return children;
    }

    // 判断能不能点击展开
    @Override
    public boolean isLeaf() {
        return !file.isDirectory();
    }

    public File getFile(){return file;}
}
