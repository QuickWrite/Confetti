/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package net.quickwrite.confetti.path;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PathSegmentTest {
    @Test
    public void returnKeyPathSegmentForKey() {
        PathSegment segment = PathSegment.key("test");

        assertTrue(segment.isKey());
    }

    @Test
    public void returnCorrectKeyInPathSegment() {
        PathSegment segment = PathSegment.key("test");

        assertEquals("test", segment.key());
    }

    @Test
    public void returnIndexPathSegmentForIndex() {
        PathSegment segment = PathSegment.index(0);

        assertTrue(segment.isIndex());
    }

    @Test
    public void returnCorrectIndexInPathSegment() {
        PathSegment segment = PathSegment.index(0);

        assertEquals(0, segment.index());
    }
}
