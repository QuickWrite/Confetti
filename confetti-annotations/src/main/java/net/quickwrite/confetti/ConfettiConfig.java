/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package net.quickwrite.confetti;

import java.lang.annotation.*;

/**
 * Declares the specified interface as a configuration definition.
 *
 * <p>
 * The interface that is being annotated will now be a contract for the code generator
 * to build the necessary source files to parse the inputted configuration file.
 * {@snippet :
 * @ConfettiConfig
 * interface MyAwesomeConfig {
 *     String name();
 * }
 * }
 *
 * <hr />
 *
 * <p>
 * If needed, the current version of the configuration object can also manually be specified.
 * If not provided it would assume the highest value of the migrators.
 * {@snippet :
 * @ConfettiConfig(version = 3)
 * interface MyAwesomeConfig {
 *     String name();
 * }
 * }
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface ConfettiConfig {
    /**
     * Provides the current version of this config file. If the value is
     * <ul>
     *     <li>{@code >= 1} the current version of the config is this value.</li>
     *     <li>
     *         {@code < 1} the current version of the config is the highest possible
     *          value based on the available migrators.
     *     </li>
     * </ul>
     *
     * @return An int of the version or a negative number.
     */
    int version() default -1;
}
