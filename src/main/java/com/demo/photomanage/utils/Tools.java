package com.demo.photomanage.utils;

import com.demo.photomanage.model.ThumbnailPane;
import javafx.scene.image.Image;

import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.util.*;
import java.util.regex.Pattern;

import static com.demo.photomanage.utils.GlobalValue.LEGAL_TYPE;
import static java.lang.Math.abs;
import static java.lang.Math.max;

public class Tools {
    public static File[] getFolder(File file){      //返回文件夹下的所有文件夹
        if(!file.isDirectory())return null;     //不是文件夹
        ArrayList<File> result = new ArrayList<File>();
        File[] files = file.listFiles();
        for(File f:files)
            if(f.isDirectory())
                result.add(f);
        return result.toArray(new File[result.size()]);
    }
    public static ArrayList<File> getAvailableImageFiles(File[] files){
        ArrayList<File> result = new ArrayList<>();
        for(File f:files){
            if(f.isFile() && isLegalFileType(f)){
                result.add(f);
            }
        }
        return result;
    }
    // 用于目录树，f.getName()对磁盘不适用(记得好像是)
    public static String getFileName(File f){return FileSystemView.getFileSystemView().getSystemDisplayName(f);}
    public static String getFileType(File f){return f.getName().substring(f.getName().lastIndexOf(".")+1).toLowerCase();}

    public static String Fitbyte(long size){
        double b = size;
        int type = 0;
        while(b >= 1024){
            b /= 1024;
            type++;
        }
        assert(type < GlobalValue.STORAGE_SIZE.length);
        return String.format("%.2f %s", b, GlobalValue.STORAGE_SIZE[type]);
    }
    public static String calcImgsSize(ArrayList<ThumbnailPane> list){
        long size = 0;
        for(ThumbnailPane c:list){
            size += c.length();
        }
        return Tools.Fitbyte(size);
    }
    public static String getAvailableName(File f, Set<String> s){
        if(!s.contains(f.getName()))return f.getName();
        String type = getFileType(f);
        String name = f.getName().substring(0, f.getName().lastIndexOf("."));
        if(!s.contains(name + " - 副本." + type))return name + " - 副本." + type;
        for(int i = 1; ;i++){
            String resname = name + " - 副本" + i + "." + type;
            if(!s.contains((resname)))return resname;
        }
    }
    public static String getImageName(Image image){
        File file = new File(image.getUrl());
        return file.getName();
    }

    /**
     * 判断是不是合法的数字(整数or小数)
     * type=0整数，1浮点数
     */
    public static boolean isNumber(String str, int type){ //
        // 根据正则判断是不是合法
        if(type == 0)
            return Pattern.compile("[0-9]*").matcher(str).matches();
        if(type == 1)
            return Pattern.compile("(?:[+]?\\d+(?:\\.\\d*)?|\\.\\d+)?").matcher(str).matches();
        return false;
    }

    /**
     * 计算字符串在长度
     * @param code  编码方式
     * ASCII 为自定义的正则方法
     */
    public static int getStrLength(String str, String code) {
        /*基本原理是将字符串中所有的非标准字符（双字节字符）替换成两个标准字符（**，或其他的也可以）。这样就可以直接例用length方法获得字符串的字节长度了*/
        if(code.equals("ASCII")) {
            String tmp = str.replaceAll("[^\\x00-\\xff]", "**");
            return tmp.length();
        }
        // utf-8 GBK
        try {
            return str.getBytes(code).length;
        }catch (Exception e){
            e.printStackTrace();
            return -1;
        }
    }
    public static int getDigitNum(int x){   // 返回x为几位数
        if(x == 0)return 1;
        int result = 0;
        while(x > 0){
            result++;
             x /= 10;
        }
        return result;
    }
    public static String RenameFormat(String prefix, int number, int digitNum, String type){
        int zero = max(0, digitNum-getDigitNum(number));    // 前导0
        return prefix + "0".repeat(zero) + number + "." + type;
    }
    public static String shortenString(String str, int max_len){
        if(getStrLength(str, "ASCII") >= max_len) {
            StringBuilder res = new StringBuilder();
            max_len -= 3;
            int i;
            for(i = 0; i < str.length() && max_len > 0; i++){
                int ascii = Character.codePointAt(str, i);
                if(ascii >=0 && ascii <= 255) max_len--;
                else max_len -= 2;
            }
            return str.substring(0, i) + "...";
        }
        return str;
    }
    public static boolean isLegalFileType(File f){
        if(f==null || !f.exists() || f.isDirectory())return false;
        return LEGAL_TYPE.contains(getFileType(f));
    }

    /**
     * maybe useles
     */
    public static boolean isEqual(double... args){
        if(args.length % 2 != 0)return true;
        for(int i = 0; i < args.length; i += 2){
            if(abs(args[i] - args[i+1]) > GlobalValue.EPS)return false;
        }
        return true;
    }
}
