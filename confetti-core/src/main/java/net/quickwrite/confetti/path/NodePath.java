/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package net.quickwrite.confetti.path;

import java.util.List;

/**
 * Immutable, iterable representation of a path into a ConfigurationNode tree.
 */
public interface NodePath extends Iterable<PathSegment> {
    /**
     * Return the ordered, immutable list of segments.
     *
     * @return list of PathSegment
     */
    List<PathSegment> segments();

    /**
     * True when the path contains no segments.
     *
     * @return true if empty
     */
    default boolean isEmpty() {
        return segments().isEmpty();
    }
}
