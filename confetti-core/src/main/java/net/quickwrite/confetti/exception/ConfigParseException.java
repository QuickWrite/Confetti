/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package net.quickwrite.confetti.exception;

/**
 * Thrown to indicate a problem while parsing configuration text or streams.
 *
 * <p>
 * This unchecked exception is used to signal both syntactic parse errors
 * and unexpected I/O problems that occur during parsing when the API does not
 * declare checked I/O exceptions. It is intentionally lightweight and carries
 * an optional cause for diagnostic purposes.
 *
 * <p>Typical usage:
 * {@snippet :
 *   try {
 *     return factory.parse(reader);
 *   } catch (IOException | SomeParsingLibraryException e) {
 *     throw new ConfigParseException("failed to parse configuration", e);
 *   }
 * }
 */
public class ConfigParseException extends RuntimeException {
    /**
     * Constructs a new exception with the specified detail message.
     * The cause is not initialized, and may subsequently be initialized by a
     * call to {@link #initCause}.
     *
     * @param message the detail message. The detail message is saved for
     *        later retrieval by the {@link #getMessage()} method.
     */
    public ConfigParseException(final String message) {
        super(message);
    }

    /**
     * Constructs a new exception with the specified detail message and
     * cause.
     *
     * <p>
     * Note that the detail message associated with
     * {@code cause} is <i>not</i> automatically incorporated in
     * this runtime exception's detail message.
     *
     * @param message the detail message (which is saved for later retrieval
     *        by the {@link #getMessage()} method).
     * @param cause the cause (which is saved for later retrieval by the
     *        {@link #getCause()} method).  (A {@code null} value is
     *        permitted, and indicates that the cause is nonexistent or
     *        unknown.)
     */
    public ConfigParseException(final String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new exception with the specified cause and a
     * detail message of {@code (cause==null ? null : cause.toString())}
     * (which typically contains the class and detail message of
     * {@code cause}). This constructor is useful for runtime exceptions
     * that are little more than wrappers for other throwables.
     *
     * @param cause the cause (which is saved for later retrieval by the
     *        {@link #getCause()} method).  (A {@code null} value is
     *        permitted, and indicates that the cause is nonexistent or
     *        unknown.)
     */
    public ConfigParseException(final Throwable cause) {
        super(cause);
    }
}

