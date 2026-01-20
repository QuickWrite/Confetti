/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package net.quickwrite.confetti.path;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Iterator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class NodePathTest {
    private static final class SimpleNodePath implements NodePath {
        private final List<PathSegment> pathSegments;

        public SimpleNodePath(final List<PathSegment> pathSegments) {
            this.pathSegments = pathSegments;
        }

        @Override
        public List<PathSegment> segments() {
            return pathSegments;
        }

        @Override
        public Iterator<PathSegment> iterator() {
            return pathSegments.iterator();
        }
    }

    private static final class SimplePathSegment implements PathSegment {
        @Override
        public boolean isKey() {
            return false;
        }

        @Override
        public boolean isIndex() {
            return false;
        }

        @Override
        public String key() {
            return "";
        }

        @Override
        public int index() {
            return 0;
        }
    }

    @Test
    public void returnTrueIfPathEmpty() {
        NodePath nodePath = new SimpleNodePath(List.of());

        assertTrue(nodePath.isEmpty(), "isEmpty() should return true if there is no path");
    }

    @Test
    public void returnFalseIfPathSome() {
        NodePath nodePath = new SimpleNodePath(List.of(new SimplePathSegment()));

        assertFalse(nodePath.isEmpty(), "isEmpty() should return false if there is a path");
    }
}
