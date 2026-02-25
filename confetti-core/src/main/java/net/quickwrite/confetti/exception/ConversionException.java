/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package net.quickwrite.confetti.exception;

import net.quickwrite.confetti.ConfigNode;
import net.quickwrite.confetti.resolver.TypeResolver;

/**
 * Thrown when a conversion between a configuration node and a Java value fails.
 *
 * <p>
 * Use this exception to signal that a {@link TypeResolver} could not convert a
 * {@link ConfigNode} into the requested Java type.
 *
 * <h2>When to throw</h2>
 * <ul>
 *   <li>
 *       When the input node has an unexpected shape or type for the requested
 *       conversion.
 *   </li>
 *   <li>
 *       When a required field is missing or a value cannot be parsed, for
 *       example a numeric parse failure.
 *   </li>
 *   <li>
 *       When a nested conversion fails and the resolver wants to propagate a
 *       clear, typed error to callers.
 *   </li>
 * </ul>
 *
 * <h2>Example</h2>
 * {@snippet :
 *  if (!node.isValue()) {
 *      throw new ConversionException("Expected value node for integer conversion");
 *  }
 *
 *  try {
 *      return Integer.parseInt(node.toValue().asString());
 *  } catch (final NumberFormatException exception) {
 *      throw new ConversionException("Invalid integer for field timeout", exception);
 *  }
 * }
 */
public class ConversionException extends Exception {

    /**
     * Create a ConversionException with the given message.
     *
     * @param message non-null description of the failure
     */
    public ConversionException(final String message) {
        super(message);
    }

    /**
     * Create a ConversionException with the given message and cause.
     *
     * @param message non-null description of the failure
     * @param cause the underlying cause of the failure
     */
    public ConversionException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
