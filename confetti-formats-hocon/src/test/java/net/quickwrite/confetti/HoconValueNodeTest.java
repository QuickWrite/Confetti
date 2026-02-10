/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package net.quickwrite.confetti;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigValue;
import net.quickwrite.confetti.path.NodePath;
import net.quickwrite.confetti.path.impl.KeyPathSegment;
import net.quickwrite.confetti.path.impl.SimpleNodePath;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HoconValueNodeTest {

    @Test
    void constructorRejectsNull() {
        assertThrows(NullPointerException.class, () -> new HoconValueNode(null));
    }

    @Test
    void asStringAsLongAsDoubleAsBooleanAndValueReturnUnwrapped() {
        String hocon = "s=\"hello\"\n" +
                "i=123\n" +
                "d=3.14\n" +
                "b=true\n";
        Config cfg = ConfigFactory.parseString(hocon);
        ConfigValue s = cfg.root().get("s");
        ConfigValue i = cfg.root().get("i");
        ConfigValue d = cfg.root().get("d");
        ConfigValue b = cfg.root().get("b");

        HoconValueNode ns = new HoconValueNode(s);
        HoconValueNode ni = new HoconValueNode(i);
        HoconValueNode nd = new HoconValueNode(d);
        HoconValueNode nb = new HoconValueNode(b);

        assertEquals("hello", ns.asString());
        assertEquals(123L, ni.asLong());
        assertEquals(3.14d, nd.asDouble(), 1e-9);
        assertTrue(nb.asBoolean());

        assertEquals("hello", ns.value());
        assertEquals(123, ni.value()); // unwrapped returns Integer for small numbers
        assertEquals(3.14d, nd.value());
        assertEquals(true, nb.value());
    }

    @Test
    void typeAndToValueDefaults() {
        Config cfg = ConfigFactory.parseString("x=1");
        ConfigValue v = cfg.root().get("x");

        HoconValueNode node = new HoconValueNode(v);
        assertEquals(NodeType.VALUE, node.type());
        assertSame(node, node.toValue());
    }

    @Test
    void elementPathReflectsParentAndKey() {
        Config cfg = ConfigFactory.parseString("x=10");
        ConfigValue v = cfg.root().get("x");

        NodePath base = new SimpleNodePath(List.of(new KeyPathSegment("base")));
        ConfigNode parent = new AbstractConfigNode() {
            @Override public NodeType type() { return NodeType.OBJECT; }
            @Override public ObjectNode toObject() { throw new UnsupportedOperationException(); }
            @Override public ArrayNode toArray() { throw new UnsupportedOperationException(); }
            @Override public ValueNode toValue() { throw new UnsupportedOperationException(); }
            @Override public NodePath path() { return base; }
        };

        HoconValueNode node = new HoconValueNode(v, parent, new KeyPathSegment("k"));

        NodePath expected = base.appendPathSegment(new KeyPathSegment("k"));
        assertEquals(expected.toString(), node.path().toString());
    }
}

