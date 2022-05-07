package org.example.annotation;

import javax.swing.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class Operation {
    String className;
    String methodName;
    Object obj;
    LinkedHashMap<Class, Object> paramValuesMap;

    public Operation(String className, String methodName, LinkedHashMap<Class, Object> paramValuesMap, Object obj)
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

    public LinkedHashMap<Class, Object> getParamValuesMap()
    {
        return paramValuesMap;
    }

    public void run() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Class cls = Class.forName(className);

        if (paramValuesMap.isEmpty()) {
            Method method = cls.getMethod(methodName);
            method.invoke(obj);
        } else {
            LinkedList<Class> listParameterTypes = new LinkedList<>();
            listParameterTypes.addAll(paramValuesMap.keySet());
            Class[] ArrParameterTypes = new Class[paramValuesMap.size()];

            listParameterTypes.toArray(ArrParameterTypes);
            Object[] valuesRes = paramValuesMap.values().toArray();

            Method method = cls.getMethod(methodName, ArrParameterTypes);
            method.invoke(obj, valuesRes);
        }
    }
}
