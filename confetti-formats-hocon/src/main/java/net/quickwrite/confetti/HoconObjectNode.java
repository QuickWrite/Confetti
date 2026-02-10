/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package net.quickwrite.confetti;

import com.typesafe.config.ConfigObject;
import com.typesafe.config.ConfigValue;
import net.quickwrite.confetti.path.PathSegment;
import net.quickwrite.confetti.path.impl.KeyPathSegment;

import java.util.*;

/**
 * {@link ObjectNode} implementation that wraps a HOCON {@code ConfigObject}.
 *
 * <p>
 * This class adapts a {@link com.typesafe.config.ConfigObject} to the
 * {@link ObjectNode} interface used by the configuration model.
 *
 * <p><b>Mutability and view semantics.</b> The underlying {@code ConfigObject} is
 * the canonical source of data. The collections and map returned by this class
 * reflect or re-compute values from that object; the {@link Map} returned by
 * {@link #toMap()} is a read-only view (attempts to modify it will throw
 * {@link UnsupportedOperationException}) but it will reflect the current state of
 * the {@code ConfigObject} at the time methods are invoked because conversion
 * is performed on access.
 */
public final class HoconObjectNode extends AbstractConfigNode implements ObjectNode {
    private final ConfigObject configObject;

    /**
     * Creates a root-level {@code HoconObjectNode} that wraps the given
     * {@code ConfigObject}.
     *
     * @param configObject the non-null HOCON {@code ConfigObject} to wrap
     * @throws NullPointerException if {@code configObject} is {@code null}
     */
    public HoconObjectNode(final ConfigObject configObject) {
        super();

        Objects.requireNonNull(configObject);
        this.configObject = configObject;
    }

    /**
     * Creates a child {@code HoconObjectNode} associated with the specified
     * {@code parent} and identified within the parent by {@code key}.
     *
     * <p>
     * The constructed node will use the provided {@code parent} for path
     * resolution and will attach the supplied {@code key} as the identifying
     * {@link PathSegment}.
     *
     * @param configObject the non-null HOCON {@code ConfigObject} to wrap
     * @param parent       the non-null parent {@link ConfigNode}
     * @param key          the non-null {@link PathSegment} that identifies this node in the parent
     * @throws NullPointerException if any argument is {@code null}
     */
    public HoconObjectNode(final ConfigObject configObject, final ConfigNode parent, final PathSegment key) {
        super(parent, key);

        Objects.requireNonNull(configObject);
        this.configObject = configObject;
    }

    /** {@inheritDoc} */
    @Override
    public Optional<ConfigNode> get(final String key) {
        return Optional.ofNullable(getNode(key));
    }

    /** {@inheritDoc} */
    @Override
    public Set<String> keys() {
        return configObject.keySet();
    }

    /** {@inheritDoc} */
    @Override
    public Collection<ConfigNode> values() {
        final List<ConfigNode> values = new ArrayList<>();

        for (final String key : this.configObject.keySet()) {
            values.add(getNode(key));
        }

        return values;
    }

    /**
     * {@inheritDoc}
     * <hr>
     * <p>
     * The returned map is an unmodifiable, lazy view that adapts the
     * underlying {@code ConfigObject} to {@link ConfigNode} values. Lookups and
     * the {@link Map#entrySet()} iteration perform conversion on-demand using
     * {@link #getNode(String)}.
     *
     * <p>Important characteristics:
     * <ul>
     *   <li>The map's {@link Map#get(Object)} accepts only {@link String} keys; other key types return {@code null}.</li>
     *   <li>{@link Map#containsKey(Object)} delegates to the underlying {@code ConfigObject}.</li>
     *   <li>{@link Map#keySet()} delegates directly to the underlying {@code ConfigObject}'s key set.</li>
     *   <li>{@link Map#entrySet()} constructs a new {@link Set} of immutable entries on each invocation; these entries contain
     *       {@link ConfigNode} values converted via {@link #getNode(String)}.</li>
     *   <li>The map does not support modification operations (the default {@link Map} mutation methods will throw
     *       {@link UnsupportedOperationException}).</li>
     * </ul>
     *
     * @return a read-only {@link Map} view of this object node
    */
    @Override
    public Map<String, ConfigNode> toMap() {
        return new AbstractMap<>() {
            @Override
            public int size() {
                return configObject.size();
            }

            @Override
            public ConfigNode get(final Object key) {
                if (!(key instanceof String)) {
                    return null;
                }
                return getNode((String)key);
            }

            @Override
            public boolean containsKey(final Object key) {
                return configObject.containsKey(key);
            }

            @Override
            public Set<String> keySet() {
                return configObject.keySet();
            }

            @Override
            public Set<Entry<String, ConfigNode>> entrySet() {
                final Set<Entry<String, ConfigNode>> entrySet = new HashSet<>();

                for (final Entry<String, ConfigValue> entry : configObject.entrySet()) {
                    entrySet.add(new SimpleImmutableEntry<>(entry.getKey(), getNode(entry.getKey())));
                }

                return entrySet;
            }
        };
    }

    /**
     * Converts the underlying HOCON value for {@code key} into a {@link ConfigNode}.
     *
     * <p>
     * This helper delegates conversion to {@link ConfigNodeAdapter#toConfigNode}.
     * The converter is invoked with this {@link HoconObjectNode} as the parent and a
     * {@link KeyPathSegment} constructed from {@code key} so that the resulting node's
     * {@link AbstractConfigNode#path()} reflects its position in the configuration tree.
     *
     * @param key the key whose associated HOCON value should be converted (can be {@code null})
     * @return the converted {@link ConfigNode} or {@code null} if the underlying HOCON value is absent
     * @implNote conversion is performed each time this method is called; callers who want to avoid
     *           repeated conversions should cache results externally or the converter may provide its own cache.
     */
    private ConfigNode getNode(final String key) {
        return ConfigNodeAdapter.toConfigNode(this.configObject.get(key), this, new KeyPathSegment(key));
    }
}
