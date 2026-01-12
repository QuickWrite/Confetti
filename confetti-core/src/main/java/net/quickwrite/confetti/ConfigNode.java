/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package net.quickwrite.confetti;

import net.quickwrite.confetti.exception.InvalidNodeTypeException;

/**
 * Base interface for all configuration tree nodes.
 *
 * <p>
 * A {@code ConfigNode} represents a single element in a configuration
 * structure, similar to a node in a JSON document. Every node has a
 * {@link NodeType} that describes its concrete kind.
 *
 * <p>
 * The configuration model is composed of four primary node types:
 * <ul>
 *   <li>{@link ObjectNode} – key–value mappings</li>
 *   <li>{@link ArrayNode} – ordered collections</li>
 *   <li>{@link ValueNode} – scalar values</li>
 *   <li>{@link NullNode} – explicit {@code null} values</li>
 * </ul>
 *
 * <p>
 * Code is expected to inspect the node's {@link #type()} before
 * calling type-specific accessors such as {@link #toObject()} or
 * {@link #toArray()}. Calling an incompatible accessor may result in
 * a runtime exception.
 */
public sealed interface ConfigNode permits ArrayNode, ObjectNode, ValueNode, NullNode {
    /**
     * Returns the {@link NodeType} of this node.
     *
     * @return The node type
     */
    NodeType type();

    /**
     * Checks if the node is an {@link ObjectNode}.
     *
     * @return {@code true} if the node is an {@link ObjectNode}, {@code false} otherwise.
     */
    default boolean isObject() {
        return this.type() == NodeType.OBJECT;
    }

    /**
     * Returns the object node variant without casting.
     *
     * @throws java.security.InvalidParameterException if the node is not an {@link ObjectNode}.
     * @return The {@link ObjectNode} representation of this node.
     */
    default ObjectNode toObject() {
        throw new InvalidNodeTypeException("The node cannot be used as a value node.");
    }

    /**
     * Checks if the node is an {@link ArrayNode}.
     *
     * @return {@code true} if the node is an {@link ArrayNode}, {@code false} otherwise.
     */
    default boolean isArray() {
        return this.type() == NodeType.ARRAY;
    }

    /**
     * Returns the array node variant without casting.
     *
     * @return The {@link ArrayNode} representation of this node.
     * @throws java.security.InvalidParameterException if the node is not an {@link ArrayNode}.
     */
    default ArrayNode toArray() {
        throw new InvalidNodeTypeException("The node cannot be used as a value node.");
    }

    /**
     * Checks if the node is a {@link ValueNode}.
     *
     * @return {@code true} if the node is a {@link ValueNode}, {@code false} otherwise.
     */
    default boolean isValue() {
        return this.type() == NodeType.VALUE;
    }

    /**
     * Returns the value node variant without casting.
     *
     * @throws java.security.InvalidParameterException if the node is not a {@link ValueNode}.
     * @return The {@link ValueNode} representation of this node.
     */
    default ValueNode toValue() {
        throw new InvalidNodeTypeException("The node cannot be used as a value node.");
    }

    /**
     * Checks if the node is a NullNode.
     *
     * @return {@code true} if the node is a NullNode, {@code false} otherwise.
     */
    default boolean isNull() {
        return this.type() == NodeType.NULL;
    }
}
