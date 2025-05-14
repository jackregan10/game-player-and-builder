package oogasalad.model.serialization.serializer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import java.util.ArrayList;
import java.util.List;
import oogasalad.model.engine.subComponent.behavior.Behavior;

/**
 * Concrete implementation for serializer of List<Behavior>.
 *
 * @author Hsuan-Kai Liao
 */
final class ListBehaviorSerializer extends Serializer<List<Behavior>> {

  @Override
  public JsonNode serialize(List<Behavior> obj) {
    ArrayNode arrayNode = new ArrayNode(null);
    for (Behavior behavior : obj) {
      arrayNode.add(getSerializer(Behavior.class).serialize(behavior));
    }
    return arrayNode;
  }

  @Override
  public List<Behavior> deserialize(JsonNode node) {
    List<Behavior> result = new ArrayList<>();
    for (JsonNode obj : node) {
      result.add((Behavior) getSerializer(Behavior.class).deserialize(obj));
    }
    return result;
  }
}
