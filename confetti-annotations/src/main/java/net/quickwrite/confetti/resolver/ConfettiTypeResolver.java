/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package net.quickwrite.confetti.resolver;

import java.lang.annotation.*;

/**
 * Marks a type resolver for discovery and registration.
 *
 * <p>
 * Apply this annotation to a {@code TypeResolver} implementation to declare
 * which Java types the resolver handles. The annotation value is an array of
 * {@link Class} objects; each class listed will be registered as a supported
 * target type for the annotated resolver.
 *
 * <h2>Contract</h2>
 * <ul>
 *   <li>
 *       The annotated class should implement the {@code TypeResolver<T>}
 *       interface.
 *   </li>
 *   <li>
 *       Each {@code Class} in {@code value()} should represent a concrete
 *       runtime type that the resolver can convert to and from a configuration
 *       node. <br>
 *       Parameterized types such as {@code List<String>} cannot be
 *       represented directly with {@code Class} and therefore should be handled
 *       by a generic resolver.
 *   </li>
 *   <li>
 *       If the resolver supports both a primitive and its boxed type, register
 *       both explicitly (for example {@code int.class} and {@code Integer.class})
 *       if both should be discoverable.
 *   </li>
 * </ul>
 *
 * <h2>Example</h2>
 * {@snippet :
 *  @ConfettiTypeResolver({ int.class, Integer.class })
 *  public final class IntegerResolver implements TypeResolver<Integer> {
 *      // implementation
 *  }
 * }
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ConfettiTypeResolver {
    /**
     * Provides the specified types that should be resolved.
     *
     * @return The specified values
     */
    Class<?>[] value();
}
