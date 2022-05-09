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
                LinkedHashMap<String, LinkedList<String>> methodsMap = new LinkedHashMap<>();
                LinkedHashMap<String, String> returnTypeMap = new LinkedHashMap<>();

                //String superClassName = element.getSimpleName().toString(); //-- это MyJButton

                String superClassName = ((TypeElement)element).getSuperclass().toString();
                String className = ((TypeElement) element).getQualifiedName().toString();
                try {
                    Class superClass = Class.forName(superClassName);
                    for (Method method : superClass.getMethods())
                    {
                        int classModifiers = method.getModifiers();
                        boolean isFinal = isModifierSet(classModifiers, Modifier.FINAL);
                        boolean isStatic = isModifierSet(classModifiers, Modifier.STATIC);
                        if (!isFinal & !isStatic) {
                            LinkedList<String> listParamTypes = new LinkedList<>();
                            for (Class cls : method.getParameterTypes())
                            {
                                listParamTypes.add(cls.getName());
                            }
                            methodsMap.put(method.getName(), listParamTypes);
                            returnTypeMap.put(method.getName(), method.getReturnType().getName());
                        }
                    }

                    try {
                        generateCode(className, methodsMap, returnTypeMap, superClassName);
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

    private void generateCode(String className, LinkedHashMap<String, LinkedList<String>> methodsMap, LinkedHashMap<String, String> returnTypeMap, String superClassName) throws IOException, ClassNotFoundException {
        String packageName = null;
        int lastDot = className.lastIndexOf('.');
        if (lastDot > 0) {
            packageName = className.substring(0, lastDot);
        }
        String generatedClassName = className + "Generated";
        String generatedSimpleClassName = generatedClassName.substring(lastDot + 1);
        JavaFileObject builderFile = processingEnv.getFiler().createSourceFile(generatedClassName);
        try (PrintWriter out = new PrintWriter(builderFile.openWriter())) {
            if (packageName != null) {
                out.print("package " + packageName + ";");
                out.println();
            }
            out.println();
            out.println("import java.util.*;");
            out.println();
            out.print("public class " + generatedSimpleClassName + " extends javax.swing.");
            out.print(superClassName.substring(12));
            out.print(" {");
            out.println();

            out.println();
            out.print("    " + generatedClassName + " new");
            out.print(superClassName.substring(13) + ";");
            out.println();

            buildClassConstructor(out, generatedSimpleClassName, superClassName, generatedClassName);

            methodsMap.entrySet().forEach(method -> {
                String methodName = method.getKey();

                if (methodName != "createVolatileImage" && methodName != "createBufferStrategy") {
                    LinkedList<String> argumentType = method.getValue();
                    out.println("@Override");
                    out.println();
                    out.print("    public ");

                    String newReturnTypeName = returnTypeMap.get(methodName);

                    if (returnTypeMap.get(methodName).startsWith("[")) {
                        int endChar = returnTypeMap.get(methodName).length();
                        newReturnTypeName = returnTypeMap.get(methodName).substring(2, endChar - 1) + "[]";
                    }

                    if (newReturnTypeName.contains("$")) {
                        newReturnTypeName = newReturnTypeName.replace("$", ".");
                    }

                    if (methodName == "getUI") {
                        out.print("javax.swing.plaf." + superClassName.substring(13) + "UI");
                    } else {
                        out.print(newReturnTypeName);
                    }
                    out.print(" " + methodName + "(");
                    int i = 0;

                    for (String s : argumentType) {
                        i++;
                        if (s.contains("$")) {
                            s = s.replace("$", ".");
                        }
                        out.print(s + " value" + i);
                        if (argumentType.size() != i) {
                            out.print(", ");
                        }
                    }

                    out.print(") {");
                    out.println();

                    addOperationToList(out, argumentType, generatedClassName, methodName, superClassName);

                    if (newReturnTypeName != "void") {
                        out.print("   return super.");
                    } else {
                        out.print("   super.");
                    }
                    out.print(methodName);
                    out.print("(");
                    int j = 0;
                    for (String s : argumentType) {
                        j++;
                        out.print("value" + j);
                        if (argumentType.size() != j) {
                            out.print(", ");
                        }
                    }
                    out.print(");");
                    out.println();
                    out.println("   }");
                    out.println();
                } else {
                    // добавить обработку методов с исключениями
                }
            });
            out.println("}");
        }
    }


    private void addOperationToList(PrintWriter out, LinkedList<String> argumentType, String generatedClassName, String methodName, String superClassName){

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

        out.print("   GlobalList.list.add(new Operation(");
        out.print("\"" + generatedClassName + "\", \"" + methodName + "\", paramValuesMap, this.new" + superClassName.substring(13) + "));");
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
        out.print("this.new" + superClassName.substring(13) + " = this;");
        out.print("""
                    LinkedHashMap<Class, Object> paramValuesMap = new LinkedHashMap<>();
                    GlobalList.list.add(new Operation(                
                """);
        out.print("\"" + generatedClassName + "\", \"createMy" + superClassName.substring(13) + "Generated\", paramValuesMap, ");
        out.print("this.new" + superClassName.substring(13) + "));");
        out.println("}");
        out.println();
    }
}



