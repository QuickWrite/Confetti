/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package net.quickwrite.confetti;

import com.typesafe.config.ConfigList;
import com.typesafe.config.ConfigObject;
import com.typesafe.config.ConfigValue;
import com.typesafe.config.ConfigValueType;
import net.quickwrite.confetti.path.PathSegment;

/**
 * Utility class responsible for converting HOCON {@link ConfigValue} instances
 * into the corresponding {@link ConfigNode} representations.
 *
 * <p>
 * This class centralizes the mapping between HOCON value types and
 * concrete {@link ConfigNode} implementations.
 * It is used by node implementations such as {@code HoconObjectNode} and
 * {@code HoconArrayNode} to lazily adapt HOCON data into the configuration
 * node tree used by this library.
 *
 * <hr>
 *
 * This class is intentionally non-instantiable and exposes only static
 * utility methods.
 */
final class ConfigNodeAdapter {
    /**
     * Private constructor to prevent instantiation.
     */
    private ConfigNodeAdapter() {}

    /**
     * Converts a HOCON {@link ConfigValue} into an appropriate {@link ConfigNode}
     * implementation.
     *
     * <p>The mapping is based on the value's {@link ConfigValueType}:
     * <ul>
     *   <li>{@link ConfigValueType#OBJECT OBJECT} -> {@link HoconObjectNode}</li>
     *   <li>{@link ConfigValueType#LIST LIST} -> {@link HoconArrayNode}</li>
     *   <li>{@link ConfigValueType#BOOLEAN BOOLEAN},
     *       {@link ConfigValueType#NUMBER NUMBER},
     *       {@link ConfigValueType#STRING STRING} -> {@link HoconValueNode}</li>
     *   <li>{@link ConfigValueType#NULL NULL} -> {@link NullNode}</li>
     * </ul>
     *
     * <p>
     * If {@code configValue} is {@code null}, this method returns {@code null}
     * rather than a {@link NullNode}. This allows callers to distinguish between
     * an explicitly present {@code null} value and the absence of a value.
     *
     * <p>The returned node will:
     * <ul>
     *   <li>Use {@code parent} as its logical parent</li>
     *   <li>Use {@code segment} as its identifying {@link PathSegment}</li>
     *   <li>Correctly report its {@link NodeType} via {@link ConfigNode#type()}</li>
     * </ul>
     *
     * @param configValue the HOCON value to convert; may be {@code null}
     * @param parent      the non-null parent {@link ConfigNode} of the resulting node
     * @param segment     the non-null {@link PathSegment} identifying the node within its parent
     * @return a {@link ConfigNode} representing the provided HOCON value,
     *         or {@code null} if {@code configValue} is {@code null}
     *
     * @implNote Conversion is performed eagerly and a new {@link ConfigNode}
     *           instance is created for each invocation. No caching is performed
     *           by this method.
     */
    public static ConfigNode toConfigNode(final ConfigValue configValue, final ConfigNode parent, final PathSegment segment) {
        if (configValue == null) {
            return null;
        }

        return switch (configValue.valueType()) {
            case OBJECT -> new HoconObjectNode((ConfigObject) configValue, parent, segment);
            case LIST -> new HoconArrayNode((ConfigList) configValue, parent, segment);
            case BOOLEAN, NUMBER, STRING -> new HoconValueNode(configValue, parent, segment);
            case NULL -> new NullNode(parent, segment);
        };
    }
}
