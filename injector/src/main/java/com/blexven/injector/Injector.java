package com.blexven.injector;

import com.sun.tools.javac.model.JavacElements;
import com.sun.tools.javac.model.JavacTypes;
import com.sun.tools.javac.processing.JavacFiler;
import com.sun.tools.javac.processing.JavacMessager;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Set;

@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class Injector extends AbstractProcessor {

    private JavacElements javacElements;
    private JavacFiler javacFiler;
    private JavacMessager javacMessager;
    private JavacProcessingEnvironment javacProcessingEnvironment;
    private JavacTypes javacTypeUtils;

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Collections.singleton(PleaseWork.class.getCanonicalName());
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);

        // cast to a more specific implementation, to use extra features
        javacProcessingEnvironment = ((JavacProcessingEnvironment) processingEnv);
        javacElements = javacProcessingEnvironment.getElementUtils();
        javacFiler = ((JavacFiler) processingEnv.getFiler());
        javacMessager = ((JavacMessager) this.javacProcessingEnvironment.getMessager());
        javacTypeUtils = javacProcessingEnvironment.getTypeUtils();

    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        for (Element element : roundEnv.getElementsAnnotatedWith(PleaseWork.class)) {

            if (!element.getKind().isField())
                continue;

            Element enclosingElement = element.getEnclosingElement();
            VariableElement annotatedVariable = (VariableElement) element;

            String generatedClassName = enclosingElement.getSimpleName() + "Injected";
            PackageElement packageName = javacElements.getPackageOf(element);

            try {
                JavaFileObject sourceFile = javacFiler.createSourceFile(generatedClassName, element);


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
        javacMessager.printWarning("Contents: \n\n" + contents + "\n");
    }


    private String simpleTypeOf(VariableElement annotatedVariable) {
        int lastIndex = annotatedVariable.asType().toString()
                                         .lastIndexOf(".");
        return annotatedVariable.asType().toString().substring(lastIndex + 1);
    }
}
