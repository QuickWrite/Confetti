/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package net.quickwrite.confetti.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ConfigParseExceptionTest {

    @Test
    void messageIsPreservedAndCauseIsNullWhenOnlyMessageProvided() {
        String msg = "parse failed";
        ConfigParseException ex = new ConfigParseException(msg);

        assertEquals(msg, ex.getMessage());
        assertNull(ex.getCause());
        assertInstanceOf(RuntimeException.class, ex);
    }

    @Test
    void messageAndCauseArePreservedWhenBothProvided() {
        String msg = "parse failed due to X";
        Throwable cause = new IllegalArgumentException("bad input");

        ConfigParseException ex = new ConfigParseException(msg, cause);

        assertEquals(msg, ex.getMessage());
        assertSame(cause, ex.getCause());
    }

    @Test
    void causeOnlyConstructorSetsMessageToCauseToString() {
        Throwable cause = new NullPointerException("oops");
        ConfigParseException ex = new ConfigParseException(cause);

        assertSame(cause, ex.getCause());
        assertEquals(cause.toString(), ex.getMessage());
    }

    @Test
    void nullCauseLeadsToNullMessageAndNullCause() {
        ConfigParseException ex = new ConfigParseException((Throwable) null);

        assertNull(ex.getCause());
        assertNull(ex.getMessage());
    }
}
