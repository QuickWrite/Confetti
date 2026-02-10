/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package net.quickwrite.confetti;

import com.typesafe.config.ConfigValue;
import net.quickwrite.confetti.path.PathSegment;

import java.util.Objects;

/**
 * {@link ValueNode} implementation that wraps a HOCON {@link ConfigValue}.
 *
 * <p>
 * This class adapts a {@link com.typesafe.config.ConfigValue} to the
 * {@link ValueNode} abstraction used by the configuration model.
 * Conversions from the wrapped HOCON value to Java primitives are performed
 * by calling {@link ConfigValue#unwrapped()} and casting to the expected
 * Java types. Callers should therefore expect {@link ClassCastException}
 * if the underlying HOCON value is not compatible with the requested
 * representation (for example, calling {@link #asLong()} on a string value).
 *
 * <p>
 * Instances of {@code HoconValueNode} are lightweight adapters and do not
 * perform caching; {@link #value()} returns the raw unwrapped Java value on
 * each invocation.
 */
public final class HoconValueNode extends AbstractConfigNode implements ValueNode {
    private final ConfigValue configValue;

    /**
     * Creates a root-level {@code HoconValueNode} that wraps the given
     * {@code ConfigValue}.
     *
     * @param configValue the non-null HOCON value to wrap
     * @throws NullPointerException if {@code configValue} is {@code null}
     */
    public HoconValueNode(final ConfigValue configValue) {
        super();

        Objects.requireNonNull(configValue);
        this.configValue = configValue;
    }

    /**
     * Creates a child {@code HoconValueNode} associated with the specified
     * {@code parent} and identified within the parent by {@code key}.
     *
     * @param configValue the non-null HOCON value to wrap
     * @param parent the non-null parent {@link ConfigNode}
     * @param key the non-null {@link PathSegment} that identifies this node in the parent
     * @throws NullPointerException if {@code configValue} is {@code null}
     */
    public HoconValueNode(final ConfigValue configValue, final ConfigNode parent, final PathSegment key) {
        super(parent, key);

        Objects.requireNonNull(configValue);
        this.configValue = configValue;
    }

    /** {@inheritDoc} */
    @Override
    public String asString() {
        return (String)this.value();
    }

    /** {@inheritDoc} */
    @Override
    public long asLong() {
        return ((Number)this.value()).longValue();
    }

    /** {@inheritDoc} */
    @Override
    public double asDouble() {
        return ((Number)this.value()).doubleValue();
    }

    /** {@inheritDoc} */
    @Override
    public boolean asBoolean() {
        return (Boolean)this.value();
    }

    /** {@inheritDoc} */
    @Override
    public Object value() {
        return this.configValue.unwrapped();
    }
}
