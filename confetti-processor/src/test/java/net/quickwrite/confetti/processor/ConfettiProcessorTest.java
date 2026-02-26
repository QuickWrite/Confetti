/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package net.quickwrite.confetti.processor;

import net.quickwrite.confetti.ConfettiConfig;
import net.quickwrite.confetti.resolver.ConfettiTypeResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ConfettiProcessorTest {
    // Stub of a ProcessingEnvironment that does nothing.
    private static class SimpleProcessingEnvironment implements ProcessingEnvironment {
        @Override
        public Map<String, String> getOptions() { return Map.of(); }

        @Override
        public Messager getMessager() { return null; }

        @Override
        public Filer getFiler() { return null; }

        @Override
        public Elements getElementUtils() { return null; }

        @Override
        public Types getTypeUtils() { return null; }

        @Override
        public SourceVersion getSourceVersion() { return null; }

        @Override
        public Locale getLocale() { return null; }
    }

    private ConfettiProcessor confettiProcessor;

    @BeforeEach
    public void setUp() {
        this.confettiProcessor = new ConfettiProcessor();

        this.confettiProcessor.init(new SimpleProcessingEnvironment());
    }

    @Test
    public void correctSupportedAnnotationTypes() {
        Set<String> supportedAnnotations = this.confettiProcessor.getSupportedAnnotationTypes();

        assertEquals(Set.of(ConfettiConfig.class.getCanonicalName(), ConfettiTypeResolver.class.getCanonicalName()), supportedAnnotations);
    }
}
