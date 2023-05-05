package com.demo.photomanage.utils;

import com.demo.photomanage.container.PlayData;
import com.demo.photomanage.container.RenameData;
import com.demo.photomanage.model.NumberTextField;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

public class GenerateDialog {

    public static Dialog<RenameData> NewMultiRenameDialog(int total){
        Dialog<RenameData> dialog = new Dialog<>();
        GridPane gridPane = new GridPane();
        TextField textField = new TextField();
        NumberTextField start = new NumberTextField(0);
        NumberTextField digitNum = new NumberTextField(0);
        gridPane.add(new Label("名称前缀: "), 0, 0);
        gridPane.add(new Label("起始编号: "), 0, 1);
        gridPane.add(new Label("编号位数: "), 0, 2);
        gridPane.add(textField, 1, 0);
        gridPane.add(start, 1, 1);
        gridPane.add(digitNum, 1, 2);
        dialog.getDialogPane().setContent(gridPane);
        dialog.getDialogPane().getButtonTypes().setAll(ButtonType.OK, ButtonType.CANCEL);
        dialog.setResultConverter(buttonType -> {
            if(buttonType == ButtonType.OK){
                int st, digit;
                if(start.getText().equals(""))st = 0;
                else st = Integer.parseInt(start.getText());
                if(digitNum.getText().equals(""))digit = Tools.getDigitNum(total);
                else digit = Integer.parseInt(digitNum.getText());
                return new RenameData(textField.getText(), st, digit);
            }
            // 没有点ok
            return null;
        });
        return dialog;
    }

    public static Dialog<String> NewOneRenameDialog(String oldName){
        Dialog<String> dialog = new Dialog<>();
        GridPane gridPane = new GridPane();
        TextField textField = new TextField(oldName);
        gridPane.add(new Label("新名称: "), 0, 0);
        gridPane.add(textField, 1, 0);
        dialog.getDialogPane().setContent(gridPane);
        dialog.getDialogPane().getButtonTypes().setAll(ButtonType.OK, ButtonType.CANCEL);
        dialog.setResultConverter(buttonType -> {
            if(buttonType == ButtonType.OK)return textField.getText();
            return null;
        });
        return dialog;
    }

    public static Dialog<PlayData> NewPlayDialog(){
        Dialog<PlayData> dialog = new Dialog<>();
        GridPane gridPane = new GridPane();
        NumberTextField timeText = new NumberTextField(1);
        gridPane.add(new Label("时间间隔: "), 0, 0);
        gridPane.add(timeText, 1, 0);
        dialog.getDialogPane().setContent(gridPane);
        dialog.getDialogPane().getButtonTypes().setAll(ButtonType.OK, ButtonType.CANCEL);
        dialog.setResultConverter(buttonType -> {
            if(buttonType == ButtonType.OK){
                double time;
                if(timeText.getText().equals(""))time = 3;  // 默认间隔
                else time = Double.parseDouble(timeText.getText());
                return new PlayData(time);
            }
            // 没有点ok
            return null;
        });
        return dialog;
    }
}
