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

    public void run() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException, NoSuchFieldException {
        Class cls = Class.forName(className);

        if (this.methodName.contains("create")) {
            System.out.println("objectMGF in run(): " + this.obj);
            System.out.println("valueFieldMGF in run(): " + this.obj);
            Object objMGF = this.getObj();

            UpdateComponent target = (UpdateComponent) objMGF;

            if (this.methodName.contains("rame")) {
                target.update(new JFrame());
            }

            if (this.methodName.contains("tton")) {
                target.update(new JButton());
            }

            if (this.methodName.contains("nel")) {
                target.update(new JPanel());
            }

            Field newObjectField = cls.getDeclaredField("newObj");
            Object newObjectFieldValue = newObjectField.get(this.obj);

            System.out.println("objectMGF after restore: " + this.obj);
            System.out.println("valueFieldMGF after restore: " + newObjectFieldValue);

            /*
            Object temp = cls.newInstance();
            this.obj.update(temp);
            */
        }
        else {
            Field newObjectField = cls.getDeclaredField("newObj");
            Object newObjectFieldValue = newObjectField.get(this.obj);

            if (this.paramValuesMap.isEmpty()) {
                Method method = cls.getMethod(this.methodName);
                System.out.println("method " + this.methodName + " invoke for " + newObjectFieldValue);
                //method.invoke(this.obj);
                method.invoke(newObjectFieldValue);
            } else {
                LinkedList<Class> listParameterTypes = new LinkedList<>();
                listParameterTypes.addAll(this.paramValuesMap.keySet());
                Class[] ArrParameterTypes = new Class[this.paramValuesMap.size()];

                listParameterTypes.toArray(ArrParameterTypes);
                Object[] valuesRes = this.paramValuesMap.values().toArray();

                Method method = cls.getMethod(this.methodName, ArrParameterTypes);
                System.out.println("method " + this.methodName + " invoke for " + newObjectFieldValue);
                //method.invoke(this.obj, valuesRes);

                method.invoke(newObjectFieldValue, valuesRes);
            }
        }
    }
}
