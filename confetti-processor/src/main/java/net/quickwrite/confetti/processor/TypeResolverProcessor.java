/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package net.quickwrite.confetti.processor;

import net.quickwrite.confetti.processor.data.UnknownDependencyList;
import net.quickwrite.confetti.resolver.ConfettiTypeResolver;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.*;
import javax.lang.model.type.TypeMirror;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * The processor that processes the {@link ConfettiTypeResolver}'s.
 */
public final class TypeResolverProcessor implements SimpleProcessor {
    private final UnknownDependencyList unknownDependencyList;
    private final ProcessingEnvironment processingEnv;

    public TypeResolverProcessor(final UnknownDependencyList unknownDependencyList, final ProcessingEnvironment processingEnv) {
        this.unknownDependencyList = unknownDependencyList;
        this.processingEnv = processingEnv;
    }

    @Override
    public void process(final TypeElement annotation, final RoundEnvironment roundEnv) {
        for (final Element element : roundEnv.getElementsAnnotatedWith(annotation)) {
            for(final TypeMirror type: getClassTypedAnnotationValues(
                    element,
                    ConfettiTypeResolver.class.getCanonicalName(),
                    "value",
                    processingEnv)
            ) {
                unknownDependencyList.setAvailable(type);

                // TODO: Error out if type already exists

                // TODO: Implement actual resolution of resolvers
            }
        }
    }

    @Override
    public void roundEnding(final RoundEnvironment roundEnv) {}

    @Override
    public Class<?> annotation() {
        return ConfettiTypeResolver.class;
    }

    /**
     * Extracts TypeMirror values from an annotation element of type Class or Class[].
     *
     * @param element the element that is annotated (e.g. a TypeElement or ExecutableElement)
     * @param annotationQualifiedName fully-qualified annotation class name (e.g. "com.example.TypeResolver")
     * @param annotationElementName the annotation element name (e.g. "value")
     * @param processingEnv the processing environment (available in your processor)
     * @return a list of TypeMirror objects; never null (empty if nothing found)
     */
    private static List<TypeMirror> getClassTypedAnnotationValues(
            final Element element,
            final String annotationQualifiedName,
            final String annotationElementName,
            final ProcessingEnvironment processingEnv
    ) {
        for (final AnnotationMirror annotationMirror : element.getAnnotationMirrors()) {
            if (!annotationQualifiedName.equals(annotationMirror.getAnnotationType().toString())) {
                continue;
            }

            final Map<? extends ExecutableElement, ? extends AnnotationValue> values =
                                        processingEnv.getElementUtils().getElementValuesWithDefaults(annotationMirror);

            for (final Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : values.entrySet()) {
                if (!annotationElementName.contentEquals(entry.getKey().getSimpleName())) {
                    continue;
                }

                final AnnotationValue value = entry.getValue();
                final Object content = value.getValue();

                // If the annotation element is an array, the value will be a List<AnnotationValue>
                if (content instanceof List) {
                    @SuppressWarnings("unchecked")
                    final List<? extends AnnotationValue> list = (List<? extends AnnotationValue>) content;

                    return list.stream()
                            .map(val -> (TypeMirror) val.getValue())
                            .collect(Collectors.toList());
                }

                if (content instanceof TypeMirror) {
                    return Collections.singletonList((TypeMirror) content);
                }

                return Collections.emptyList();
            }
        }

        return Collections.emptyList();
    }
}
