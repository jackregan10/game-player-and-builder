package oogasalad.model.serialization.serializer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.NullNode;

import com.fasterxml.jackson.databind.node.TextNode;
import java.util.ArrayList;
import java.util.List;

/**
 * Concrete implementation for serializer of List<String>.
 *
 * @author Hsuan-Kai Liao
 */
final class ListStringSerializer extends Serializer<List<String>> {

  @Override
  public JsonNode serialize(List<String> obj) {
    if (obj == null) {
      return NullNode.getInstance();
    }

    ArrayNode arrayNode = new ArrayNode(null);
    for (String str : obj) {
      arrayNode.add(str == null ? NullNode.getInstance() : new TextNode(str));
    }
    return arrayNode;
  }

  @Override
  public List<String> deserialize(JsonNode node) {
    if (node == null || node.isNull() || !node.isArray()) {
      return new ArrayList<>();
    }

    List<String> result = new ArrayList<>();
    for (JsonNode n : node) {
      if (n.isTextual()) {
        result.add(n.asText());
      } else {
        result.add("");
      }
    }
    return result;
  }
}
