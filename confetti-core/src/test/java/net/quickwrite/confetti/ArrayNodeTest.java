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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

public class ArrayNodeTest {
    // Small implementation of Interface
    private static final class SimpleArrayNode implements ArrayNode {
        private final List<ConfigNode> delegate;

        SimpleArrayNode(List<ConfigNode> delegate) {
            this.delegate = delegate;
        }

        @Override
        public ConfigNode get(final int index) {
            return delegate.get(index);
        }

        @Override
        public int size() {
            return delegate.size();
        }

        @Override
        public List<ConfigNode> toList() {
            return List.copyOf(delegate);
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

    private static final class CountingArrayNode implements ArrayNode {
        private final List<ConfigNode> delegate;
        final AtomicInteger sizeCalls = new AtomicInteger(0);
        final AtomicInteger getCalls = new AtomicInteger(0);
        final List<Integer> getIndices = new ArrayList<>();

        CountingArrayNode(List<ConfigNode> delegate) {
            this.delegate = delegate;
        }

        @Override
        public ConfigNode get(final int index) {
            getCalls.incrementAndGet();
            getIndices.add(index);
            return delegate.get(index);
        }

        @Override
        public int size() {
            sizeCalls.incrementAndGet();
            return delegate.size();
        }

        @Override
        public List<ConfigNode> toList() {
            return List.copyOf(delegate);
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
    public void iterateElementsInOrder() {
        ConfigNode a = new SimpleArrayNode(List.of());
        ConfigNode b = new SimpleArrayNode(List.of());
        ConfigNode c = new SimpleArrayNode(List.of());

        SimpleArrayNode node = new SimpleArrayNode(List.of(a, b, c));

        // use iterator() (default implementation) to collect items
        List<ConfigNode> collected = new ArrayList<>();
        for (ConfigNode item : node) {
            collected.add(item);
        }

        assertEquals(3, collected.size(), "Iterator should visit three elements");
        assertSame(a, collected.get(0));
        assertSame(b, collected.get(1));
        assertSame(c, collected.get(2));

        // toList() should return same elements in same order (not necessarily same list instance)
        List<ConfigNode> asList = node.toList();
        assertEquals(asList, collected);
    }

    @Test
    public void updateHasNext() {
        ConfigNode n1 = new SimpleArrayNode(List.of());
        
        SimpleArrayNode node = new SimpleArrayNode(List.of(n1));

        Iterator<ConfigNode> it = node.iterator();

        assertTrue(it.hasNext(), "hasNext() must be true for first element");
        assertSame(n1, it.next(), "next() must return the first element");
        // After consuming the only element, hasNext should be false
        assertFalse(it.hasNext(), "hasNext() must be false after consuming the only element");
    }

    @Test
    public void throwAfterEnd() {
        // Create a node with exactly one element but call next twice
        ConfigNode n1 = new SimpleArrayNode(List.of());

        SimpleArrayNode node = new SimpleArrayNode(List.of(n1));

        Iterator<ConfigNode> it = node.iterator();
        assertSame(n1, it.next());

        // The default iterator delegates to get(index) without wrapping exceptions.
        // Therefore calling next() again will cause get(1) -> List.get(1) to throw
        assertThrows(IndexOutOfBoundsException.class, it::next);
    }

    @Test
    public void correctIteration() {
        ConfigNode a = new SimpleArrayNode(List.of());
        ConfigNode b = new SimpleArrayNode(List.of());
        ConfigNode c = new SimpleArrayNode(List.of());

        CountingArrayNode node = new CountingArrayNode(List.of(a, b, c));

        Iterator<ConfigNode> it = node.iterator();

        // drive the iterator step-by-step to ensure size() and get() are used
        assertTrue(it.hasNext()); // should call size()
        assertSame(a, it.next()); // should call get(0)

        assertTrue(it.hasNext());
        assertSame(b, it.next()); // get(1)

        assertTrue(it.hasNext());
        assertSame(c, it.next()); // get(2)

        // now no more elements
        assertFalse(it.hasNext());

        // We expect multiple calls to size() (at least once per hasNext invocation)
        assertTrue(node.sizeCalls.get() >= 4, "size() should have been called multiple times by the iterator");

        // get() should have been called exactly three times with indices 0,1,2 in order
        assertEquals(3, node.getCalls.get());
        assertEquals(List.of(0, 1, 2), node.getIndices);
    }

    @Test
    public void returnArrayNodeType() {
        SimpleArrayNode node = new SimpleArrayNode(List.of());

        assertEquals(NodeType.ARRAY, node.type(), "any ArrayNode implementation should return NodeType.ARRAY from default type()");
    }

    @Test
    public void returnSelfFromToArray() {
        SimpleArrayNode node = new SimpleArrayNode(List.of());

        assertSame(node, node.toArray(), "Default toArray() should return the same instance");
    }

    @Test
    public void throwFromToObject() {
        SimpleArrayNode node = new SimpleArrayNode(List.of());

        assertThrowsExactly(InvalidNodeTypeException.class, node::toObject, "Default toObject() should throw InvalidNodeTypeException.");
    }

    @Test
    public void throwFromToValue() {
        SimpleArrayNode node = new SimpleArrayNode(List.of());

        assertThrowsExactly(InvalidNodeTypeException.class, node::toValue, "Default toValue() should throw InvalidNodeTypeException.");
    }
}
