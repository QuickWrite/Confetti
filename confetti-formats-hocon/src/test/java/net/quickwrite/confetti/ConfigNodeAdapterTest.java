/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package net.quickwrite.confetti;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigObject;
import com.typesafe.config.ConfigValue;
import net.quickwrite.confetti.path.NodePath;
import net.quickwrite.confetti.path.impl.KeyPathSegment;
import net.quickwrite.confetti.path.impl.SimpleNodePath;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
public class ConfigNodeAdapterTest {
    private static final class ParentNode extends AbstractConfigNode {
        private final NodePath path;

        ParentNode(final NodePath path) {
            this.path = path;
        }

        @Override
        public NodeType type() { return NodeType.OBJECT; }

        @Override
        public ObjectNode toObject() { throw new UnsupportedOperationException(); }

        @Override
        public ArrayNode toArray() { throw new UnsupportedOperationException(); }

        @Override
        public ValueNode toValue() { throw new UnsupportedOperationException(); }

        @Override
        public NodePath path() { return path; }
    }

    @Test
    void nullConfigValueReturnsNull() {
        ConfigNode result = ConfigNodeAdapter.toConfigNode(null, new ParentNode(NodePath.empty()), new KeyPathSegment("x"));
        assertNull(result, "Converter should return null for a null ConfigValue");
    }

    @Test
    void convertsObjectListValueAndPrimitiveTypes() {
        // Build a HOCON config containing an object, a list, a string, a number and null
        String hocon = "obj: { a: 1 }, list: [1, 2], b: true, s: \"hello\", n: 42, nnull: null";
        Config cfg = ConfigFactory.parseString(hocon);
        ConfigObject root = cfg.root();

        // parent with a known path
        NodePath parentPath = new SimpleNodePath(java.util.List.of(new KeyPathSegment("parent")));
        ParentNode parent = new ParentNode(parentPath);

        // OBJECT -> HoconObjectNode
        ConfigValue objVal = root.get("obj");
        ConfigNode objNode = ConfigNodeAdapter.toConfigNode(objVal, parent, new KeyPathSegment("obj"));
        assertNotNull(objNode);
        assertInstanceOf(HoconObjectNode.class, objNode, "obj should convert to HoconObjectNode");
        assertEquals(NodeType.OBJECT, objNode.type());

        // LIST -> HoconArrayNode
        ConfigValue listVal = root.get("list");
        ConfigNode listNode = ConfigNodeAdapter.toConfigNode(listVal, parent, new KeyPathSegment("list"));
        assertNotNull(listNode);
        assertInstanceOf(HoconArrayNode.class, listNode, "list should convert to HoconArrayNode");
        assertEquals(NodeType.ARRAY, listNode.type());

        // Boolean -> HoconValueNode
        ConfigValue bVal = root.get("b");
        ConfigNode bNode = ConfigNodeAdapter.toConfigNode(bVal, parent, new KeyPathSegment("s"));
        assertNotNull(bNode);
        assertEquals(NodeType.VALUE, bNode.type());

        // STRING -> HoconValueNode
        ConfigValue sVal = root.get("s");
        ConfigNode sNode = ConfigNodeAdapter.toConfigNode(sVal, parent, new KeyPathSegment("s"));
        assertNotNull(sNode);
        assertEquals(NodeType.VALUE, sNode.type());

        // NUMBER -> HoconValueNode
        ConfigValue nVal = root.get("n");
        ConfigNode nNode = ConfigNodeAdapter.toConfigNode(nVal, parent, new KeyPathSegment("n"));
        assertNotNull(nNode);
        assertEquals(NodeType.VALUE, nNode.type());

        // NULL -> NullNode
        ConfigValue nullVal = root.get("nnull");
        ConfigNode nullNode = ConfigNodeAdapter.toConfigNode(nullVal, parent, new KeyPathSegment("nnull"));
        assertNotNull(nullNode);
        assertEquals(NodeType.NULL, nullNode.type());
    }

    @Test
    void convertedNodeHasCorrectPath() {
        String hocon = "inner: { x: 1 }";
        Config cfg = ConfigFactory.parseString(hocon);
        ConfigObject root = cfg.root();

        NodePath base = new SimpleNodePath(java.util.List.of(new KeyPathSegment("base")));
        ParentNode parent = new ParentNode(base);

        ConfigValue objVal = root.get("inner");
        ConfigNode node = ConfigNodeAdapter.toConfigNode(objVal, parent, new KeyPathSegment("inner"));

        // The converted node's path should be base/inner
        NodePath expected = base.appendPathSegment(new KeyPathSegment("inner"));
        assertEquals(expected.toString(), node.path().toString());
    }
}

