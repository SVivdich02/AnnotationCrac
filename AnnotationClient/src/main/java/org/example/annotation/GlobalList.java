package org.example.annotation;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class GlobalList {
    public static List<Operation> list = new ArrayList<Operation>();

    public static void RunList() throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        int correctlySize = list.size();

        for (int i = 0; i < correctlySize; i++)
        {
            list.get(i).run();
        }
    }
}
