/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package net.quickwrite.confetti;

import net.quickwrite.confetti.path.PathSegment;

/**
 * A {@link ConfigNode} implementation representing the absence of a value.
 *
 * <h2>Key characteristics</h2>
 * <ul>
 *   <li>
 *       Represents a logical {@code null} value in the configuration tree.
 *   </li>
 *   <li>
 *       Immutable and thread-safe.
 *   </li>
 *   <li>
 *       May optionally track its parent node and the {@link PathSegment}
 *       that led to it, enabling accurate path reconstruction.
 *   </li>
 *   <li>
 *       Returned by APIs such as {@code getOrEmpty()} or {@code atOrEmpty()}
 *       to avoid {@code null} checks.
 *   </li>
 * </ul>
 */
public final class NullNode extends AbstractConfigNode {
    /**
     * Creates a root-level {@code NullNode} with no parent and no key.
     */
    public NullNode() {
        super();
    }

    /**
     * Creates a {@code NullNode} that is logically a child of the given parent.
     *
     * @param parent non-null parent node
     * @param key    non-null path segment identifying this node within the parent
     * @throws NullPointerException if {@code parent} or {@code key} is {@code null}
     */
    public NullNode(final ConfigNode parent, final PathSegment key) {
        super(parent, key);
    }

    /**
     * Returns {@link NodeType#NULL}, identifying this node as a null placeholder.
     */
    @Override
    public NodeType type() {
        return NodeType.NULL;
    }
}
