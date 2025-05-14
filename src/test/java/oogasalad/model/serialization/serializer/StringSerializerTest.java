package oogasalad.model.serialization.serializer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.TextNode;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StringSerializerTest {

  private final StringSerializer serializer = new StringSerializer();

  @Test
  void serialize_NonNullString_ReturnsTextNode() {
    String input = "hello";
    JsonNode result = serializer.serialize(input);
    assertTrue(result instanceof TextNode);
    assertEquals("hello", result.asText());
  }

  @Test
  void deserialize_ValidTextNode_ReturnsString() {
    JsonNode node = new TextNode("world");
    String result = serializer.deserialize(node);
    assertEquals("world", result);
  }

  @Test
  void deserialize_NullNode_ReturnsEmptyString() {
    String result = serializer.deserialize(null);
    assertEquals("", result);
  }

  @Test
  void deserialize_ExplicitNullNode_ReturnsEmptyString() {
    String result = serializer.deserialize(NullNode.getInstance());
    assertEquals("", result);
  }
}
