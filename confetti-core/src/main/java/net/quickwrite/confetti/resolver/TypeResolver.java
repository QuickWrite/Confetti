/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package net.quickwrite.confetti.resolver;

import net.quickwrite.confetti.ConfigNode;
import net.quickwrite.confetti.exception.ConversionException;

import java.lang.reflect.Type;
import java.util.Objects;

/**
 * Converts between configuration nodes and Java values.
 *
 * <p>
 * A {@code TypeResolver<T>} is responsible for converting a {@link ConfigNode}
 * into an instance of {@code T}. If the {@link #supportedTypes()} method is not
 * overwritten it is assumed that the class has a {@link ConfettiTypeResolver} annotation.
 *
 * <h2>Contract</h2>
 * <ul>
 *   <li>
 *       {@link #fromNode(ConfigNode, Type)} must convert the provided node into
 *       an instance of the requested {@link Type}. Implementations should throw
 *       {@link ConversionException} if any issues while converting arise.
 *   </li>
 *   <li>
 *       The {@code type} parameter supplies the requested target type.
 *       Resolvers that support multiple types should inspect the type.
 *   </li>
 *   <li>
 *       Implementations must not modify the provided {@link ConfigNode}.
 *   </li>
 *   <li>
 *       Implementations may assume the registry will call them with a
 *       {@code type} that they declared support for.
 *   </li>
 * </ul>
 *
 * <h2>Threading</h2>
 * <p>
 * Resolvers may be invoked concurrently. If a resolver holds
 * mutable state it must be thread safe to be executed.
 *
 * <h2>Example</h2>
 * {@snippet :
 *  @ConfettiTypeResolver(Integer.class)
 *  public final class IntegerResolver implements TypeResolver<Integer> {
 *      public Integer fromNode(final ConfigNode node, final Type type) {
 *          return (int) node.toValue().asLong();
 *      }
 *  }
 * }
 */
public interface TypeResolver<T> {

    /**
     * Convert the given configuration node into an instance of the requested type.
     *
     * @param node non null configuration node to convert
     * @param type non-null target type to produce; may include generic information
     * @return an instance of {@code T} representing the node content
     * @throws ConversionException when conversion cannot be performed
     */
    T fromNode(final ConfigNode node, final Type type) throws ConversionException;

    /**
     * Returns the concrete Java types this resolver supports.
     *
     * <p>
     * The default implementation reads the {@link ConfettiTypeResolver}
     * annotation on the resolver implementation and returns its {@code value()}.
     * Implementations may override this method to provide dynamic discovery or
     * to avoid the annotation requirement.
     *
     * @return an array of {@link Class} objects that this resolver can handle
     * @throws NullPointerException if the default implementation is used and the
     *                              {@code ConfettiTypeResolver} annotation is missing
     */
    default Class<?>[] supportedTypes() {
        return Objects.requireNonNull(this.getClass().getAnnotation(ConfettiTypeResolver.class)).value();
    }
}
