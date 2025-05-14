package oogasalad.model.serialization.serializer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.DoubleNode;
import com.fasterxml.jackson.databind.node.NullNode;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

final class DoubleSerializerTest {

  private final DoubleSerializer serializer = new DoubleSerializer();

  @Test
  void serialize_ValidDouble_ReturnsDoubleNode() {
    Double input = 42.5;
    JsonNode result = serializer.serialize(input);
    assertTrue(result instanceof DoubleNode);
    assertEquals(42.5, result.asDouble());
  }

  @Test
  void deserialize_ValidDoubleNode_ReturnsDoubleValue() {
    DoubleNode node = new DoubleNode(3.1415);
    Double result = serializer.deserialize(node);
    assertEquals(3.1415, result);
  }

  @Test
  void deserialize_NullNode_ReturnsZero() {
    Double result = serializer.deserialize(null);
    assertEquals(0.0, result);
  }

  @Test
  void deserialize_ExplicitNullNode_ReturnsZero() {
    Double result = serializer.deserialize(NullNode.getInstance());
    assertEquals(0.0, result);
  }
}
