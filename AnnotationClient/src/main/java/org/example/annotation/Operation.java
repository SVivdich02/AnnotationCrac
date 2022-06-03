package org.example.annotation;

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
        Class generatedClass = Class.forName(className);
        int indexG = className.indexOf("G");
        String originalClassName = "javax.swing.J" + className.substring(25, indexG);
        Class cls = Class.forName(originalClassName);

        if (this.methodName.contains("create")) {
            System.out.println("objectMGF in run(): " + this.obj);
            System.out.println("valueFieldMGF in run(): " + this.obj);
            Object objMGF = this.getObj();

            UpdateComponent target = (UpdateComponent) objMGF;
            target.update(cls.newInstance());

            Field newObjectField = generatedClass.getDeclaredField("newObj");
            Object newObjectFieldValue = newObjectField.get(this.obj);

            System.out.println("objectMGF after restore: " + this.obj);
            System.out.println("valueFieldMGF after restore: " + newObjectFieldValue);
        }
        else {
            Field newObjectField = generatedClass.getDeclaredField("newObj");
            Object newObjectFieldValue = newObjectField.get(this.obj);

            if (this.paramValuesMap.isEmpty()) {
                Method method = cls.getMethod(this.methodName);
                System.out.println("method " + this.methodName + " invoke for " + newObjectFieldValue);
                method.invoke(newObjectFieldValue);
            } else {
                LinkedList<Class> listParameterTypes = new LinkedList<>();
                listParameterTypes.addAll(this.paramValuesMap.keySet());
                Class[] ArrParameterTypes = new Class[this.paramValuesMap.size()];

                listParameterTypes.toArray(ArrParameterTypes);
                Object[] valuesRes = this.paramValuesMap.values().toArray();

                Method method = cls.getMethod(this.methodName, ArrParameterTypes);
                System.out.println("method " + this.methodName + " invoke for " + newObjectFieldValue);

                method.invoke(newObjectFieldValue, valuesRes);
            }
        }
    }
}
