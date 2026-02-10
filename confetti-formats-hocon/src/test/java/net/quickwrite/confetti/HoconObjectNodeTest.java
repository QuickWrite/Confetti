/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package net.quickwrite.confetti;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigObject;
import net.quickwrite.confetti.path.NodePath;
import net.quickwrite.confetti.path.impl.KeyPathSegment;
import net.quickwrite.confetti.path.impl.SimpleNodePath;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class HoconObjectNodeTest {

    @Test
    void getReturnsOptionalEmptyForMissingKey() {
        Config cfg = ConfigFactory.parseString("a=1");
        ConfigObject root = cfg.root();

        HoconObjectNode node = new HoconObjectNode(root);

        assertEquals(Optional.empty(), node.get("missing"));
    }

    @Test
    void getAndKeysAndValuesAndToMapBehavior() {
        String hocon = "x = 1, y = { z = \"hello\" }";
        Config cfg = ConfigFactory.parseString(hocon);
        ConfigObject root = cfg.root();

        HoconObjectNode node = new HoconObjectNode(root);

        assertTrue(node.keys().contains("x"));
        assertTrue(node.keys().contains("y"));

        Optional<ConfigNode> xOpt = node.get("x");
        assertTrue(xOpt.isPresent());
        assertEquals(NodeType.VALUE, xOpt.get().type());

        Optional<ConfigNode> yOpt = node.get("y");
        assertTrue(yOpt.isPresent());
        assertEquals(NodeType.OBJECT, yOpt.get().type());

        assertEquals(2, node.values().size());

        Map<String, ConfigNode> mapView = node.toMap();
        assertTrue(mapView.containsKey("x"));
        assertTrue(mapView.containsKey("y"));

        assertNull(mapView.get(123));

        boolean foundX = false;
        for (Map.Entry<String, ConfigNode> e : mapView.entrySet()) {
            if ("x".equals(e.getKey())) {
                assertEquals(NodeType.VALUE, e.getValue().type());
                foundX = true;
            }
        }
        assertTrue(foundX);
    }

    @Test
    void childNodesHaveCorrectPathWithParent() {
        String hocon = "a = 5";
        Config cfg = ConfigFactory.parseString(hocon);
        ConfigObject root = cfg.root();

        NodePath parentPath = new SimpleNodePath(java.util.List.of(new KeyPathSegment("root")));
        ConfigNode inner = getConfigNode(parentPath, root);

        NodePath expected = parentPath.appendPathSegment(new KeyPathSegment("child")).appendPathSegment(new KeyPathSegment("a"));
        assertEquals(expected.toString(), inner.path().toString());
    }

    private static ConfigNode getConfigNode(NodePath parentPath, ConfigObject root) {
        final ConfigNode parent = new AbstractConfigNode() {
            @Override public NodeType type() { return NodeType.OBJECT; }
            @Override public ObjectNode toObject() { throw new UnsupportedOperationException(); }
            @Override public ArrayNode toArray() { throw new UnsupportedOperationException(); }
            @Override public ValueNode toValue() { throw new UnsupportedOperationException(); }
            @Override public NodePath path() { return parentPath; }
        };

        HoconObjectNode child = new HoconObjectNode(root, parent, new KeyPathSegment("child"));

        return child.get("a").orElseThrow();
    }
}
