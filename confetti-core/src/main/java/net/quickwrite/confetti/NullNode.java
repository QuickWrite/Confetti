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
 */
public non-sealed interface NullNode extends ConfigNode {
    /**
     * Returns the {@link NodeType} of this node.
     *
     * @return {@link NodeType#NULL}
     */
    @Override
    default NodeType type() {
        return NodeType.NULL;
    }
}
