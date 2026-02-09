/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package net.quickwrite.confetti;

import net.quickwrite.confetti.path.NodePath;
import net.quickwrite.confetti.path.PathSegment;
import net.quickwrite.confetti.path.impl.KeyPathSegment;
import net.quickwrite.confetti.path.impl.SimpleNodePath;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class AbstractConfigNodeTest {
    private static final class ConcreteNode extends AbstractConfigNode {
        ConcreteNode() { super(); }
        ConcreteNode(final ConfigNode parent, final PathSegment key) { super(parent, key); }

        @Override
        public NodeType type() {
            return NodeType.NULL; // irrelevant for these tests
        }

        @Override
        public ObjectNode toObject() {
            throw new UnsupportedOperationException();
        }

        @Override
        public ArrayNode toArray() {
            throw new UnsupportedOperationException();
        }

        @Override
        public ValueNode toValue() {
            throw new UnsupportedOperationException();
        }
    }

    @Test
    void rootNodeHasNoKey() {
        ConcreteNode root = new ConcreteNode();

        assertFalse(root.key().isPresent(), "Root node must not have a key");
    }

    @Test
    void childNodeExposesProvidedKeyAndAppendsPathToParent() {
        NodePath parentPath = new SimpleNodePath(List.of(new KeyPathSegment("parent")));

        ConfigNode parent = new NullNode(new NullNode(), parentPath.segments().getFirst());

        KeyPathSegment childSeg = new KeyPathSegment("child");

        ConcreteNode child = new ConcreteNode(parent, childSeg);

        Optional<PathSegment> keyOpt = child.key();
        assertTrue(keyOpt.isPresent());
        assertEquals(childSeg, keyOpt.get());

        NodePath result = child.path();
        NodePath expected = parentPath.appendPathSegment(childSeg);

        assertEquals(expected.toString(), result.toString(), "Appended NodePath should match expected");
    }

    @Test
    void constructorRejectsNullParent() {
        KeyPathSegment segment = new KeyPathSegment("seg");

        assertThrows(NullPointerException.class, () -> new ConcreteNode(null, segment));
    }

    @Test
    void constructorRejectsNullKey() {
        ConfigNode parent = new NullNode();

        assertThrows(NullPointerException.class, () -> new ConcreteNode(parent, null));
    }
}
