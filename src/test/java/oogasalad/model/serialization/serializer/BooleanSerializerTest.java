package oogasalad.model.serialization.serializer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.BooleanNode;
import com.fasterxml.jackson.databind.node.NullNode;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

final class BooleanSerializerTest {

  private final BooleanSerializer serializer = new BooleanSerializer();

  @Test
  void serialize_ValidBoolean_ReturnsBooleanNode() {
    Boolean input = true;
    JsonNode result = serializer.serialize(input);
    assertTrue(result instanceof BooleanNode);
    assertTrue(result.asBoolean());
  }

  @Test
  void deserialize_ValidBooleanNode_ReturnsBooleanValue() {
    BooleanNode node = BooleanNode.TRUE;
    Boolean result = serializer.deserialize(node);
    assertTrue(result);
  }

  @Test
  void deserialize_NullNode_ReturnsFalse() {
    Boolean result = serializer.deserialize(null);
    assertFalse(result);
  }

  @Test
  void deserialize_ExplicitNullNode_ReturnsFalse() {
    Boolean result = serializer.deserialize(NullNode.getInstance());
    assertFalse(result);
  }
}
