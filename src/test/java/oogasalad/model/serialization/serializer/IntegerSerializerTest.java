package oogasalad.model.serialization.serializer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.NullNode;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

final class IntegerSerializerTest {

  private final IntegerSerializer serializer = new IntegerSerializer();

  @Test
  void serialize_ValidInteger_ReturnsIntNode() {
    Integer input = 100;
    JsonNode result = serializer.serialize(input);
    assertTrue(result instanceof IntNode);
    assertEquals(100, result.asInt());
  }

  @Test
  void deserialize_ValidIntNode_ReturnsIntegerValue() {
    IntNode node = new IntNode(25);
    Integer result = serializer.deserialize(node);
    assertEquals(25, result);
  }

  @Test
  void deserialize_NullNode_ReturnsZero() {
    Integer result = serializer.deserialize(null);
    assertEquals(0, result);
  }

  @Test
  void deserialize_ExplicitNullNode_ReturnsZero() {
    Integer result = serializer.deserialize(NullNode.getInstance());
    assertEquals(0, result);
  }
}
