/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package net.quickwrite.confetti;

import net.quickwrite.confetti.path.NodePath;
import net.quickwrite.confetti.path.PathSegment;

import java.util.Objects;
import java.util.Optional;

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
 *
 * <h2>Parent and key semantics</h2>
 * <ul>
 *   <li>
 *       If constructed without a parent, this node represents a root-level
 *       null value and its {@link #path()} is empty.
 *   </li>
 *   <li>
 *       If constructed with a parent and key, the full path is computed by
 *       appending the key to the parent’s path.
 *   </li>
 * </ul>
 */
public final class NullNode implements ConfigNode {

    /** Parent node in the configuration tree, or {@code null} if root-level. */
    private final ConfigNode parent;

    /** The segment that identifies this node within its parent, or {@code null}. */
    private final PathSegment key;

    /**
     * Creates a root-level {@code NullNode} with no parent and no key.
     */
    public NullNode() {
        this.parent = null;
        this.key = null;
    }

    /**
     * Creates a {@code NullNode} that is logically a child of the given parent.
     *
     * @param parent non-null parent node
     * @param key    non-null path segment identifying this node within the parent
     * @throws NullPointerException if {@code parent} or {@code key} is {@code null}
     */
    public NullNode(final ConfigNode parent, final PathSegment key) {
        Objects.requireNonNull(parent, "parent must not be null");
        Objects.requireNonNull(key, "key must not be null");

        this.parent = parent;
        this.key = key;
    }

    /**
     * Returns {@link NodeType#NULL}, identifying this node as a null placeholder.
     */
    @Override
    public NodeType type() {
        return NodeType.NULL;
    }

    /**
     * Returns the {@link PathSegment} that identifies this node within its parent,
     * if present.
     *
     * @return optional key segment
     */
    @Override
    public Optional<PathSegment> key() {
        return Optional.ofNullable(this.key);
    }

    /**
     * Computes the full {@link NodePath} to this node.
     * <p>
     * If this node has no parent, the returned path is empty. Otherwise, the
     * path is derived by appending this node's key to the parent's path.
     * </p>
     *
     * @return the full path to this node
     */
    @Override
    public NodePath path() {
        if (parent == null) {
            return NodePath.empty();
        }

        return parent.path().appendPathSegment(key);
    }
}
