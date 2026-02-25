/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package net.quickwrite.confetti.processor;

import net.quickwrite.confetti.resolver.ConfettiTypeResolver;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.TypeElement;

/**
 * The processor that processes the {@link ConfettiTypeResolver}'s.
 */
public final class TypeResolverProcessor implements SimpleProcessor {
    private final ProcessingEnvironment processingEnv;

    public TypeResolverProcessor(final ProcessingEnvironment processingEnv) {
        this.processingEnv = processingEnv;
    }

    @Override
    public void process(final TypeElement annotation, final RoundEnvironment roundEnv) {
        processingEnv.getMessager().printNote("ConfettiTypeResolver");
    }

    @Override
    public void roundEnding(final RoundEnvironment roundEnv) {}

    @Override
    public Class<?> annotation() {
        return ConfettiTypeResolver.class;
    }
}
