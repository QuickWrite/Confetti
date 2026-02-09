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
 * Base partial implementation of {@link ConfigNode} that provides common
 * parent/key and path semantics for tree-structured configuration nodes.
 *
 * <p>
 * This abstract class is intended to be extended by concrete node types
 * (such as object, array, value, or null nodes). It centralizes logic for
 * storing an optional parent reference and the {@link PathSegment} that
 * identifies this node within its parent, and it implements {@link #key()}
 * and {@link #path()} using those fields.
 *
 * <h2>Parent and key semantics</h2>
 * <ul>
 *   <li>
 *       If constructed without a parent, this node represents a root-level
 *       node and its {@link #path()} is empty.
 *   </li>
 *   <li>
 *       If constructed with a parent and key, the full path is computed by
 *       appending the key to the parent’s path (see {@link NodePath#appendPathSegment(PathSegment)}).
 *   </li>
 * </ul>
 *
 * <h3>Immutability and identity</h3>
 * <p>
 * Instances of {@code AbstractConfigNode} are effectively immutable with
 * respect to their parent and key: the parent and key are final and set on
 * construction. Subclasses may add mutable state, but should document any
 * mutability and thread-safety guarantees.
 *
 * <h3>Subclass responsibilities</h3>
 * <ul>
 *   <li>
 *       Concrete subclasses must implement the type-specific accessors and
 *       behavior required by {@link ConfigNode} (for example, {@code toObject()},
 *       {@code toArray()}, {@code toValue()}, and {@link ConfigNode#type()}).
 *   </li>
 *   <li>
 *       When a subclass represents a child node, it should call the
 *       {@link #AbstractConfigNode(ConfigNode, PathSegment)} constructor to
 *       correctly establish the parent/key relationship and preserve path semantics.
 *   </li>
 *   <li>
 *       Subclasses that override {@link #path()} or {@link #key()} should
 *       preserve the contract that {@code path()} returns an absolute {@link NodePath}
 *       and that {@code key()} returns an {@link Optional} containing the
 *       identifying {@link PathSegment} when present.
 *   </li>
 * </ul>
 *
 * <h3>Errors and validation</h3>
 * <p>
 * The two-argument constructor enforces non-nullity of {@code parent}
 * and {@code key} and throws {@link NullPointerException} if either is null.
 * Callers and subclasses should follow this contract when constructing
 * child nodes.
 */
public non-sealed abstract class AbstractConfigNode implements ConfigNode {
    /** Parent node in the configuration tree, or {@code null} if root-level. */
    private final ConfigNode parent;

    /** The segment that identifies this node within its parent, or {@code null}. */
    private final PathSegment key;

    /**
     * Creates a root-level {@code AbstractConfigNode} with no parent and no key.
     */
    public AbstractConfigNode() {
        this.parent = null;
        this.key = null;
    }

    /**
     * Creates a {@code AbstractConfigNode} that is logically a child of the given parent.
     *
     * @param parent non-null parent node
     * @param key    non-null path segment identifying this node within the parent
     * @throws NullPointerException if {@code parent} or {@code key} is {@code null}
     */
    public AbstractConfigNode(final ConfigNode parent, final PathSegment key) {
        Objects.requireNonNull(parent, "parent must not be null");
        Objects.requireNonNull(key, "key must not be null");

        this.parent = parent;
        this.key = key;
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
