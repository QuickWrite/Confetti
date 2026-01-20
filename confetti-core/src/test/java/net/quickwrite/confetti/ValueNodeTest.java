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

import java.util.List;
import java.util.Optional;

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
    public void returnSelfFromToValue() {
        ConfigNode node = new SimpleValueNode();

        assertSame(node, node.toValue(), "Default toValue() should return the same instance");
    }

    @Test
    public void returnFalseIsObject() {
        ConfigNode node = new SimpleValueNode();

        assertFalse(node.isObject());
    }

    @Test
    public void throwFromToObject() {
        ConfigNode node = new SimpleValueNode();

        assertThrowsExactly(InvalidNodeTypeException.class, node::toObject, "Default toObject() should throw InvalidNodeTypeException.");
    }

    @Test
    public void returnFalseIsArray() {
        ConfigNode node = new SimpleValueNode();

        assertFalse(node.isArray());
    }

    @Test
    public void throwFromToArray() {
        ConfigNode node = new SimpleValueNode();

        assertThrowsExactly(InvalidNodeTypeException.class, node::toArray, "Default toArray() should throw InvalidNodeTypeException.");
    }

    @Test
    public void returnTrueIsValue() {
        ConfigNode node = new SimpleValueNode();

        assertTrue(node.isValue());
    }

    @Test
    public void returnValueNodeType() {
        ConfigNode node = new SimpleValueNode();

        assertEquals(NodeType.VALUE, node.type(), "any ValueNode implementation should return NodeType.VALUE from default type()");
    }

    @Test
    public void returnFalseIsNull() {
        ConfigNode node = new SimpleValueNode();

        assertFalse(node.isNull());
    }
}
