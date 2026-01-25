/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package net.quickwrite.confetti.migration;

import net.quickwrite.confetti.ConfigNode;

/**
 * Performs a schema migration on a configuration node.
 *
 * <p>
 * A {@code ConfigMigrator} is responsible for taking a configuration tree
 * (represented as a {@link ConfigNode}) and producing an equivalent configuration
 * that conforms to a newer or different schema expected by the application.
 *
 * <p>
 * The exact target schema or version is implementation-specific and should be
 * documented by each {@code ConfigMigrator} implementation.
 *
 * <h3>Behavioral contract</h3>
 * <ul>
 *   <li>
 *       The implementation receives the current configuration via {@code config}
 *       and returns a {@link ConfigNode} that conforms to the target schema.
 *   </li>
 *   <li>
 *       The returned {@code ConfigNode} may be the same instance as the provided
 *       {@code config} or a new instance. Callers should use the returned node
 *       and not assume the original is unchanged.
 *   </li>
 *   <li>
 *       Both input and returned nodes must be usable and valid according to the
 *       respective schema; it is acceptable for the migration to drop, rename, or
 *       otherwise transform values as required by the target schema.
 *   </li>
 *   <li>
 *       Migrators should aim to be idempotent when possible: applying the same
 *       migrator multiple times to an already-migrated configuration should not
 *       further change the configuration.
 *   </li>
 *   <li>
 *       On unrecoverable failure (invalid input, incompatible schema, or other
 *       unrecoverable conditions), implementations should throw an unchecked
 *       exception describing the problem.
 *   </li>
 * </ul>
 *
 * <h3>Thread-safety and side effects</h3>
 * <p>
 * Unless explicitly documented by an implementation, callers should assume a
 * {@code ConfigMigrator} is not thread-safe. Migration should generally be a
 * pure transformation of the configuration object and avoid performing external
 * side effects (I/O, network calls). If an implementation does perform side
 * effects, it should document those behaviors clearly.
 *
 * <h3>Implementation notes</h3>
 * <p>
 * Implementors should document the target schema/version and whether the
 * migrator performs in-place changes or returns a copy. Consider providing
 * tests that demonstrate idempotence and correctness for common inputs.
 */
public interface ConfigMigrator {
    /**
     * Migrates the provided configuration object to the specified version.
     *
     * <p>
     * The returned node is the configuration that should be used after the
     * migration; callers must use the returned value (it may be the same object
     * as the input or a different instance).
     *
     * <p>
     * Both objects should be valid and in a usable state, but it is not required
     * that the old configuration will retain its values.
     *
     * @param config The config to be migrated
     * @return A config that now conforms to the new schema
     */
    ConfigNode migrate(final ConfigNode config);
}
