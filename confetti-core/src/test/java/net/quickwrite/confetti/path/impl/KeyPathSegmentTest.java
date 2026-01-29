/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package net.quickwrite.confetti.path.impl;

import net.quickwrite.confetti.path.PathSegment;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class KeyPathSegmentTest {

    @Test
    public void isKeyReturnsTrue() {
        PathSegment seg = PathSegment.key("name");

        assertTrue(seg.isKey(), "isKey should return true for a key segment");
    }

    @Test
    public void isIndexReturnsFalse() {
        PathSegment seg = PathSegment.key("name");

        assertFalse(seg.isIndex(), "isIndex should return false for a key segment");
    }

    @Test
    public void keyReturnsProvidedValue() {
        PathSegment seg = PathSegment.key("config");

        assertEquals("config", seg.key(), "key should return the value provided to the factory");
    }

    @Test
    public void indexThrowsOnKeySegment() {
        PathSegment seg = PathSegment.key("config");

        assertThrowsExactly(IllegalStateException.class, seg::index, "index should throw IllegalStateException for a key segment");
    }

    @Test
    public void equalsAndHashCodeAreValueBased() {
        PathSegment a = PathSegment.key("alpha");
        PathSegment b = PathSegment.key("alpha");
        PathSegment c = PathSegment.key("beta");

        assertEquals(a, b, "segments with the same key should be equal");
        assertEquals(a.hashCode(), b.hashCode(), "equal segments should have equal hash codes");
        assertNotEquals(a, c, "segments with different keys should not be equal");
    }

    @Test
    public void toStringContainsKey() {
        PathSegment seg = PathSegment.key("service");

        String s = seg.toString();
        assertTrue(s.contains("service"), "toString should include the key for easier debugging");
    }
}

