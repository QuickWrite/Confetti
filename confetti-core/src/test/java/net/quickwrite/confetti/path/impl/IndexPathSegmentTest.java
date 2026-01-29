/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package net.quickwrite.confetti.path.impl;

import net.quickwrite.confetti.path.PathSegment;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class IndexPathSegmentTest {

    @Test
    public void isKeyReturnsFalse() {
        PathSegment seg = PathSegment.index(3);

        assertFalse(seg.isKey(), "isKey should return false for an index segment");
    }

    @Test
    public void isIndexReturnsTrue() {
        PathSegment seg = PathSegment.index(3);

        assertTrue(seg.isIndex(), "isIndex should return true for an index segment");
    }

    @Test
    public void indexReturnsProvidedValue() {
        PathSegment seg = PathSegment.index(7);

        assertEquals(7, seg.index(), "index should return the value provided to the factory");
    }

    @Test
    public void keyThrowsOnIndexSegment() {
        PathSegment seg = PathSegment.index(5);

        assertThrowsExactly(IllegalStateException.class, seg::key, "key should throw IllegalStateException for an index segment");
    }

    @Test
    public void equalsAndHashCodeAreValueBased() {
        PathSegment a = PathSegment.index(2);
        PathSegment b = PathSegment.index(2);
        PathSegment c = PathSegment.index(4);

        assertEquals(a, b, "segments with the same index should be equal");
        assertEquals(a.hashCode(), b.hashCode(), "equal segments should have equal hash codes");
        assertNotEquals(a, c, "segments with different indices should not be equal");
    }

    @Test
    public void toStringContainsIndex() {
        PathSegment seg = PathSegment.index(9);

        String s = seg.toString();
        assertTrue(s.contains("9"), "toString should include the index for easier debugging");
    }
}

