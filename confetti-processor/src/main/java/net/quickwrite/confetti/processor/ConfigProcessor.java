/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package net.quickwrite.confetti.processor;

import net.quickwrite.confetti.ConfettiConfig;
import net.quickwrite.confetti.processor.data.UnknownDependencyList;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.*;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.util.*;

/**
 * The processor that processes the {@link ConfettiConfig}'s.
 */
public final class ConfigProcessor implements SimpleProcessor {
    private final UnknownDependencyList unknownDependencyList;
    private final ProcessingEnvironment processingEnv;

    public ConfigProcessor(final UnknownDependencyList unknownDependencyList, final ProcessingEnvironment processingEnv) {
        this.unknownDependencyList = unknownDependencyList;
        this.processingEnv = processingEnv;
    }

    @Override
    public void process(final TypeElement annotation, final RoundEnvironment roundEnv) {
        for (final Element element : roundEnv.getElementsAnnotatedWith(annotation)) {
            this.processConfettiConfig(element);
        }
    }

    @Override
    public void roundEnding(final RoundEnvironment roundEnv) {
        final Collection<TypeElement> finalizedElements = unknownDependencyList.collectKnown();
        // TODO: Do something with those elements

        if (!roundEnv.processingOver()) {
            return;
        }

        final Collection<TypeElement> currentConfigs = this.unknownDependencyList.collectCurrentConfigs();

        if (currentConfigs.isEmpty()) {
            return; // All good!
        }

        final Set<TypeMirror> missingTypes = this.unknownDependencyList.missingTypes();

        final StringBuilder builder = new StringBuilder();
        builder.append("Could not resolve all dependencies for all interfaces annotated with @ConfettiConfig.\n")
                .append("Affected interfaces:\n");
        for (final TypeElement typeElement : currentConfigs) {
            builder.append(" - ").append(typeElement).append('\n');
        }

        builder.append("Affected types:\n");

        for (final TypeMirror typeMirror : missingTypes) {
            builder.append(" - ").append(typeMirror).append('\n');
        }

        builder.append("It is not possible to generate sources with unknown type resolvers.\n")
                .append("Suggestion: Fix by adding type resolvers for the listed types.");

        processingEnv.getMessager().printError(builder.toString());
    }

    @Override
    public Class<?> annotation() {
        return ConfettiConfig.class;
    }

    private void processConfettiConfig(final Element element) {
        if (element.getKind() != ElementKind.INTERFACE) {
            final String message = "Only interfaces can be annotated with @ConfettiConfig\n" +
                    "This error appeared when processing " + element + '\n' +
                    "Suggestion: Fix by removing @ConfettiConfig from " + element;

            processingEnv.getMessager().printError(message);

            return;
        }

        final Set<TypeMirror> collectedTypes = new HashSet<>();

        for (final ExecutableElement instanceMethod : this.getAllInstanceMethods((TypeElement) element, false)) {
            // Skip methods that return nothing
            if (instanceMethod.getReturnType().getKind() == TypeKind.VOID) {
                continue;
            }

            collectedTypes.add(instanceMethod.getReturnType());
        }

        unknownDependencyList.addConfigWithDependencies((TypeElement) element, collectedTypes.stream().toList());
    }

    /**
     * Returns every instance (non‑static) method that a class inherits or declares,
     * excluding methods from java.lang.Object if you wish (set includeObject = false).
     *
     * <p>
     * The list only includes the highest methods in terms of inheritance:
     * {@snippet :
     * class A {
     *     String test() {
     *         // code
     *     }
     * }
     *
     * class B extends A {
     *     @Overwrite
     *     String test() { // <-- The output will only include this method when providing 'B'
     *         // code
     *     }
     * }
     * }
     *
     * @param start The {@link TypeElement} that should be checked upon
     * @param includeObject If the methods of {@link Object} should be added
     * @return A list of {@link ExecutableElement}s that represents all methods
     */
    private List<ExecutableElement> getAllInstanceMethods(final TypeElement start, final boolean includeObject) {
        final Elements elems = processingEnv.getElementUtils();
        final Types types = processingEnv.getTypeUtils();
        final List<ExecutableElement> result = new ArrayList<>();

        // Queue for breadth‑first traversal of the type hierarchy
        final Queue<TypeElement> queue = new LinkedList<>();
        queue.add(start);

        while (!queue.isEmpty()) {
            TypeElement cur = queue.poll();

            if (!includeObject && cur.getQualifiedName().contentEquals("java.lang.Object")) {
                continue;
            }

            for (final Element e : cur.getEnclosedElements()) {
                if (e.getKind() != ElementKind.METHOD) continue;
                ExecutableElement method = (ExecutableElement) e;

                if (method.getModifiers().contains(Modifier.STATIC)) continue;

                // Is the method being overwritten?
                final boolean overwritten = result.stream().anyMatch(m -> elems.overrides(m, method, start));
                if (overwritten) {
                    continue;
                }

                result.add(method);
            }

            for (final TypeMirror superTm : types.directSupertypes(cur.asType())) {
                final Element superEl = types.asElement(superTm);

                if (superEl instanceof TypeElement element) {
                    queue.add(element);
                }
            }
        }

        return result;
    }
}
