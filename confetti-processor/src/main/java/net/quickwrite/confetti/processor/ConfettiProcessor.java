/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package net.quickwrite.confetti.processor;

import net.quickwrite.confetti.ConfettiConfig;
import net.quickwrite.confetti.resolver.ConfettiTypeResolver;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import java.lang.annotation.Annotation;
import java.util.*;
import java.util.stream.Collectors;

@SupportedAnnotationTypes({"net.quickwrite.confetti.ConfettiConfig","net.quickwrite.confetti.resolver.ConfettiTypeResolver"})
@SupportedSourceVersion(SourceVersion.RELEASE_21)
public class ConfettiProcessor extends AbstractProcessor {
    private Collection<SimpleProcessor> processors;

    public synchronized void init(final ProcessingEnvironment processingEnv) {
        super.init(processingEnv);

        processors = List.of(
                new ConfigProcessor(processingEnv),
                new TypeResolverProcessor(processingEnv)
        );
    }

    /** {@inheritDoc} */
    @Override
    public boolean process(final Set<? extends TypeElement> annotations, final RoundEnvironment roundEnv) {
        for (final TypeElement annotation : annotations) {
            // Find elements annotated with the custom annotation
            for (final SimpleProcessor processor : processors) {
                if (!annotation.asType().toString().equals(processor.annotation().getCanonicalName())) {
                    continue;
                }

                processor.process(annotation, roundEnv);
            }
        }

        for (final SimpleProcessor processor : processors) {
            processor.roundEnding(roundEnv);
        }

        return true; // Claim the ConfettiConfigs
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return this.processors.stream().map(p -> p.annotation().getCanonicalName()).collect(Collectors.toUnmodifiableSet());
    }
}
