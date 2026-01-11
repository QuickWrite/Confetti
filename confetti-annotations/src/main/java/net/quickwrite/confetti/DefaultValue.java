/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package net.quickwrite.confetti;

import java.lang.annotation.*;

/**
 * Sets a default value for the annotated entry.
 * The contents of this annotation will override any other provided default value.
 *
 * <p>
 * If the {@code name} configuration entry should have the default value of
 * {@code "Max"}, then it can be annotated like this:
 * {@snippet :
 * @ConfettiConfig
 * interface MyAwesomeConfig {
 *     @DefaultValue("Max")
 *     String name();
 * }
 * }
 *
 * <p>
 * If the entry has a different type than {@link String}, then the provided
 * string will be parsed into the correct value:
 * {@snippet :
 * @ConfettiConfig
 * interface MyAwesomeConfig {
 *     @DefaultValue("42")
 *     int age();
 * }
 * }
 *
 * <p>
 * If the provided string is invalid, there should be an error at compile time
 * or if this value cannot be parsed at compile time, at runtime.
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DefaultValue {
    /**
     * Provides the specified default value.
     */
    String[] value();
}
