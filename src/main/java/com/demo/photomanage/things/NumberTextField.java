package com.demo.photomanage.things;

import com.demo.photomanage.utils.Tools;
import javafx.scene.control.TextField;

public class NumberTextField extends TextField {
    public NumberTextField(int type){
        super();
        this.textProperty().addListener((o, oldValue, newValue)->{
            if(!Tools.isNumber(newValue, type)){
                this.setText(oldValue);
            }
        });
    }
}
