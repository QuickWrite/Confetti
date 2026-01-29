/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package net.quickwrite.confetti.path;

import net.quickwrite.confetti.path.impl.SimpleNodePath;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Immutable, iterable representation of a path into a ConfigurationNode tree.
 */
public interface NodePath extends Iterable<PathSegment> {
    /**
     * Return the ordered, immutable list of segments.
     *
     * @return immutable list of PathSegment
     */
    List<PathSegment> segments();

    /**
     * True when the path contains no segments.
     *
     * @return {@code true} when the path is empty
     */
    default boolean isEmpty() {
        return segments().isEmpty();
    }

    /**
     * Returns a canonical empty {@link NodePath} instance.
     *
     * @return empty NodePath
     */
    static NodePath empty() {
        return SimpleNodePath.empty();
    }

    /**
     * Returns a new {@link NodePath} with the given segment appended to the end.
     * <p>
     * The original path remains unchanged; this method always returns a new,
     * immutable instance.
     *
     * @param pathSegment non-null segment to append
     * @return a new NodePath containing all existing segments plus the appended one
     * @throws NullPointerException if {@code pathSegment} is {@code null}
     */
    default NodePath appendPathSegment(final PathSegment pathSegment) {
        Objects.requireNonNull(pathSegment, "pathSegment must not be null");

        final List<PathSegment> segments = new ArrayList<>(this.segments());
        segments.add(pathSegment);

        return new SimpleNodePath(Collections.unmodifiableList(segments));
    }
}
