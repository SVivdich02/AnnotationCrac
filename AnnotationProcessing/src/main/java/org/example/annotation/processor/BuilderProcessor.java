package org.example.annotation.processor;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.*;
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
            /*
            Set<? extends Element> annotatedElements = roundEnv.getElementsAnnotatedWith(annotation);

            Map<String, String> methodsMap = new HashMap<>();
            for (Element element : annotatedElements)
            {
                if (element.getKind().isClass())
                {
                    for (Method m : element.getClass().getDeclaredMethods())
                    {
                        methodsMap.put(m.getName(), "String");
                    }
                }
                String className = element.getSimpleName().toString();
                //String className = ((TypeElement) element.getEnclosingElement()).getQualifiedName().toString();

                try {
                    writeBuilderFile(className, methodsMap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            */

            Set<? extends Element> annotatedElements = roundEnv.getElementsAnnotatedWith(annotation);
            Map<Boolean, List<Element>> annotatedMethods = annotatedElements.stream().
                    collect(Collectors.partitioningBy(element  -> ((ExecutableType)element.asType()).getParameterTypes().size()==1));
            List<Element> suitableMethods = annotatedMethods.get(true);

            String className = ((TypeElement) suitableMethods.get(0).getEnclosingElement()).getQualifiedName().toString();
            Map<String,String> suitableMethodsMap  = suitableMethods.stream()
                    .collect(Collectors.toMap(method -> method.getSimpleName().toString(),
                            method -> ((ExecutableType)method.asType()).getParameterTypes().get(0).toString()));
            try {
                generateCode(className, suitableMethodsMap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }
    private void generateCode(String className, Map<String, String> suitableMethodsMap) throws IOException {
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
            out.print("public class ");
            out.print(generatedSimpleClassName);
            out.println("{");
            out.println();
            out.print("    private ");
            out.print(simpleClassName);
            out.print(" object = new ");
            out.print(simpleClassName);
            out.println("();");
            out.println();

            out.print("    public ");
            out.print(simpleClassName);
            out.println(" build() {");
            out.println("        return object;");
            out.println("    }");
            out.println();

            suitableMethodsMap.entrySet().forEach(method -> {
                String methodName = method.getKey();
                String argumentType = method.getValue();
                out.print("    public void ");
                out.print(methodName);
                out.print("(");
                out.print(argumentType);
                out.println(" value) {");
                out.print("        //super."); // в сгенерированном классе
                out.print(methodName);         // этого
                out.println("(value);");       // нет
                out.println("       Boolean b = false;");
                out.println("    }");
                out.println();
            });
            out.println("}");
        }
    }
}

