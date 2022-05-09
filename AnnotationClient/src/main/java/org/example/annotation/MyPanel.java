package org.example.annotation;
import org.example.annotation.processor.Crac;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.Method;

@Crac
public class MyPanel extends JPanel{
    JPanel newPanel;

    public MyPanel() {
        super();
        this.newPanel = new JPanel();
    }
}
