package oogasalad.model.serialization.serializer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.DoubleNode;
import com.fasterxml.jackson.databind.node.NullNode;
import oogasalad.model.config.GameConfig;

/**
 * Concrete implementation for serializer of Doubles.
 *
 * @author Hsuan-Kai Liao
 */
final class DoubleSerializer extends Serializer<Double> {

  @Override
  public JsonNode serialize(Double obj) {
    return obj == null ? NullNode.getInstance() : new DoubleNode(obj);
  }

  @Override
  public Double deserialize(JsonNode node) {
    if (node == null || node.isNull()) {
      return 0.0;
    }

    if (!node.isNumber()) {
      throw new IllegalArgumentException(GameConfig.getText("errorIllegalDeserializedInput", node));
    }

    return node.asDouble();
  }
}
