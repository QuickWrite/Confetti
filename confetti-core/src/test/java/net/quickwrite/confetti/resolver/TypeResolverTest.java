/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package net.quickwrite.confetti.resolver;
import net.quickwrite.confetti.ConfigNode;
import net.quickwrite.confetti.exception.ConversionException;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Type;

import static org.junit.jupiter.api.Assertions.*;

public class TypeResolverTest {

    @ConfettiTypeResolver({ String.class, CharSequence.class })
    private static final class AnnotatedStringResolver implements TypeResolver<String> {
        @Override
        public String fromNode(final ConfigNode node, final Type type) {
            // For test purposes we ignore the node and type and return a sentinel value
            return "converted";
        }
    }

    private static final class UnannotatedResolver implements TypeResolver<Object> {
        @Override
        public Object fromNode(final ConfigNode node, final Type type) {
            return new Object();
        }
    }

    private static final class OverridingResolver implements TypeResolver<Integer> {
        @Override
        public Integer fromNode(final ConfigNode node, final Type type) {
            return 123;
        }

        @Override
        public Class<?>[] supportedTypes() {
            return new Class<?>[] { Integer.class, int.class };
        }
    }

    @Test
    public void supportedTypesReadsAnnotationValue() {
        TypeResolver<String> resolver = new AnnotatedStringResolver();

        Class<?>[] supported = resolver.supportedTypes();

        assertArrayEquals(new Class<?>[] { String.class, CharSequence.class }, supported,
                "supportedTypes should return the classes declared in the ConfettiTypeResolver annotation");
    }

    @Test
    public void supportedTypesThrowsWhenAnnotationMissing() {
        TypeResolver<Object> resolver = new UnannotatedResolver();

        assertThrows(NullPointerException.class, resolver::supportedTypes,
                "Default supportedTypes should throw NullPointerException when the ConfettiTypeResolver annotation is missing");
    }

    @Test
    public void overridingSupportedTypesDoesNotRequireAnnotation() {
        TypeResolver<Integer> resolver = new OverridingResolver();

        Class<?>[] supported = resolver.supportedTypes();

        assertArrayEquals(new Class<?>[] { Integer.class, int.class }, supported,
                "A resolver that overrides supportedTypes should return the overridden values even without an annotation");
    }

    @Test
    public void fromNodeImplementationIsInvoked() throws ConversionException {
        TypeResolver<String> resolver = new AnnotatedStringResolver();

        String result = resolver.fromNode(null, String.class);

        assertEquals("converted", result, "fromNode should return the value produced by the resolver implementation");
    }
}

