package com.blexven.injector;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Set;

@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class Injector extends AbstractProcessor {

    private Elements elementUtils;
    private Filer filer;
    private Messager messager;

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Collections.singleton(PleaseWork.class.getCanonicalName());
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        elementUtils = processingEnv.getElementUtils();
        filer = processingEnv.getFiler();
        messager = processingEnv.getMessager();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        for (Element element : roundEnv.getElementsAnnotatedWith(PleaseWork.class)) {

            Element enclosingElement = element.getEnclosingElement();
            VariableElement annotatedVariable = (VariableElement) element;

            String generatedClassName = enclosingElement.getSimpleName() + "Injected";
            PackageElement packageName = elementUtils.getPackageOf(element);

            try {
                JavaFileObject sourceFile = filer.createSourceFile(generatedClassName, element);

                try (OutputStream out = sourceFile.openOutputStream()) {

                    String type = simpleTypeOf(annotatedVariable);

                    String dependencyToBeInjected = "Hello";
                    String injectedConstructorCall = "new " + dependencyToBeInjected + "()";

                    String contents =

                            "package " + packageName + ";\n\n" +

                            "public class " + generatedClassName + " {\n\t" +

                            "private String asType = \"" + annotatedVariable.asType() + "\";\n\t" +

                            "private " + type + " " + annotatedVariable + " = " + injectedConstructorCall + ";\n" +

                            "}";

                    out.write(contents.getBytes());

                    printGeneratedContents(contents);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return true; // signal that we're done with processing
    }

    private void printGeneratedContents(String contents) {
        messager.printMessage(Diagnostic.Kind.WARNING, "Contents: \n\n" + contents + "\n");
    }


    private String simpleTypeOf(VariableElement annotatedVariable) {
        int lastIndex = annotatedVariable.asType().toString()
                                         .lastIndexOf(".");
        return annotatedVariable.asType().toString().substring(lastIndex + 1);
    }
}
