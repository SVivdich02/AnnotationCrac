package org.example.annotation;

import javax.swing.*;
import java.lang.reflect.Field;
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
    public Object getObj() {
        return obj;
    }

    public void run() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException {
        Class cls = Class.forName(className);

        if (this.methodName.contains("create")) {
            System.out.println("newComponent in run(): " + this.obj);
            Object temp = cls.newInstance();
            this.obj = temp;
            System.out.println("newComponent in run() after restore: " + this.obj);
        }
        else {

            if (this.paramValuesMap.isEmpty()) {
                Method method = cls.getMethod(this.methodName);
                System.out.println("method " + this.methodName + " invoke for " + this.obj);
                method.invoke(this.obj);
            } else {
                LinkedList<Class> listParameterTypes = new LinkedList<>();
                listParameterTypes.addAll(this.paramValuesMap.keySet());
                Class[] ArrParameterTypes = new Class[this.paramValuesMap.size()];

                listParameterTypes.toArray(ArrParameterTypes);
                Object[] valuesRes = this.paramValuesMap.values().toArray();

                Method method = cls.getMethod(this.methodName, ArrParameterTypes);
                System.out.println("method " + this.methodName + " invoke for " + this.obj);
                method.invoke(this.obj, valuesRes);
            }
        }
    }
}
