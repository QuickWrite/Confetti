/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package net.quickwrite.confetti.factory;

import net.quickwrite.confetti.ConfigNode;
import net.quickwrite.confetti.HoconObjectNode;
import net.quickwrite.confetti.NodeType;
import net.quickwrite.confetti.exception.ConfigParseException;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class HoconConfigFactoryTest {
    @Test
    void parseReaderValidHoconReturnsHoconObjectNode() throws Exception {
        HoconConfigFactory factory = new HoconConfigFactory();

        String hocon = """
                a = 1
                b = { x = "y" }
                """;
        Reader reader = new StringReader(hocon);

        ConfigNode node = factory.parse(reader);
        assertInstanceOf(HoconObjectNode.class, node);

        HoconObjectNode obj = (HoconObjectNode) node;
        assertTrue(obj.get("a").isPresent());
        assertEquals(NodeType.VALUE, obj.get("a").get().type());
        assertTrue(obj.get("b").isPresent());
        assertEquals(NodeType.OBJECT, obj.get("b").get().type());
    }

    @Test
    void parsePathValidFileReturnsHoconObjectNode() throws Exception {
        HoconConfigFactory factory = new HoconConfigFactory();

        Path tmp = Files.createTempFile("hocon-factory-test", ".conf");
        try {
            Files.writeString(tmp, "k = 42\n");

            ConfigNode node = factory.parse(tmp);
            assertInstanceOf(HoconObjectNode.class, node);

            HoconObjectNode obj = (HoconObjectNode) node;
            assertTrue(obj.get("k").isPresent());
            assertEquals(NodeType.VALUE, obj.get("k").get().type());
        } finally {
            Files.deleteIfExists(tmp);
        }
    }

    @Test
    void parseReaderWithMalformedHoconThrowsConfigParseException() {
        HoconConfigFactory factory = new HoconConfigFactory();

        // malformed (unclosed brace)
        Reader reader = new StringReader("a = {\n");

        assertThrows(ConfigParseException.class, () -> factory.parse(reader));
    }

    @Test
    void parsePathWithMalformedHoconThrowsConfigParseException() throws Exception {
        HoconConfigFactory factory = new HoconConfigFactory();

        Path tmp = Files.createTempFile("hocon-factory-test-bad", ".conf");
        try {
            Files.writeString(tmp, "a = {\n");

            assertThrows(ConfigParseException.class, () -> factory.parse(tmp));
        } finally {
            Files.deleteIfExists(tmp);
        }
    }

    @Test
    void parseReaderWithReaderThrowingIOExceptionLeadsToIOException() {
        HoconConfigFactory factory = new HoconConfigFactory();

        try (final Reader broken = new Reader() {
            @Override
            public int read(char[] cbuf, int off, int len) throws IOException {
                throw new IOException("simulated IO");
            }

            @Override
            public void close() throws IOException {
            }
        }) {
            assertThrows(IOException.class, () -> factory.parse(broken));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void parsePathWithNonExistingFileThrowsIOException() {
        HoconConfigFactory factory = new HoconConfigFactory();

        // This is not a great way of ensuring that the file does not exist
        Path nonExisting = Path.of("definitely-does-not-exist-" + System.nanoTime() + ".conf");

        assertThrows(IOException.class, () -> factory.parse(nonExisting));
    }

    @Test
    void parseReaderNullThrowsNpe() {
        HoconConfigFactory factory = new HoconConfigFactory();
        assertThrows(NullPointerException.class, () -> factory.parse((Reader) null));
    }

    @Test
    void parsePathNullThrowsNpe() {
        HoconConfigFactory factory = new HoconConfigFactory();
        assertThrows(NullPointerException.class, () -> factory.parse((Path) null));
    }
}
