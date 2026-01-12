/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package net.quickwrite.confetti;

/**
 * The type discriminator for {@link ConfigNode} implementations.
 *
 * <p>
 * Each {@code ConfigNode} should return the appropriate {@link NodeType}
 * from its {@link ConfigNode#type()} method. Consumers can use this enum
 * to switch on node kinds or to make type-based decisions.
 */
public enum NodeType {
    /**
     * A key–value mapping (object-like) node, e.g. a JSON object.
     */
    OBJECT,

    /**
     * An ordered, index-based collection (array-like) node, e.g. a JSON array.
     */
    ARRAY,

    /**
     * A scalar value node (string, numeric, boolean, etc.).
     */
    VALUE,

    /**
     * An explicit null node representing the absence of a value.
     */
    NULL;

    /**
     * Returns {@code true} when this type represents a container (object or array).
     *
     * @return {@code true} for OBJECT and ARRAY; {@code false} otherwise
     */
    public boolean isContainer() {
        return this == OBJECT || this == ARRAY;
    }
}
