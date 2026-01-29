/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package net.quickwrite.confetti.path;

import net.quickwrite.confetti.path.impl.IndexPathSegment;
import net.quickwrite.confetti.path.impl.KeyPathSegment;

/**
 * A single segment of a NodePath. A segment is either a key (map/object access)
 * or an index (array access).
 *
 * <p>
 * Implementations are immutable.
 */
public interface PathSegment {
    /**
     * True when this segment represents a key (object/map access).
     *
     * @return true if key segment
     */
    boolean isKey();

    /**
     * True when this segment represents an index (array access).
     *
     * @return true if index segment
     */
    boolean isIndex();

    /**
     * Return the key for key segments.
     *
     * @return the key string; may be null for index segments
     * @throws IllegalStateException if called on an index segment
     */
    String key();

    /**
     * Return the index for index segments.
     *
     * @return the index value
     * @throws IllegalStateException if called on a key segment
     */
    int index();

    static PathSegment key(final String key) {
        return new KeyPathSegment(key);
    }

    static PathSegment index(final int index) {
        return new IndexPathSegment(index);
    }
}
