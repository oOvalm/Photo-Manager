package com.demo.photomanage.utils;
import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class GlobalValue {
    /**
     * FileSystemView.getFileSystemView().getRoots()[0]
     * 该方法获得系统默认根目录，Windows下返回的是桌面
     * 在initialize中将ROOT_FILE设为此电脑
     */
    public static File ROOT_FILE;
    public static Set<String> LEGAL_TYPE = new HashSet<>();
    public static String[] STORAGE_SIZE = {"B", "KB", "MB", "GB", "TB"};
    public static double EPS = 0.01;

//    // 每个路径最近更新的时间戳
//    public static HashMap<String, Long> Directory_TimeStamp = new HashMap<>();
//    //
//    public static HashMap<String, Long> Directory_Num = new HashMap<>();

    public static void initialize(){
        for(File f:FileSystemView.getFileSystemView().getRoots()[0].listFiles())
            if(Tools.getFileName(f).compareTo("此电脑") == 0)
                GlobalValue.ROOT_FILE = f;
        LEGAL_TYPE.add("bmp");
        LEGAL_TYPE.add("gif");
        LEGAL_TYPE.add("jpeg");
        LEGAL_TYPE.add("png");
        LEGAL_TYPE.add("jpg");

    }
}
