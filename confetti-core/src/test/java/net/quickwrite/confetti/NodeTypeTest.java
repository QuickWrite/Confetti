/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package net.quickwrite.confetti;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class NodeTypeTest {
    @Test
    void correctContainer() {
        assertTrue(NodeType.OBJECT.isContainer(), "OBJECT should be a container type");
        assertTrue(NodeType.ARRAY.isContainer(), "ARRAY should be a container type");
    }

    @Test
    void correctNonContainer() {
        assertFalse(NodeType.VALUE.isContainer(), "VALUE should not be a container type");
        assertFalse(NodeType.NULL.isContainer(), "NULL should not be a container type");
    }
}
