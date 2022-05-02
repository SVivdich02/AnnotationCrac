package org.example.annotation.processor;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.sql.Array;
import java.util.*;
import javax.lang.model.element.Name;
import javax.swing.JButton;
import java.util.stream.Collectors;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.TypeMirror;
import javax.swing.*;
import javax.tools.Diagnostic;
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

            Map<String, List<String>> methodsMap = new HashMap<>();
            Map<String, String> returnTypeMap = new HashMap<>();
            for (Element element : annotatedElements)
            {
                JButton but = new JButton();
                //String superClassName = element.getSimpleName().toString(); //-- это MyJButton
                //String superClassName = ((TypeElement)element).getSuperclass().toString().substring(12); //-- это Jbutton

                String className = ((TypeElement) element).getQualifiedName().toString();

                for (Method method : but.getClass().getMethods())
                {
                    int classModifiers = method.getModifiers();
                    boolean isFinal = isModifierSet(classModifiers, Modifier.FINAL);
                    boolean isStatic = isModifierSet(classModifiers, Modifier.STATIC);
                    if (!isFinal & !isStatic)
                    {
                        List<String> arr = new ArrayList<String>();
                        for (Class cl : method.getParameterTypes())
                        {
                            arr.add(cl.getName());
                        }
                        methodsMap.put(method.getName(), arr);
                        returnTypeMap.put(method.getName(), method.getReturnType().getName());
                    }
                }

                try {
                        generateCode(className, methodsMap, returnTypeMap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
            }
        }
        return true;
    }

    public static boolean isModifierSet(int allModifiers, int specificModifier) {
        return (allModifiers & specificModifier) > 0;
    }

    private void generateCode(String className, Map<String, List<String>> suitableMethodsMap, Map<String, String> returnTypeMap) throws IOException {
        String packageName = null;
        int lastDot = className.lastIndexOf('.');
        if (lastDot > 0) {
            packageName = className.substring(0, lastDot);
        }
        String simpleClassName = className.substring(lastDot + 1);
        String generatedClassName = className + "Generated";
        String generatedSimpleClassName = generatedClassName.substring(lastDot + 1);
        JavaFileObject builderFile = processingEnv.getFiler().createSourceFile(generatedClassName);
        try (PrintWriter out = new PrintWriter(builderFile.openWriter())) {
            if (packageName != null) {
                out.print("package ");
                out.print(packageName);
                out.println(";");
                out.println();
            }
            out.println();
            out.println("import java.util.*;");
            out.println();
            out.print("public class ");
            out.print(generatedSimpleClassName);
            out.println(" extends javax.swing.JButton {");
            out.println();

            out.println("public java.awt.image.VolatileImage createVolatileImage(int value1, int value2, java.awt.ImageCapabilities value3) {\n" +
                    "        throw new RuntimeException(\"don't call me\");\n" +
                    "    }");

            out.println();
            out.print("    ");
            out.print(generatedSimpleClassName);
            out.print(" newButton;");
            out.println();

            // конструктор
            out.print("    public ");
            out.print(generatedSimpleClassName);
            out.print("() {");
            out.println("   super();");
            out.println();
            out.println("   LinkedHashMap<String, Object> paramValuesMap = new LinkedHashMap<>();");
            out.println();
            out.print("GlobalList.list.add(new Operation(");
            out.print("\"");
            out.print(generatedSimpleClassName);
            out.print("\"");
            out.print(", \"CreateMyButtonGenerated\"");
            out.print(", paramValuesMap, this));");
            out.println();
            out.println("this.newButton = this;");
            //out.println("String a = ");
            //out.print(name);
            //out.print(";");
            out.println("}");

            out.println();

            suitableMethodsMap.entrySet().forEach(method -> {
                String methodName = method.getKey();
                if (methodName != "createVolatileImage") {
                    List<String> argumentType = method.getValue();
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
                        out.print("javax.swing.plaf.ButtonUI");
                    } else {
                        out.print(newReturnTypeName);
                    }
                    out.print(" ");
                    out.print(methodName);
                    out.print("(");
                    int i = 0;
                    for (String s : argumentType) {
                        i++;
                        if (s.contains("$")) {
                            s = s.replace("$", ".");
                        }
                        out.print(s);
                        out.print(" ");
                        out.print("value");
                        out.print(i);
                        if (argumentType.size() != i) {
                            out.print(", ");
                        }
                    }

                    out.print(") {");
                    out.println();


                    // добавление операции в лист
                    i = 0;
                    out.println("   LinkedHashMap<String, Object> paramValuesMap = new LinkedHashMap<>();");
                    out.println();
                    for (String s : argumentType) {
                        i++;
                        if (s.contains("$")) {
                            s = s.replace("$", ".");
                        }

                        if (s.contains("bool") || s.contains("int") || s.contains("floa") || s.contains("doubl") || s.contains("cha") || s.contains("long"))
                        {
                            out.print("String nameSpecial");
                            out.print(i);
                            out.print(" = ");
                            out.print(s);
                            out.print(".class.getSimpleName();");
                            out.print(" paramValuesMap.put(nameSpecial");

                        }
                        else {
                            out.print("String name");
                            out.print(i);
                            out.print(" = value");
                            out.print(i);
                            out.print(".getClass().getSimpleName();");
                            out.println();
                            out.print(" paramValuesMap.put(name");
                        }
                        out.print(i);
                        out.print(", (Object) value");
                        out.print(i);
                        out.print(");");
                        out.println();
                    }
                    out.println();
                    out.print("   GlobalList.list.add(new Operation(");
                    out.print("\"");
                    out.print(generatedSimpleClassName);
                    out.print("\"");
                    out.print(", \"");
                    out.print(methodName);
                    out.print("\"");
                    out.print(", paramValuesMap, this));");
                    out.println();
                    // операция добавлена


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
                        out.print("value");
                        out.print(j);
                        if (argumentType.size() != j) {
                            out.print(", ");
                        }
                    }
                    out.print(");");
                    out.println();
                    out.println("   }");
                    out.println();
                }
            });
            out.println("}");
        }
    }
}

