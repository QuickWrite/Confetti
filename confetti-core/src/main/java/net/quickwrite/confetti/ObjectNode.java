/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package net.quickwrite.confetti;

import java.util.*;

/**
 * A {@link ConfigNode} representing a key–value mapping of named nodes.
 *
 * <p>
 * An {@code ObjectNode} models an object-like structure similar to a JSON object,
 * where each key is associated with a {@link ConfigNode} value.
 *
 * <p>
 * Keys are unique strings. The ordering of keys and values is implementation-specific
 * unless otherwise documented.
 *
 * <p>
 * This interface is {@code non-sealed} to allow custom implementations.
 */
public non-sealed interface ObjectNode extends ConfigNode {
    /**
     * Returns the value associated with the given key, if present.
     *
     * @param key the key whose associated value is to be returned
     * @return an {@link Optional} containing the associated {@link ConfigNode},
     *         or {@link Optional#empty()} if the key is not present
     */
    Optional<ConfigNode> get(final String key);

    /**
     * Returns the set of keys contained in this object.
     *
     * <p>
     * The returned set may be backed by the underlying object and may reflect
     * subsequent changes, depending on the implementation.
     *
     * @return a {@link Set} of keys
     */
    Set<String> keys();

    /**
     * Returns a collection of the values contained in this object.
     *
     * <p>
     * The returned collection may be backed by the underlying object and may
     * reflect subsequent changes, depending on the implementation.
     *
     * @return a {@link Collection} of {@link ConfigNode} values
     */
    Collection<ConfigNode> values();

    /**
     * Converts this object node into a {@link Map}.
     *
     * <p>
     * The returned map contains the same key–value pairs as this object.
     *
     * <p>
     * The returned map is not guaranteed to be modifiable, and modifications
     * to it may or may not affect the underlying {@code ObjectNode}.
     *
     * @return a {@link Map} representation of this object node
     */
    Map<String, ConfigNode> toMap();

    /**
     * Returns the {@link NodeType} of this node.
     *
     * @return {@link NodeType#OBJECT}
     */
    @Override
    default NodeType type() {
        return NodeType.OBJECT;
    }

    /** {@inheritDoc} */
    @Override
    default ObjectNode toObject() {
        return this;
    }
}
