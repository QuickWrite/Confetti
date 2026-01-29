/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package net.quickwrite.confetti.path.impl;

import net.quickwrite.confetti.path.PathSegment;

/**
 * A {@link PathSegment} that represents an index.
 *
 * <p>
 * Instances are immutable and represent a single array access.
 *
 * <h2>Example</h2>
 * {@snippet :
 * PathSegment segment = PathSegment.index("server");
 *
 * if (segment.isIndex()) {
 *     int index = segment.index();
 * }
 * }
 *
 * @param index The index that is being provided
 */
public record IndexPathSegment(int index) implements PathSegment {

    // No null check needed as this is a primitive type

    /**
     * {@inheritDoc}
     *
     * @return always {@code false}.
     */
    @Override
    public boolean isKey() {
        return false;
    }

    /**
     * {@inheritDoc}
     *
     * @return always {@code true}.
     */
    @Override
    public boolean isIndex() {
        return true;
    }

    /**
     * {@inheritDoc}
     *
     * @return Nothing as it always throws
     * @throws IllegalStateException always, because this segment is a key
     *                               and does not have an index
     */
    @Override
    public String key() {
        throw new IllegalStateException("The PathSegment is an index");
    }
}
