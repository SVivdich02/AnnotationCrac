package org.example.annotation;
import org.example.annotation.processor.Crac;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.Method;

@Crac
public class MyButton extends JButton {
    JButton newButton;

    public MyButton() {
        super();
        this.newButton = new JButton();
    }
}
