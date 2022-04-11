package org.example.annotation;
import org.example.annotation.processor.Crac;

import javax.swing.*;
import java.awt.*;

@Crac
public class MyButton extends JButton {
    private String text;
    private Color color;
    private Boolean visible;

    public void setText(String text) {
        this.text = text;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void setVisible(Boolean visible)
    {
        this.visible = visible;
    }
}
