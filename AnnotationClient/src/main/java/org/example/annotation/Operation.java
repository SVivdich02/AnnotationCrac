package org.example.annotation;

import javax.swing.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class Operation {
    String className;
    String methodName;
    Object obj;
    //LinkedHashMap<String, Object[]> paramValuesMap;
    LinkedHashMap<String, Object> paramValuesMap;

    public Operation(String className, String methodName, LinkedHashMap<String, Object> paramValuesMap, Object obj)
    {
        this.className = className;
        this.methodName = methodName;
        this.paramValuesMap = paramValuesMap;
        this.obj = obj;
    }

    public String getClassName()
    {
        return className;
    }

    public String getMethodName()
    {
        return methodName;
    }

    public LinkedHashMap<String, Object> getParamValuesMap()
    {
        return paramValuesMap;
    }

    public void run() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Class c = Class.forName(className);

        if (paramValuesMap.isEmpty()) {
            Method m = c.getMethod(methodName);
            m.invoke(obj);
        }
        else {
            LinkedList<String> listParameterTypes = new LinkedList<String>();
            listParameterTypes.addAll(paramValuesMap.keySet());

            Class[] ArrParameterTypes = new Class[listParameterTypes.size()];
            for (int j = 0; j < listParameterTypes.size(); j++) {
                String name = listParameterTypes.get(j);
                Class param = Class.forName(name);
                ArrParameterTypes[j] = param;
            }

            Object[] valuesRes = paramValuesMap.values().toArray();

            Method m = c.getMethod(methodName, ArrParameterTypes);
            m.invoke(obj, valuesRes);
        }

        /*
        Class c = Class.forName(className);

        LinkedList<String> listParameterTypes = new LinkedList<String>();
        listParameterTypes.addAll(paramValuesMap.keySet());

        LinkedList<Object[]> valuesRes = new LinkedList<Object[]>();

        for (int k = 0; k < paramValuesMap.size(); k++)
        {
            valuesRes.add(paramValuesMap.get(k));
            /*
            if (!listParameterTypes.get(k).contains("["))
            {
                Object[] paramNotMas = new Object[1];
                paramNotMas[0] = paramValuesMap.get(k);
                valuesres.add(paramNotMas);
            }
            else // параметр -- массив
            {
                Object[] paramMas = paramValuesMap.get(k);
                valuesres.add(paramMas);
            }
        }

        // получим в valueRes список:
        // 5
        // { "abc", "def" } и тд

        Class[] ArrParameterTypes = new Class[listParameterTypes.size()];
        for (int j = 0; j < listParameterTypes.size(); j++)
        {
            String name = listParameterTypes.get(j);
            Class param = Class.forName(name);
            ArrParameterTypes[j] = param;
        }

        Method m = c.getMethod(methodName, ArrParameterTypes);
        m.invoke(obj, valuesRes);
         */
    }
}
