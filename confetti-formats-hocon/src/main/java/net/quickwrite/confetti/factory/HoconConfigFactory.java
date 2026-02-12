/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package net.quickwrite.confetti.factory;

import com.typesafe.config.ConfigException;
import net.quickwrite.confetti.ConfigNode;
import net.quickwrite.confetti.HoconObjectNode;
import net.quickwrite.confetti.exception.ConfigParseException;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Path;
import java.util.Objects;

/**
 * {@link ConfigFactory} implementation that uses the Typesafe Config library
 * (HOCON) to parse configuration sources into {@link ConfigNode} trees.
 *
 * <p>
 * This factory adapts the Typesafe Config parsing APIs to the {@link ConfigNode}
 * model used by this library. Conversion of the parsed HOCON AST to
 * {@link ConfigNode} instances is performed by {@link HoconObjectNode} and
 * its collaborators.
 */
public class HoconConfigFactory implements ConfigFactory {
    /** {@inheritDoc} */
    @Override
    public ConfigNode parse(final Reader reader) throws IOException, ConfigParseException {
        Objects.requireNonNull(reader, "The reader cannot be null.");

        try {
            return new HoconObjectNode(com.typesafe.config.ConfigFactory.parseReader(reader).root());
        } catch (final ConfigException.IO exception) {
            throw new IOException(exception);
        } catch (final ConfigException exception) {
            throw new ConfigParseException(exception);
        }
    }

    /** {@inheritDoc} */
    @Override
    public ConfigNode parse(final Path path) throws IOException, ConfigParseException {
        Objects.requireNonNull(path, "The path cannot be null.");

        // It seems that the typesafe config library just does not care if the file
        // already exists or not. This is ensuring that the file does exist.
        if (!path.toFile().exists()) {
            throw new IOException("The provided file path does not exist.");
        }

        try {
            return new HoconObjectNode(com.typesafe.config.ConfigFactory.parseFile(path.toFile()).root());
        } catch (final ConfigException exception) {
            throw new ConfigParseException(exception);
        }
    }
}
