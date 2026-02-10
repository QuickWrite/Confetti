/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package net.quickwrite.confetti;

import com.typesafe.config.ConfigList;
import net.quickwrite.confetti.path.PathSegment;
import net.quickwrite.confetti.path.impl.IndexPathSegment;

import java.util.AbstractList;
import java.util.List;
import java.util.Objects;

/**
 * {@link ArrayNode} implementation that adapts a HOCON {@code ConfigList}.
 *
 * <p>
 * This class wraps a {@link com.typesafe.config.ConfigList} and exposes it
 * as an {@link ArrayNode} within the configuration node model used by this
 * library.
 *
 * <p>
 * Conversion from raw HOCON values to {@link ConfigNode} instances is
 * delegated to {@link ConfigNodeAdapter#toConfigNode} at access time so
 * that element nodes carry correct parent/path information. As a result,
 * accessors may perform conversion lazily on each invocation.
 */
public class HoconArrayNode extends AbstractConfigNode implements ArrayNode {
    private final ConfigList configList;

    /**
     * Creates a root-level {@code HoconArrayNode} wrapping {@code configList}.
     *
     * @param configList the non-null HOCON list to wrap
     * @throws NullPointerException if {@code configList} is {@code null}
     */
    public HoconArrayNode(final ConfigList configList) {
        super();

        Objects.requireNonNull(configList);
        this.configList = configList;
    }

    /**
     * Creates a child {@code HoconArrayNode} associated with a {@code parent}
     * and identified by {@code key} within the parent.
     *
     * @param configList the non-null HOCON list to wrap
     * @param parent the non-null parent {@link ConfigNode}
     * @param key the non-null {@link PathSegment} identifying this node in the parent
     * @throws NullPointerException if {@code configList} is {@code null}
     */
    public HoconArrayNode(final ConfigList configList, final ConfigNode parent, final PathSegment key) {
        super(parent, key);

        Objects.requireNonNull(configList);
        this.configList = configList;
    }

    /** {@inheritDoc} */
    @Override
    public ConfigNode get(final int index) {
        return ConfigNodeAdapter.toConfigNode(this.configList.get(index), this, new IndexPathSegment(index));
    }

    /** {@inheritDoc} */
    @Override
    public int size() {
        return this.configList.size();
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The returned list delegates its {@code get(int)} and {@code size()}
     * to this node's {@link #get(int)} and {@link #size()} methods and therefore
     * performs conversion on access.
     */
    @Override
    public List<ConfigNode> toList() {
        return new AbstractList<>() {
            @Override
            public ConfigNode get(final int index) {
                return HoconArrayNode.this.get(index);
            }

            @Override
            public int size() {
                return configList.size();
            }
        };
    }
}
