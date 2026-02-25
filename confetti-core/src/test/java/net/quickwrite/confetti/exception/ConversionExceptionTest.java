/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package net.quickwrite.confetti.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ConversionExceptionTest {

    @Test
    public void constructorSetsMessage() {
        ConversionException ex = new ConversionException("failed to convert");
        assertEquals("failed to convert", ex.getMessage(), "message should be preserved");
    }

    @Test
    public void constructorSetsMessageAndCause() {
        Throwable cause = new IllegalArgumentException("bad input");
        ConversionException ex = new ConversionException("conversion failed", cause);

        assertEquals("conversion failed", ex.getMessage(), "message should be preserved");
        assertSame(cause, ex.getCause(), "cause should be preserved");
    }

    @Test
    public void stackTraceIsAvailable() {
        ConversionException ex = new ConversionException("boom");
        StackTraceElement[] trace = ex.getStackTrace();
        assertNotNull(trace, "stack trace should not be null");
        assertTrue(trace.length > 0, "stack trace should contain at least one element");
    }

    @Test
    public void causeCanBeNull() {
        ConversionException ex = new ConversionException("no cause", null);
        assertNull(ex.getCause(), "cause may be null");
    }

    @Test
    public void exceptionIsRuntimeException() {
        ConversionException ex = new ConversionException("runtime");

        assertInstanceOf(Exception.class, ex, "ConversionException should extend Exception");
    }
}
