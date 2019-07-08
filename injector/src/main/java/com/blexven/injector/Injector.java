package com.blexven.injector;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.util.Set;

@SupportedAnnotationTypes("com.blexven.injector.PleaseWork")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class Injector extends AbstractProcessor {
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        for (TypeElement annotation : annotations) {
            roundEnv.getElementsAnnotatedWith(annotation).forEach(element -> {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING, "Inject here, please: " + element);
            });
        }


        return false;
    }
}
