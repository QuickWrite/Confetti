package net.quickwrite.confetti.factory;

import net.quickwrite.confetti.*;
import net.quickwrite.confetti.exception.ConfigParseException;
import net.quickwrite.confetti.path.NodePath;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class ConfigFactoryTest {
    private static final class TextConfigNode extends AbstractConfigNode {
        private final String text;

        TextConfigNode(final String text) { this.text = text; }

        String getText() { return text; }

        @Override public NodeType type() { return NodeType.VALUE; }
        @Override public ObjectNode toObject() { throw new UnsupportedOperationException(); }
        @Override public ArrayNode toArray() { throw new UnsupportedOperationException(); }
        @Override public ValueNode toValue() { throw new UnsupportedOperationException(); }
        @Override public NodePath path() { return NodePath.empty(); }
    }

    private static final class TestConfigFactory implements ConfigFactory {
        @Override
        public ConfigNode parse(final Reader reader) throws IOException, ConfigParseException {
            if (reader == null) throw new NullPointerException("reader");

            final StringBuilder sb = new StringBuilder();
            final char[] buf = new char[128];
            int r;
            while ((r = reader.read(buf)) != -1) {
                sb.append(buf, 0, r);
            }
            final String s = sb.toString();

            if ("ioerror".equals(s)) {
                throw new IOException("simulated IO");
            }
            if ("parseerror".equals(s)) {
                throw new ConfigParseException("simulated parse error");
            }
            return new TextConfigNode(s);
        }
    }

    private static final class CloseTrackingInputStream extends ByteArrayInputStream {
        boolean closed = false;

        CloseTrackingInputStream(final byte[] buf) {
            super(buf);
        }

        @Override
        public void close() throws IOException {
            closed = true;
            super.close();
        }

        boolean isClosed() { return closed; }
    }

    @Test
    void parseStringDelegatesToReader() {
        ConfigFactory factory = new TestConfigFactory();

        ConfigNode node = factory.parse("hello");
        assertInstanceOf(TextConfigNode.class, node);
        assertEquals("hello", ((TextConfigNode) node).getText());
    }

    @Test
    void parseStringWrapsReaderIOExceptionAsConfigParseException() {
        ConfigFactory factory = new TestConfigFactory();

        // Test: parse(reader) will throw IOException for input "ioerror".
        // parse(String) should catch that and wrap it into ConfigParseException.
        assertThrows(ConfigParseException.class, () -> factory.parse("ioerror"));
    }

    @Test
    void parseStringPropagatesConfigParseException() {
        ConfigFactory factory = new TestConfigFactory();

        assertThrows(ConfigParseException.class, () -> factory.parse("parseerror"));
    }

    @Test
    void parsePathReadsFileContents() throws Exception {
        Path tmp = Files.createTempFile("cfgfactory-test", ".txt");
        try {
            Files.writeString(tmp, "fromfile", StandardCharsets.UTF_8);

            ConfigFactory factory = new TestConfigFactory();

            ConfigNode node = factory.parse(tmp);
            assertInstanceOf(TextConfigNode.class, node);
            assertEquals("fromfile", ((TextConfigNode) node).getText());
        } finally {
            Files.deleteIfExists(tmp);
        }
    }

    @Test
    void parseInputStreamClosesStreamAndParsesContent() throws Exception {
        byte[] bytes = "payload".getBytes(StandardCharsets.UTF_8);
        CloseTrackingInputStream in = new CloseTrackingInputStream(bytes);

        ConfigFactory factory = new TestConfigFactory();

        ConfigNode node = factory.parse(in, StandardCharsets.UTF_8);
        assertInstanceOf(TextConfigNode.class, node);
        assertEquals("payload", ((TextConfigNode) node).getText());

        assertTrue(in.isClosed(), "InputStream should have been closed by default method");
    }

    @Test
    void parseInputStreamClosesStreamWhenParseThrowsIOException() {
        byte[] bytes = "ioerror".getBytes(StandardCharsets.UTF_8);
        CloseTrackingInputStream in = new CloseTrackingInputStream(bytes);

        ConfigFactory factory = new TestConfigFactory();

        assertThrows(IOException.class, () -> factory.parse(in, StandardCharsets.UTF_8));
        assertTrue(in.isClosed(), "InputStream should be closed even when parse throws");
    }

    @Test
    void parseNullArgumentsThrowNpe() {
        ConfigFactory factory = new TestConfigFactory();

        assertThrows(NullPointerException.class, () -> factory.parse((Path) null));
        assertThrows(NullPointerException.class, () -> factory.parse(null, StandardCharsets.UTF_8));
        assertThrows(NullPointerException.class, () -> factory.parse(new ByteArrayInputStream(new byte[0]), null));
        assertThrows(NullPointerException.class, () -> factory.parse((String) null));
    }
}
