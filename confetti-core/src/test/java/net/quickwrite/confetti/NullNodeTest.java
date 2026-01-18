/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package net.quickwrite.confetti;

import net.quickwrite.confetti.path.NodePath;
import net.quickwrite.confetti.path.PathSegment;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class NullNodeTest {
    private static final class SimpleNullNode implements NullNode {
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
    public void returnValueNodeType() {
        ConfigNode node = new SimpleNullNode();

        assertEquals(NodeType.NULL, node.type(), "a NullNode should return the NodeType of null.");
    }
}
