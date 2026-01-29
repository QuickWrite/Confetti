/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package net.quickwrite.confetti.path.impl;

import net.quickwrite.confetti.path.PathSegment;

import java.util.Objects;

/**
 * A {@link PathSegment} that represents a key lookup.
 *
 * <p>
 * Instances are immutable and represent a single map or object access.
 *
 * <h2>Example</h2>
 * {@snippet :
 * PathSegment segment = PathSegment.key("server");
 *
 * if (segment.isKey()) {
 *     String name = segment.key();
 * }
 * }
 *
 * @param key The key that is being provided
 */
public record KeyPathSegment(String key) implements PathSegment {
    /**
     * Constructs a new KeyPathSegment instance and checks if the value is null.
     *
     * @param key The key that is being provided
     * @throws NullPointerException if the provided value is a null-value.
     */
    public KeyPathSegment {
        Objects.requireNonNull(key, "key cannot be null");
    }

    /**
     * {@inheritDoc}
     *
     * @return always {@code true}.
     */
    @Override
    public boolean isKey() {
        return true;
    }

    /**
     * {@inheritDoc}
     *
     * @return always {@code false}.
     */
    @Override
    public boolean isIndex() {
        return false;
    }

    /**
     * {@inheritDoc}
     *
     * @return Nothing as it always throws
     * @throws IllegalStateException always, because this segment is a key
     *                               and does not have an index
     */
    @Override
    public int index() {
        throw new IllegalStateException("The PathSegment is a key.");
    }
}
