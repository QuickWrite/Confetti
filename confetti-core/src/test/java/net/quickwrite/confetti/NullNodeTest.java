/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package net.quickwrite.confetti;

import net.quickwrite.confetti.path.NodePath;
import net.quickwrite.confetti.path.PathSegment;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link NullNode} using camelCase test names and no underscores
 * in assertion messages or identifiers.
 */
public class NullNodeTest {

    /**
     * Minimal ArrayNode implementation used as a parent in some tests.
     * Only the methods required by the tests are implemented.
     */
    private record SimpleParentNode(NodePath path) implements ArrayNode {
        @Override
        public ConfigNode get(int index) {
            return null;
        }

        @Override
        public int size() {
            return 0;
        }

        @Override
        public List<ConfigNode> toList() {
            return List.of();
        }

        @Override
        public NodeType type() {
            return NodeType.NULL;
        }

        @Override
        public Optional<PathSegment> key() {
            return Optional.empty();
        }
    }

    @Test
    public void defaultConstructorReturnsNullType() {
        ConfigNode node = new NullNode();

        assertEquals(NodeType.NULL, node.type(), "Default NullNode should report NodeType.NULL");
    }

    @Test
    public void defaultConstructorHasNoKey() {
        ConfigNode node = new NullNode();

        assertTrue(node.key().isEmpty(), "Root-level NullNode should have no key");
    }

    @Test
    public void defaultConstructorPathIsEmpty() {
        ConfigNode node = new NullNode();

        assertTrue(node.path().isEmpty(), "Root-level NullNode should expose an empty path");
        assertEquals(NodePath.empty(), node.path(), "Root-level NullNode path should equal NodePath.empty()");
    }

    @Test
    public void childConstructorStoresParentAndKey() {
        NodePath parentPath = NodePath.empty()
                .appendPathSegment(PathSegment.key("a"))
                .appendPathSegment(PathSegment.key("b"));

        ConfigNode parent = new SimpleParentNode(parentPath);
        PathSegment key = PathSegment.key("child");

        NullNode node = new NullNode(parent, key);

        assertEquals(Optional.of(key), node.key(), "Child NullNode should expose the provided key");
    }

    @Test
    public void childConstructorPathIsParentPathPlusKey() {
        NodePath parentPath = NodePath.empty()
                .appendPathSegment(PathSegment.key("a"))
                .appendPathSegment(PathSegment.key("b"));

        ConfigNode parent = new SimpleParentNode(parentPath);
        PathSegment key = PathSegment.key("child");

        NullNode node = new NullNode(parent, key);

        NodePath expected = parentPath.appendPathSegment(key);
        assertEquals(expected.segments(), node.path().segments(), "Child NullNode path should be parent's path plus the key");
    }

    @Test
    public void constructorThrowsOnNullParent() {
        assertThrows(NullPointerException.class, () -> new NullNode(null, PathSegment.key("x")),
                "Constructor should throw when parent is null");
    }

    @Test
    public void constructorThrowsOnNullKey() {
        ConfigNode parent = new NullNode();
        assertThrows(NullPointerException.class, () -> new NullNode(parent, null),
                "Constructor should throw when key is null");
    }

    @Test
    public void pathSegmentsAreImmutable() {
        ConfigNode node = new NullNode();
        NodePath path = node.path();

        assertThrows(UnsupportedOperationException.class, () -> path.segments().add(PathSegment.key("x")),
                "Returned segments list should be unmodifiable");
    }
}

