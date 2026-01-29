/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package net.quickwrite.confetti.path.impl;

import net.quickwrite.confetti.path.NodePath;
import net.quickwrite.confetti.path.PathSegment;
import org.junit.jupiter.api.Test;

import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link SimpleNodePath} and {@link NodePath} behavior.
 * Tests use camelCase names and avoid underscores in messages and identifiers.
 */
public class SimpleNodePathTest {

    @Test
    public void emptyReturnsSharedInstance() {
        NodePath empty1 = SimpleNodePath.empty();
        NodePath empty2 = NodePath.empty();

        assertSame(empty1, empty2, "empty should return a shared canonical instance");
        assertTrue(empty1.isEmpty(), "empty path should be empty");
        assertEquals(0, empty1.segments().size(), "empty path should have zero segments");
    }

    @Test
    public void appendPathSegmentProducesNewPath() {
        NodePath base = SimpleNodePath.empty();
        NodePath withA = base.appendPathSegment(PathSegment.key("a"));
        NodePath withAB = withA.appendPathSegment(PathSegment.key("b"));

        assertTrue(base.isEmpty(), "base path must remain unchanged");
        assertEquals(1, withA.segments().size(), "path with one segment should have size 1");
        assertEquals("a", withA.segments().getFirst().key(), "first segment key should be a");
        assertEquals(2, withAB.segments().size(), "path with two segments should have size 2");
        assertEquals("b", withAB.segments().get(1).key(), "second segment key should be b");
    }

    @Test
    public void iteratorIteratesInOrder() {
        NodePath path = SimpleNodePath.empty()
                .appendPathSegment(PathSegment.key("x"))
                .appendPathSegment(PathSegment.index(0))
                .appendPathSegment(PathSegment.key("y"));

        Iterator<PathSegment> it = path.iterator();

        assertTrue(it.hasNext());
        assertEquals("x", it.next().key());
        assertTrue(it.hasNext());
        assertTrue(it.next().isIndex());
        assertTrue(it.hasNext());
        assertEquals("y", it.next().key());
        assertFalse(it.hasNext(), "iterator should be exhausted");
    }

    @Test
    public void segmentsListIsUnmodifiable() {
        NodePath path = SimpleNodePath.empty().appendPathSegment(PathSegment.key("immutable"));

        assertThrows(UnsupportedOperationException.class, () -> path.segments().add(PathSegment.key("x")),
                "segments list should be unmodifiable");
    }

    @Test
    public void equalsAndHashCodeAreValueBased() {
        NodePath p1 = SimpleNodePath.empty()
                .appendPathSegment(PathSegment.key("one"))
                .appendPathSegment(PathSegment.index(1));

        NodePath p2 = SimpleNodePath.empty()
                .appendPathSegment(PathSegment.key("one"))
                .appendPathSegment(PathSegment.index(1));

        assertEquals(p1, p2, "two paths with identical segments should be equal");
        assertEquals(p1.hashCode(), p2.hashCode(), "equal paths should have equal hash codes");
    }
}

