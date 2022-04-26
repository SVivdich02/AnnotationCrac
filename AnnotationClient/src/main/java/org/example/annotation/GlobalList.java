package org.example.annotation;

import java.util.ArrayList;
import java.util.List;

public class GlobalList {
    public static List<Operation> list = new ArrayList<Operation>();

    public static void RunList()
    {
        int correctlySize = list.size();

        for (int i = 0; i < correctlySize; i++)
        {
            list.get(i).run();
        }
    }
}
