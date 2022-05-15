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
    public Object getObj() {
        return obj;
    }

    public void run(int correctlySize) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException {
        Class cls = Class.forName(className);

        if (this.methodName.contains("create")) {
            Object temp = cls.newInstance();
            for (int i = 0; i < correctlySize; i++)
            {
                Operation op = GlobalList.list.get(i);
                if (op.obj == this.obj)
                {
                    op.obj = temp;
                }
            }
            this.obj = temp;
        }
        else {

            if (this.paramValuesMap.isEmpty()) {
                Method method = cls.getMethod(this.methodName);
                method.invoke(this.obj);
            } else {
                LinkedList<Class> listParameterTypes = new LinkedList<>();
                listParameterTypes.addAll(this.paramValuesMap.keySet());
                Class[] ArrParameterTypes = new Class[this.paramValuesMap.size()];

                listParameterTypes.toArray(ArrParameterTypes);
                Object[] valuesRes = this.paramValuesMap.values().toArray();

                Method method = cls.getMethod(this.methodName, ArrParameterTypes);
                method.invoke(this.obj, valuesRes);
            }
        }
    }
}
