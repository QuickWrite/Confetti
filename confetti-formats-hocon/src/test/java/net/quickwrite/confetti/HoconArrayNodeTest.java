/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package net.quickwrite.confetti;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigList;
import net.quickwrite.confetti.path.NodePath;
import net.quickwrite.confetti.path.impl.IndexPathSegment;
import net.quickwrite.confetti.path.impl.KeyPathSegment;
import net.quickwrite.confetti.path.impl.SimpleNodePath;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HoconArrayNodeTest {

    @Test
    void constructorRejectsNull() {
        assertThrows(NullPointerException.class, () -> new HoconArrayNode(null));
    }

    @Test
    void sizeAndGetReturnConvertedElements() {
        String hocon = "arr = [1, {a = 2}, [3], \"s\", null, true]";
        Config cfg = ConfigFactory.parseString(hocon);
        ConfigList list = cfg.getList("arr");

        HoconArrayNode node = new HoconArrayNode(list);

        assertEquals(list.size(), node.size());

        assertEquals(NodeType.VALUE, node.get(0).type());
        assertEquals(NodeType.OBJECT, node.get(1).type());
        assertEquals(NodeType.ARRAY, node.get(2).type());
        assertEquals(NodeType.VALUE, node.get(3).type());
        assertEquals(NodeType.NULL, node.get(4).type());
        assertEquals(NodeType.VALUE, node.get(5).type());
    }

    @Test
    void toListReturnsViewWithSameOrder() {
        String hocon = "arr = [10, 20, 30]";
        Config cfg = ConfigFactory.parseString(hocon);
        ConfigList list = cfg.getList("arr");

        HoconArrayNode node = new HoconArrayNode(list);

        List<ConfigNode> view = node.toList();
        assertEquals(3, view.size());
        assertEquals(NodeType.VALUE, view.get(0).type());
        assertEquals(NodeType.VALUE, view.get(1).type());
        assertEquals(NodeType.VALUE, view.get(2).type());

        // modifying the original ConfigList is not supported here (immutable config),
        // but we can assert accesses map to the same indices
        assertEquals(node.get(1).type(), view.get(1).type());
    }

    @Test
    void elementPathsReflectParentAndIndex() {
        String hocon = "items = [ {x=1}, 2 ]";
        Config cfg = ConfigFactory.parseString(hocon);
        ConfigList list = cfg.getList("items");

        // create a parent with a known path
        NodePath base = new SimpleNodePath(List.of(new KeyPathSegment("base")));
        ConfigNode first = getConfigNode(base, list);

        NodePath expected = base.appendPathSegment(new KeyPathSegment("arr")).appendPathSegment(new IndexPathSegment(0));
        assertEquals(expected.toString(), first.path().toString());
    }

    private static ConfigNode getConfigNode(NodePath base, ConfigList list) {
        final ConfigNode parent = new AbstractConfigNode() {
            @Override public NodeType type() { return NodeType.OBJECT; }
            @Override public ObjectNode toObject() { throw new UnsupportedOperationException(); }
            @Override public ArrayNode toArray() { throw new UnsupportedOperationException(); }
            @Override public ValueNode toValue() { throw new UnsupportedOperationException(); }
            @Override public NodePath path() { return base; }
        };

        HoconArrayNode node = new HoconArrayNode(list, parent, new KeyPathSegment("arr"));

        return node.get(0);
    }
}

