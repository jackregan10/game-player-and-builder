package oogasalad.model.serialization.serializer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.BooleanNode;
import com.fasterxml.jackson.databind.node.NullNode;
import oogasalad.model.config.GameConfig;

/**
 * Concrete implementation for serializer of Booleans.
 *
 * @author Hsuan-Kai Liao
 */
final class BooleanSerializer extends Serializer<Boolean> {

  @Override
  public JsonNode serialize(Boolean obj) {
    return obj == null ? NullNode.getInstance() : BooleanNode.valueOf(obj);
  }

  @Override
  public Boolean deserialize(JsonNode node) {
    if (node == null || node.isNull()) {
      return false;
    }

    if (!node.isBoolean()) {
      throw new IllegalArgumentException(GameConfig.getText("errorIllegalDeserializedInput", node));
    }

    return node.asBoolean();
  }
}
