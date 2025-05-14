package oogasalad.model.serialization.serializer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.fasterxml.jackson.databind.node.NullNode;
import oogasalad.model.engine.architecture.KeyCode;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

final class KeyCodeSerializerTest {

  private final KeyCodeSerializer serializer = new KeyCodeSerializer();

  @Test
  void serialize_ValidKeyCode_ReturnsTextNode() {
    KeyCode input = KeyCode.W;
    JsonNode result = serializer.serialize(input);
    assertTrue(result instanceof TextNode);
    assertEquals("W", result.asText());
  }

  @Test
  void deserialize_ValidTextNode_ReturnsKeyCode() {
    TextNode node = new TextNode("A");
    KeyCode result = serializer.deserialize(node);
    assertEquals(KeyCode.A, result);
  }

  @Test
  void deserialize_NullNode_ReturnsNone() {
    KeyCode result = serializer.deserialize(null);
    assertEquals(KeyCode.NONE, result);
  }

  @Test
  void deserialize_ExplicitNullNode_ReturnsNone() {
    KeyCode result = serializer.deserialize(NullNode.getInstance());
    assertEquals(KeyCode.NONE, result);
  }
}
