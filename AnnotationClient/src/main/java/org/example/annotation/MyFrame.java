package org.example.annotation;
import org.example.annotation.processor.Crac;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.Method;

@Crac
public class MyFrame extends JFrame{
    JFrame newFrame;

    public MyFrame() {
        super();
        this.newFrame = new JFrame();
    }
}
