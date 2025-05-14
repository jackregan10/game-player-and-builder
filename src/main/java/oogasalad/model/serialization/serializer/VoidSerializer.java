package oogasalad.model.serialization.serializer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.NullNode;

/**
 * Concrete implementation for serializer of Void.
 *
 * @author Hsuan-Kai Liao
 */
final class VoidSerializer extends Serializer<Void> {

  @Override
  public JsonNode serialize(Void obj) {
    return NullNode.getInstance();
  }

  @Override
  public Void deserialize(JsonNode node) {
    return null;
  }
}
