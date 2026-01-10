/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package net.quickwrite.confetti.migration;

import java.lang.annotation.*;

/**
 * Denotes a migration class with its respective metadata.
 *
 * <p>
 * The annotation encodes the information
 * <ul>
 *     <li>that this class is a migrator</li>
 *     <li>which interface is being migrated</li>
 *     <li>which versions are affected by this migration</li>
 * </ul>
 * {@snippet :
 * @ConfigMigration(
 *     target = MyAwesomeConfig.class,
 *     from = 1,
 *     to = 2
 * )
 * class V1toV2Migration implements ConfigMigrator {
 *     @Override
 *     public Config migrate(final Config config) {
 *         // code
 *     }
 * }
 * }
 *
 * <p>
 * It is possible to set a greater version difference than {@code 1} in {@link #from()} and {@link #to()}.
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface ConfigMigration {
    /**
     * Provides the target interface that is being annotated with {@link net.quickwrite.confetti.ConfettiConfig}.
     *
     * @return The reference to the interface
     */
    Class<?> target();

    /**
     * Provides the version that is being migrated from.
     *
     * <p>
     * It is required that {@code from < to}.
     */
    int from();

    /**
     * Provides the version that is being migrated to.
     *
     * <p>
     * It is required that {@code from < to}.
     */
    int to();
}
