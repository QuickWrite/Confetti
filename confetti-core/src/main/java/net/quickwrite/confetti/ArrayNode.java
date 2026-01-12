/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package net.quickwrite.confetti;

import java.util.Iterator;
import java.util.List;

/**
 * A {@link ConfigNode} representing an ordered, index-based collection of nodes.
 *
 * <p>
 * An {@code ArrayNode} models an array-like structure similar to a JSON array.
 * Elements are ordered, zero-indexed, and may be accessed by position.
 *
 * <p>
 * Implementations are required to provide random access via {@link #get(int)}
 * and a size via {@link #size()}. All default behavior (such as iteration)
 * is defined in terms of these two methods.
 *
 * <p>This interface is {@code non-sealed} to allow custom implementations.
 */
public non-sealed interface ArrayNode extends ConfigNode, Iterable<ConfigNode> {
    /**
     * Returns the element at the specified position in this list.
     *
     * @param index index of the element to return
     * @return The element at the index
     * @throws IndexOutOfBoundsException if the index is negative or
     *                                   greater than or equal to {@link #size()}
     */
    ConfigNode get(final int index);

    /**
     * Returns the number of elements in this list.
     *
     * @return the number of elements in this list
     */
    int size();

    /**
     * Returns an iterator over the elements in this array in proper sequence.
     *
     * <p>The default implementation iterates from index {@code 0} up to
     * {@code size() - 1} and retrieves elements using {@link #get(int)}.
     *
     * <p>The returned iterator does not support element removal.
     *
     * @return an {@link Iterator} over the elements of this array
     */
    @Override
    default Iterator<ConfigNode> iterator() {
        return new Iterator<>() {
            int position = 0;

            @Override
            public boolean hasNext() {
                return this.position < size();
            }

            @Override
            public ConfigNode next() {
                return get(this.position++);
            }
        };
    }

    /**
     * Converts the contents of this array node into a {@link List}.
     *
     * <p>The returned list contains the same elements in the same order as
     * this array.
     *
     * <p>The returned list is not guaranteed to be modifiable, and modifications
     * to it may or may not affect the underlying {@code ArrayNode}.
     *
     * @return a {@link List} containing this array's elements
     */
    List<ConfigNode> toList();

    /**
     * Returns the {@link NodeType} of this node.
     *
     * @return {@link NodeType#ARRAY}
     */
    @Override
    default NodeType type() {
        return NodeType.ARRAY;
    }

    /** {@inheritDoc} */
    @Override
    default ArrayNode toArray() {
        return this;
    }
}
