package org.example.annotation;
import org.example.annotation.processor.Crac;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.Method;

@Crac
public class MyButton extends JButton {
    MyButton newButton;
    private String text;

    public MyButton() {
        super();
        this.newButton = this;
    }

    public void setText(String text) {
    }
}
