/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package net.quickwrite.confetti;

/**
 * A {@link ConfigNode} representing a {@code null} value.
 *
 * <p>
 * {@code NullNode} models the explicit absence of a value, similar to
 * {@code null} in JSON.
 *
 * <p>
 * This class is final and immutable.
 */
public final class NullNode implements ConfigNode {
    /**
     * Constructs a new {@link NullNode} instance without any parameters.
     */
    public NullNode() {}

    /**
     * Returns the {@link NodeType} of this node.
     *
     * @return {@link NodeType#NULL}
     */
    @Override
    public NodeType type() {
        return NodeType.NULL;
    }
}
