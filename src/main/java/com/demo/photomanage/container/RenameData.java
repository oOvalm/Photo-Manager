package com.demo.photomanage.container;

public class RenameData {
    private String prefix;
    private int start;
    private int digitNum;
    public RenameData(String prefix, int start, int digitNum){
        this.prefix = prefix;
        this.start = start;
        this.digitNum = digitNum;
    }
    public String getPrefix(){return prefix;}
    public int getStart(){return start;}
    public int getDigitNum(){return digitNum;}
}
