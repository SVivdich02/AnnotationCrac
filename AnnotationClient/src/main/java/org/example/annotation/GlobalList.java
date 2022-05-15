package org.example.annotation;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class GlobalList {
    public static LinkedList<Operation> list = new LinkedList<>();

    public static void RunList() throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IllegalAccessException, InstantiationException {
        int correctlySize = list.size();

        for (int i = 0; i < correctlySize; i++)
        {
            list.get(i).run(correctlySize);
        }
    }
}
