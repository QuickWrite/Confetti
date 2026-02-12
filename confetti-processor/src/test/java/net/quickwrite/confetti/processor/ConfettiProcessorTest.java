/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package net.quickwrite.confetti.processor;

import net.quickwrite.confetti.ConfettiConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ConfettiProcessorTest {
    private ConfettiProcessor confettiProcessor;

    @BeforeEach
    public void setUp() {
        this.confettiProcessor = new ConfettiProcessor();
    }

    @Test
    public void correctSupportedAnnotationTypes() {
        Set<String> supportedAnnotations = this.confettiProcessor.getSupportedAnnotationTypes();

        assertEquals(Set.of(ConfettiConfig.class.getCanonicalName()), supportedAnnotations);
    }
}
