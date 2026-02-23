/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package net.quickwrite.confetti.processor;

import net.quickwrite.confetti.processor.data.UnknownDependencyList;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import java.util.*;

@SupportedAnnotationTypes("net.quickwrite.confetti.ConfettiConfig")
@SupportedSourceVersion(SourceVersion.RELEASE_21)
public class ConfettiProcessor extends AbstractProcessor {
    private UnknownDependencyList unknownDependencyList;

    public synchronized void init(final ProcessingEnvironment processingEnv) {
        super.init(processingEnv);

        unknownDependencyList = new UnknownDependencyList();
    }

    /** {@inheritDoc} */
    @Override
    public boolean process(final Set<? extends TypeElement> annotations, final RoundEnvironment roundEnv) {
        for (final TypeElement annotation : annotations) {
            // Find elements annotated with the custom annotation
            for (final Element element : roundEnv.getElementsAnnotatedWith(annotation)) {
                // Process each element
                processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "Processing: " + element.getSimpleName());

                // TODO: Get type resolvers

                this.processConfettiConfig(element);
            }
        }

        final Collection<TypeElement> finalizedElements = unknownDependencyList.collectKnown();
        // TODO: Do something with those elements

        return true; // Claim the ConfettiConfigs
    }

    private void processConfettiConfig(final Element element) {
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
