package oogasalad.model.serialization.serializer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.NullNode;
import oogasalad.model.config.GameConfig;

/**
 * Concrete implementation for serializer of Integers.
 *
 * @author Hsuan-Kai Liao
 */
final class IntegerSerializer extends Serializer<Integer> {

  @Override
  public JsonNode serialize(Integer obj) {
    return obj == null ? NullNode.getInstance() : new IntNode(obj);
  }

  @Override
  public Integer deserialize(JsonNode node) {
    if (node == null || node.isNull()) {
      return 0;
    }

    if (!node.isNumber()) {
      throw new IllegalArgumentException(GameConfig.getText("errorIllegalDeserializedInput", node));
    }

    return node.asInt();
  }
}
