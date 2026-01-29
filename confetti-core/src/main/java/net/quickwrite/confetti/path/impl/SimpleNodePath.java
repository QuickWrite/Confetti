/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package net.quickwrite.confetti.path.impl;

import net.quickwrite.confetti.path.NodePath;
import net.quickwrite.confetti.path.PathSegment;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Default immutable implementation of {@link NodePath}.
 * <p>
 * This record stores an ordered list of {@link PathSegment} instances and
 * represents a fully resolved navigation path into a configuration tree.
 *
 * <h2>Design notes</h2>
 * <ul>
 *   <li>
 *       Instances are fully immutable and thread-safe.
 *   </li>
 *   <li>
 *       The {@code segments} list is defensively wrapped in an
 *       unmodifiable view to guarantee immutability.
 *   </li>
 * </ul>
 */
public record SimpleNodePath(List<PathSegment> segments) implements NodePath {

    /**
     * Shared empty-path instance. Used by {@link #empty()} and
     * {@link NodePath#empty()}.
     */
    private static final NodePath EMPTY = new SimpleNodePath(Collections.emptyList());

    /**
     * Creates a new {@code SimpleNodePath} with the given ordered segments.
     * <p>
     * The provided list is wrapped in an unmodifiable view. Callers are
     * encouraged to pass an already-immutable list, but this constructor
     * guarantees immutability regardless.
     *
     * @param segments ordered list of path segments, never {@code null}
     */
    public SimpleNodePath(final List<PathSegment> segments) {
        this.segments = Collections.unmodifiableList(segments);
    }

    /**
     * Returns a shared, canonical empty {@link NodePath} instance.
     *
     * @return an empty NodePath
     */
    public static NodePath empty() {
        return EMPTY;
    }

    /**
     * Returns an iterator over the path segments in the order they appear.
     *
     * @return iterator over the underlying segment list
     */
    @Override
    public Iterator<PathSegment> iterator() {
        return segments.iterator();
    }
}


