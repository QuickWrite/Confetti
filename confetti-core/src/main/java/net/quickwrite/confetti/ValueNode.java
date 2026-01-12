/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package net.quickwrite.confetti;

/**
 * A {@link ConfigNode} representing a single scalar value.
 *
 * <p>
 * A {@code ValueNode} models a primitive or scalar value such as a string,
 * number, or boolean, similar to a JSON value.
 *
 * <p>
 * Implementations are responsible for defining how conversions between
 * different representations are performed. Conversion methods may throw
 * runtime exceptions if the underlying value cannot be meaningfully
 * converted to the requested type.
 *
 * <p>This interface is {@code non-sealed} to allow custom implementations.
 */
public non-sealed interface ValueNode extends ConfigNode {
    /**
     * Interprets the value as a string.
     *
     * @return String version of the value
     */
    String asString();

    /**
     * Interprets the value as a long.
     *
     * @return Long version of the value
     */
    long asLong();

    /**
     * Interprets the value as a double.
     *
     * @return Double version of the value
     */
    double asDouble();

    /**
     * Interprets the value as a boolean
     *
     * @return Boolean version of the value
     */
    boolean asBoolean();

    /**
     * Returns the underlying value stored in this node.
     *
     * <p>
     * The returned object represents the raw value as stored by the
     * implementation and may be of any type.
     *
     * @return the underlying value of this node
     */
    Object value();

    /**
     * Returns the {@link NodeType} of this node.
     *
     * @return {@link NodeType#VALUE}
     */
    @Override
    default NodeType type() {
        return NodeType.VALUE;
    }

    /** {@inheritDoc} */
    @Override
    default ValueNode toValue() {
        return this;
    }
}
