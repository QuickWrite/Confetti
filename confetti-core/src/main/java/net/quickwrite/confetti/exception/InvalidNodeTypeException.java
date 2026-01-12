/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package net.quickwrite.confetti.exception;

/**
 * Thrown to indicate that an unexpected or unsupported {@link net.quickwrite.confetti.NodeType}
 * was encountered.
 *
 * <p>
 * This exception is typically used when a {@link net.quickwrite.confetti.ConfigNode} is processed
 * in a context that requires a specific node type, but the actual type does
 * not match the expected one.
 *
 * <p>
 * This is an unchecked exception because it usually indicates a programming
 * error or an invalid configuration state rather than a recoverable condition.
 */
public class InvalidNodeTypeException extends RuntimeException {
    /**
     * Constructs a new {@code InvalidNodeTypeException} with the specified detail message.
     *
     * @param message a human-readable message describing the invalid node type
     */
    public InvalidNodeTypeException(final String message) {
        super(message);
    }
}
