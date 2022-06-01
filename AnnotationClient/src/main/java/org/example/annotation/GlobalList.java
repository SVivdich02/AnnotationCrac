package org.example.annotation;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class GlobalList {
    public static LinkedList<Operation> list = new LinkedList<>();

    public static void runList(int correctlySize) throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IllegalAccessException, InstantiationException {
        for (int i = 0; i < correctlySize; i++)
        {
            System.out.println("operation: " + list.get(i));
            list.get(i).run();
        }
    }
}
