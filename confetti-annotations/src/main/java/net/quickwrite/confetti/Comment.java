/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package net.quickwrite.confetti;

import java.lang.annotation.*;

/**
 * Provides a comment for the configuration entry.
 * The contents of this annotation will override any other provided comment.
 *
 * <p>
 * The comment is being applied in the interface to the methods:
 * {@snippet :
 * @ConfettiConfig
 * interface MyAwesomeConfig {
 *     @Comment("This is my awesome comment")
 *     String name();
 * }
 * }
 *
 * <p>
 * If there are other ways of specifying a comment, like JavaDocs, then
 * the annotation will take precedence.
 * {@snippet :
 * @ConfettiConfig
 * interface MyAwesomeConfig {
 *     /// This JavaDoc comment won't be shown
 *     /// in the config
 *     @Comment("This comment will be shown in the config")
 *     String name();
 * }
 * }
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.SOURCE)
public @interface Comment {
    /**
     * Provides the specified comment for the configuration entry
     */
    String value();
}
