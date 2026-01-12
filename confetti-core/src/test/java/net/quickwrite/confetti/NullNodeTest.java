/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package net.quickwrite.confetti;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class NullNodeTest {
    @Test
    public void returnValueNodeType() {
        ConfigNode node = new NullNode();

        assertEquals(NodeType.NULL, node.type(), "a NullNode should return the NodeType of null.");
    }
}
