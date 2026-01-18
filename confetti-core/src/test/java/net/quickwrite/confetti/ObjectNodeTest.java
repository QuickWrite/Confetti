/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package net.quickwrite.confetti;

import net.quickwrite.confetti.exception.InvalidNodeTypeException;
import net.quickwrite.confetti.path.NodePath;
import net.quickwrite.confetti.path.PathSegment;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

public class ObjectNodeTest {
    // Small implementation of Interface
    private static final class SimpleObjectNode implements ObjectNode {
        private final Map<String, ConfigNode> delegate;

        SimpleObjectNode(Map<String, ConfigNode> delegate) {
            this.delegate = delegate;
        }

        @Override
        public Optional<ConfigNode> get(final String key) {
            return Optional.ofNullable(delegate.get(key));
        }

        @Override
        public Set<String> keys() {
            return delegate.keySet();
        }

        @Override
        public Collection<ConfigNode> values() {
            return delegate.values();
        }

        @Override
        public Map<String, ConfigNode> toMap() {
            return Map.copyOf(delegate);
        }

        @Override
        public Optional<PathSegment> key() {
            return Optional.empty();
        }

        @Override
        public NodePath path() {
            return null;
        }
    }

    private static final class CountingObjectNode implements ObjectNode {
        private final Map<String, ConfigNode> delegate;
        final AtomicInteger getCalls = new AtomicInteger(0);
        final AtomicInteger keysCalls = new AtomicInteger(0);

        CountingObjectNode(Map<String, ConfigNode> delegate) {
            this.delegate = delegate;
        }

        @Override
        public Optional<ConfigNode> get(final String key) {
            getCalls.incrementAndGet();
            return Optional.ofNullable(delegate.get(key));
        }

        @Override
        public Set<String> keys() {
            keysCalls.incrementAndGet();
            return delegate.keySet();
        }

        @Override
        public Collection<ConfigNode> values() {
            return delegate.values();
        }

        @Override
        public Map<String, ConfigNode> toMap() {
            return Map.copyOf(delegate);
        }

        @Override
        public Optional<PathSegment> key() {
            return Optional.empty();
        }

        @Override
        public NodePath path() {
            return null;
        }
    }

    @Test
    void returnCorrectValue() {
        ConfigNode value = new SimpleObjectNode(Map.of());

        Map<String, ConfigNode> map = new HashMap<>();
        map.put("a", value);

        SimpleObjectNode node = new SimpleObjectNode(map);

        Optional<ConfigNode> present = node.get("a");
        assertTrue(present.isPresent());
        assertSame(value, present.get());

        Optional<ConfigNode> missing = node.get("missing");
        assertFalse(missing.isPresent());
    }

    @Test
    void consistentKeysAndValues() {
        ConfigNode a = new SimpleObjectNode(Map.of());
        ConfigNode b = new SimpleObjectNode(Map.of());

        Map<String, ConfigNode> map = new LinkedHashMap<>();
        map.put("k1", a);
        map.put("k2", b);

        SimpleObjectNode node = new SimpleObjectNode(map);

        // keys() returns the same key set as the backing map
        Set<String> keys = node.keys();
        assertEquals(map.keySet(), keys);

        // values() contains the same elements (order for values() follows insertion order for LinkedHashMap)
        Collection<ConfigNode> values = node.values();
        assertEquals(map.values().size(), values.size());
        assertTrue(values.containsAll(map.values()));

        // toMap() returns a map equal to the backing map
        Map<String, ConfigNode> snapshot = node.toMap();
        assertEquals(map, snapshot);
    }

    @Test
    void correctToMap() {
        ConfigNode a = new SimpleObjectNode(Map.of());
        ConfigNode b = new SimpleObjectNode(Map.of());

        Map<String, ConfigNode> backing = new HashMap<>();
        backing.put("x", a);

        SimpleObjectNode node = new SimpleObjectNode(backing);

        Map<String, ConfigNode> snapshot = node.toMap();
        assertEquals(1, snapshot.size());
        assertTrue(snapshot.containsKey("x"));

        // modify the original backing map
        backing.put("y", b);

        // snapshot should remain unchanged because toMap returned an immutable copy
        assertEquals(1, snapshot.size());
        assertFalse(snapshot.containsKey("y"));

        // and a fresh call to toMap should reflect the new entry
        Map<String, ConfigNode> later = node.toMap();
        assertTrue(later.containsKey("y"));
    }

    @Test
    void correctCountGetAndKeys() {
        ConfigNode a = new SimpleObjectNode(Map.of());

        Map<String, ConfigNode> map = new HashMap<>();
        map.put("aa", a);

        CountingObjectNode node = new CountingObjectNode(map);

        assertEquals(0, node.getCalls.get());
        assertEquals(0, node.keysCalls.get());

        node.get("aa");
        node.get("missing");
        node.keys();
        node.keys();

        assertEquals(2, node.getCalls.get(), "get should have been called twice");
        assertEquals(2, node.keysCalls.get(), "keys should have been called twice");
    }

    @Test
    public void returnObjectNodeType() {
        ConfigNode node = new SimpleObjectNode(Map.of());

        assertEquals(NodeType.OBJECT, node.type(), "any ObjectNode implementation should return NodeType.OBJECT from default type()");
    }

    @Test
    public void returnFalseIsArray() {
        ConfigNode node = new SimpleObjectNode(Map.of());

        assertFalse(node.isArray());
    }

    @Test
    public void throwFromToArray() {
        ConfigNode node = new SimpleObjectNode(Map.of());

        assertThrowsExactly(InvalidNodeTypeException.class, node::toArray, "Default toArray() should throw InvalidNodeTypeException");
    }

    @Test
    public void returnTrueIsObject() {
        ConfigNode node = new SimpleObjectNode(Map.of());

        assertTrue(node.isObject());
    }

    @Test
    public void returnSelfFromToObject() {
        ConfigNode node = new SimpleObjectNode(Map.of());

        assertSame(node, node.toObject(), "Default toObject() should return the same instance");
    }

    @Test
    public void returnFalseIsValue() {
        ConfigNode node = new SimpleObjectNode(Map.of());

        assertFalse(node.isValue());
    }

    @Test
    public void throwFromToValue() {
        ConfigNode node = new SimpleObjectNode(Map.of());

        assertThrowsExactly(InvalidNodeTypeException.class, node::toValue, "Default toValue() should throw InvalidNodeTypeException.");
    }
}

