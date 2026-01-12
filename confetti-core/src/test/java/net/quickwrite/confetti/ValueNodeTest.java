/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package net.quickwrite.confetti;

import net.quickwrite.confetti.exception.InvalidNodeTypeException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ValueNodeTest {
    private static final class SimpleValueNode implements ValueNode {
        @Override
        public String asString() {
            return "";
        }

        @Override
        public long asLong() {
            return 0;
        }

        @Override
        public double asDouble() {
            return 0;
        }

        @Override
        public boolean asBoolean() {
            return false;
        }

        @Override
        public Object value() {
            return null;
        }
    }

    @Test
    public void returnValueNodeType() {
        ValueNode node = new SimpleValueNode();

        assertEquals(NodeType.VALUE, node.type(), "any ValueNode implementation should return NodeType.VALUE from default type()");
    }

    @Test
    public void returnSelfFromToValue() {
        ValueNode node = new SimpleValueNode();

        assertSame(node, node.toValue(), "Default toValue() should return the same instance");
    }

    @Test
    public void throwFromToObject() {
        ValueNode node = new SimpleValueNode();

        assertThrowsExactly(InvalidNodeTypeException.class, node::toObject, "Default toObject() should throw InvalidNodeTypeException.");
    }

    @Test
    public void throwFromToValue() {
        ValueNode node = new SimpleValueNode();

        assertThrowsExactly(InvalidNodeTypeException.class, node::toArray, "Default toArray() should throw InvalidNodeTypeException.");
    }
}
