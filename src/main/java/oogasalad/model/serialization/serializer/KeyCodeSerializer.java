package oogasalad.model.serialization.serializer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.TextNode;
import oogasalad.model.engine.architecture.KeyCode;

/**
 * Concrete implementation for serializer of KeyCode.
 *
 * @author Hsuan-Kai Liao
 */
final class KeyCodeSerializer extends Serializer<KeyCode> {

  @Override
  public JsonNode serialize(KeyCode obj) {
    return obj == null ? NullNode.getInstance() : new TextNode(obj.name());
  }

  @Override
  public KeyCode deserialize(JsonNode node) {
    if (node == null || node.isNull()) {
      return KeyCode.NONE;
    }
    return KeyCode.valueOf(node.asText());
  }
}
