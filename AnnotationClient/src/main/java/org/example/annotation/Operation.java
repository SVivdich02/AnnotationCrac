package org.example.annotation;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Operation {
    Method method;
    Class cls;
    Map<Class, Object> paramValuesMap;

    public Operation(Method method, Class cls, Map<Class, Object> paramValuesMap)
    {
        this.method = method;
        this.cls = cls;
        this.paramValuesMap = paramValuesMap;
    }

    public Method getMethod() {
        return method;
    }

    public void run() {
    }
}
