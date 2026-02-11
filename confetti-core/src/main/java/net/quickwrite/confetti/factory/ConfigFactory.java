/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package net.quickwrite.confetti.factory;

import net.quickwrite.confetti.ConfigNode;
import net.quickwrite.confetti.exception.ConfigParseException;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

/**
 * Factory that parses configuration sources into {@link ConfigNode} trees.
 *
 * <p>
 * Implementations must implement {@link #parse(Reader)} as the core parsing
 * entry point. Other parsing methods provide convenient overloads that delegate
 * to the reader-based method.
 */
public interface ConfigFactory {
    /**
     * Parse the content of the given {@link Reader} into a {@link ConfigNode}.
     *
     * <p>
     * This method does <em>not</em> close the supplied {@code Reader}; the
     * caller is responsible for closing it.
     *
     * @param reader the reader to parse; must not be {@code null}
     * @return A parsed {@link ConfigNode}
     * @throws IOException if an I/O error occurs while reading
     * @throws ConfigParseException if the input is syntactically invalid for this parser
     * @throws NullPointerException if {@code reader} is {@code null}
     */
    ConfigNode parse(final Reader reader) throws IOException, ConfigParseException;

    /**
     * Parse the contents of the file at {@code path} using the platform default charset.
     *
     * @param path the file path; must not be {@code null}
     * @return A parsed {@link ConfigNode}
     * @throws IOException if an I/O error occurs while reading
     * @throws ConfigParseException if the input is syntactically invalid for this parser
     * @throws NullPointerException if {@code path} is {@code null}
     */
    default ConfigNode parse(final Path path) throws IOException, ConfigParseException {
        Objects.requireNonNull(path, "The path cannot be null.");

        try (final Reader r = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            return parse(r);
        }
    }

    /**
     * Parse the contents of an {@link InputStream} using the given {@link Charset}.
     *
     * <p>
     * The method closes the given {@link InputStream} (it consumes it completely).
     *
     * @param in the input stream; must not be {@code null}
     * @param charset the charset to decode the bytes; must not be {@code null}
     * @return A parsed {@link ConfigNode}
     * @throws IOException if an I/O error occurs while reading
     * @throws ConfigParseException if the input is syntactically invalid for this parser
     * @throws NullPointerException if any argument is {@code null}
     */
    default ConfigNode parse(final InputStream in, final Charset charset) throws IOException, ConfigParseException {
        Objects.requireNonNull(in, "The InputStream cannot be null.");
        Objects.requireNonNull(charset, "The charset cannot be null.");

        try (final Reader r = new InputStreamReader(in, charset)) {
            return parse(r);
        }
    }

    /**
     * Parse the given string into a ConfigNode.
     *
     * <p>
     * This is a convenience method and may be implemented by delegating to
     * {@link #parse(Reader)}. No I/O exceptions should normally occur here;
     * implementations may still throw {@link ConfigParseException}.
     *
     * @param input the input string; must not be {@code null}
     * @return A parsed {@link ConfigNode}
     * @throws ConfigParseException if the input is syntactically invalid for this parser
     * @throws NullPointerException if {@code input} is {@code null}
     */
    default ConfigNode parse(final String input) throws ConfigParseException {
        Objects.requireNonNull(input, "The input cannot be null.");

        try (final Reader r = new StringReader(input)) {
            // StringReader.close() is a no-op, but keep try-with-resources for clarity.
            return parse(r);
        } catch (final IOException e) {
            // unlikely with StringReader; wrap as parse exception
            throw new ConfigParseException("Unexpected I/O error while parsing string", e);
        }
    }
}
