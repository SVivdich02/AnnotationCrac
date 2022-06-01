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

        String fieldName = superClassName.substring(13);
        out.print("   GlobalList.list.add(new Operation(");
        out.print("\"" + generatedClassName + "\", \"" + methodName + "\", paramValuesMap, this.new" + fieldName + "));");
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
        out.println("this.new" + superClassName.substring(13) + " = this;");
        out.print("""
                    LinkedHashMap<Class, Object> paramValuesMap = new LinkedHashMap<>();
                    GlobalList.list.add(new Operation(                
                """);
        out.print("\"" + generatedClassName + "\", \"createMy" + superClassName.substring(13) + "Generated\", paramValuesMap, this.new" + superClassName.substring(13) + "));");
        out.println("}");
        out.println();
    }


    private void generateClass(String className, String superClassName, Class currentClass) throws IOException {
        int b = 0;
        HashMap<HashMap<String, LinkedList<String>>, Boolean> addedMethods = new HashMap<>();
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

            while (true) {
                String currentClassName = currentClass.getName();
                if (currentClassName.contains("Obj")) {
                    break;
                }

                for (Method method : currentClass.getMethods()) {
                    LinkedList<String> listParamTypes = new LinkedList<>();
                    for (Class cls : method.getParameterTypes()) {
                        listParamTypes.add(cls.getName());
                    }
                    HashMap<String, LinkedList<String>> newMethod = new HashMap<>();
                    newMethod.put(method.getName(), listParamTypes);
                    if (!addedMethods.containsKey(newMethod)) {
                        generateMethod(method, out, currentClassName, generatedClassName);
                        addedMethods.put(newMethod, true);
                    }
                }
                String superClass = currentClass.getSuperclass().getName();
                Class currentSuperClass = Class.forName(superClass);
                currentClass = currentSuperClass;
            }
            out.println("   }");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void generateMethod(Method method, PrintWriter out, String superClassName, String generatedClassName) {
        String methodName = method.getName();

        String returnType = updateReturnTypeName(method.getReturnType().getName());
        if (methodName == "getUI") {
            returnType = "javax.swing.plaf." + superClassName.substring(13) + "UI";
        }

        int classModifiers = method.getModifiers();
        boolean isFinal = isModifierSet(classModifiers, Modifier.FINAL);
        boolean isStatic = isModifierSet(classModifiers, Modifier.STATIC);

        Class[] exceptions = method.getExceptionTypes();
        if (!isFinal && !isStatic) {
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

            addOperationToList(out, listParamTypes, generatedClassName, methodName, superClassName);
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



