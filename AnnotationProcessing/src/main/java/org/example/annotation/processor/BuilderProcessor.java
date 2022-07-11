package org.example.annotation.processor;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.JavaFileObject;

import com.google.auto.service.AutoService;
@SupportedAnnotationTypes("org.example.annotation.processor.Crac")
@AutoService(Processor.class)

public class BuilderProcessor extends AbstractProcessor {
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for(TypeElement annotation: annotations)
        {
            Set<? extends Element> annotatedElements = roundEnv.getElementsAnnotatedWith(annotation);

            for (Element element : annotatedElements)
            {
                String superClassName = ((TypeElement)element).getSuperclass().toString();
                String className = ((TypeElement) element).getQualifiedName().toString();

                try {
                    Class startClass = Class.forName(superClassName);
                    try {
                        generateClass(className, superClassName, startClass);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        return true;
    }

    public static boolean isModifierSet(int allModifiers, int specificModifier) {
        return (allModifiers & specificModifier) > 0;
    }

    private void addOperationToList(PrintWriter out, LinkedList<String> argumentType, String generatedClassName, String methodName, String currentClassName){
        out.println("""
                            StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
                            boolean fromSystem = Arrays.stream(stackTraceElements).toList().get(2).getClassName().contains("java.");
                            boolean fromXSystem = Arrays.stream(stackTraceElements).toList().get(2).getClassName().contains("javax.");
                            boolean fromSun = Arrays.stream(stackTraceElements).toList().get(2).getClassName().contains("sun");
                            
                            if (!fromSystem && !fromXSystem && !fromSun) {
                            """);
        int i = 0;
        out.println("   LinkedHashMap<Class, Object> paramValuesMap = new LinkedHashMap<>();");
        out.println();
        for (String s : argumentType) {
            i++;
            if (s.contains("$")) {
                s = s.replace("$", ".");
            }

            out.print(" paramValuesMap.put(" + s + ".class, (Object) value" + i + ");");
            out.println();
        }
        out.println();
        System.out.println("from addOpToList before add to global list: " +  currentClassName);
        out.print("   GlobalList.list.add(new Operation(");
        out.print("\"" + generatedClassName + "\", \"" + methodName + "\", paramValuesMap, this, " + "\"" + currentClassName + "\"" + "));");
        System.out.println("from addOpToList after add to global list: " +  currentClassName);
        out.println("}");
    }

    private void buildClassConstructor(PrintWriter out, String generatedSimpleClassName, String superClassName, String generatedClassName)
    {
        out.print(" public ");
        out.print(generatedSimpleClassName);
        out.print("""
                () {
                    super();
                """);
        out.println("this.newObj = this;");
        out.print("""
                    LinkedHashMap<Class, Object> paramValuesMap = new LinkedHashMap<>();
                    GlobalList.list.add(new Operation(                
                """);
        out.print("\"" + generatedClassName + "\", \"createMy" + superClassName.substring(13) + "Generated\", paramValuesMap, this, null));");
        out.println("}");
        out.println();
    }

    private void addUpdateMethod(PrintWriter out) {
        out.print("""
                    public void update(java.lang.Object obj) {    
                    newObj = obj;
                    }    
                """);
    }


    private void generateClass(String className, String superClassName, Class currentClass) throws IOException {
        HashMap<HashMap<String, LinkedList<String>>, Boolean> addedMethods = new HashMap<>();
        String packageName = null;
        int lastDot = className.lastIndexOf('.');
        if (lastDot > 0) {
            packageName = className.substring(0, lastDot);
        }
        String generatedClassName = className + "Generated";
        String generatedSimpleClassName = generatedClassName.substring(lastDot + 1);
        String fieldType = "javax.swing.J" + superClassName.substring(13);
        String fieldName = "new" + superClassName.substring(13);

        JavaFileObject builderFile = processingEnv.getFiler().createSourceFile(generatedClassName);
        try (PrintWriter out = new PrintWriter(builderFile.openWriter())) {
            if (packageName != null) {
                out.print("package " + packageName + ";");
                out.println();
            }
            out.println();
            out.println("import java.util.*;");
            out.println();
            out.print("public class " + generatedSimpleClassName + " extends javax.swing." + superClassName.substring(12));
            out.print(" implements org.example.annotation.UpdateComponent {");
            out.println();

            out.println();
            out.print("    java.lang.Object newObj;");
            out.println();

            buildClassConstructor(out, generatedSimpleClassName, superClassName, generatedClassName);
            addUpdateMethod(out);

            while (true) {
                String currentClassName = currentClass.getName();
                System.out.println("from generateClass : " + currentClassName);
                if (currentClassName.contains("Obj")) {
                    break;
                }

                for (Method method : currentClass.getDeclaredMethods()) {
                    LinkedList<String> listParamTypes = new LinkedList<>();
                    for (Class cls : method.getParameterTypes()) {
                        listParamTypes.add(cls.getName());
                    }
                    HashMap<String, LinkedList<String>> newMethod = new HashMap<>();
                    newMethod.put(method.getName(), listParamTypes);
                    if (!addedMethods.containsKey(newMethod)) {
                        System.out.println("from generateClass before generateMethod: " + currentClassName);
                        generateMethod(method, out, currentClassName, generatedClassName, currentClassName);
                        System.out.println("from generateClass after generateMethod: " + currentClassName);
                        addedMethods.put(newMethod, true);
                    }
                }
                currentClass = currentClass.getSuperclass();
            }
            out.println("   }");
        }
    }

    private void generateMethod(Method method, PrintWriter out, String superClassName, String generatedClassName, String currentClassName) {
        String methodName = method.getName();

        String returnType = updateReturnTypeName(method.getReturnType().getName());
        if (methodName == "getUI" && !superClassName.contains("utton")) {
            returnType = "javax.swing.plaf." + superClassName.substring(13) + "UI";
        }

        if (methodName == "getUI" && superClassName.contains("utton")) {
            returnType = "javax.swing.plaf.ButtonUI";
        }

        int classModifiers = method.getModifiers();
        boolean isFinal = isModifierSet(classModifiers, Modifier.FINAL);
        boolean isStatic = isModifierSet(classModifiers, Modifier.STATIC);
        boolean isPublic = isModifierSet(classModifiers, Modifier.PUBLIC);

        Class[] exceptions = method.getExceptionTypes();
        if (isPublic && !isFinal && !isStatic) {
            LinkedList<String> listParamTypes = new LinkedList<>();
            for (Class cls : method.getParameterTypes()) {
                listParamTypes.add(cls.getName());
            }
            out.println("@Override");
            out.println();
            out.print("    public " + returnType + " " + methodName + "(");
            parameterEnumeration(out, listParamTypes, true);
            out.print(") ");
            addExceptions(out, exceptions);
            out.print(" {");
            out.println();

            System.out.println("from generateMethod before addOpToList: " + currentClassName);
            addOperationToList(out, listParamTypes, generatedClassName, methodName, currentClassName);
            System.out.println("from generateMethod after addOpToList: " + currentClassName);
            out.println();

            if (returnType != "void") {
                out.print("   return super.");
            } else {
                out.print("   super.");
            }
            out.print(methodName + "(");
            parameterEnumeration(out, listParamTypes, false);
            out.print(");");
            out.println();
            out.println("   }");
            out.println();
        }
    }

    private String updateReturnTypeName(String returnType) {
        String newReturnTypeName = returnType;

        if (returnType.startsWith("[")) {
            int endChar = returnType.length();
            newReturnTypeName = returnType.substring(2, endChar - 1) + "[]";
        }

        if (newReturnTypeName.contains("$")) {
            newReturnTypeName = newReturnTypeName.replace("$", ".");
        }
        return newReturnTypeName;
    }

    private void parameterEnumeration(PrintWriter out, LinkedList<String> listParamTypes, boolean forMethodSignature) {
        int i = 0;
        for (String s : listParamTypes) {
            i++;
            if (forMethodSignature) {
                if (s.contains("$")) {
                    s = s.replace("$", ".");
                }
                out.print(s + " ");
            }
            out.print("value" + i);

            if (listParamTypes.size() != i) {
                out.print(", ");
            }
        }
    }

    private void addExceptions(PrintWriter out, Class[] exceptions) {
        if (exceptions.length != 0) {
            out.print("throws ");
            int i = 0;
            for (Class exception: exceptions) {
                i++;
                out.print(exception.getName());
                if (i != exceptions.length) {
                    out.print(", ");
                }
            }
        }
    }
}



