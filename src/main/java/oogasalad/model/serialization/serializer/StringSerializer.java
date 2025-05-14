package oogasalad.model.serialization.serializer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.TextNode;

/**
 * Concrete implementation for serializer of Strings.
 *
 * @author Hsuan-Kai Liao
 */
@SuppressWarnings("unused")
final class StringSerializer extends Serializer<String> {

  @Override
  public JsonNode serialize(String obj) {
    return obj == null ? NullNode.getInstance() : new TextNode(obj);
  }

  @Override
  public String deserialize(JsonNode node) {
    if (node == null || node.isNull()) {
      return "";
    }
    return node.asText();
  }
}
