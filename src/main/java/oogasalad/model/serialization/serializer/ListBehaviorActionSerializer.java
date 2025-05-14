package oogasalad.model.serialization.serializer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import oogasalad.model.engine.subComponent.behavior.BehaviorAction;
import oogasalad.model.serialization.serializable.TypeRef;

/**
 * Concrete implementation for serializer of List<BehaviorAction<?>>.
 *
 * @author Hsuan-Kai Liao
 */
final class ListBehaviorActionSerializer extends Serializer<List<BehaviorAction<?>>>{

  private static final Type ACTION_TYPE = new TypeRef<BehaviorAction<?>>() {}.getType();
  private static final ObjectMapper mapper = new ObjectMapper();

  @Override
  protected JsonNode serialize(List<BehaviorAction<?>> obj) {
    ArrayNode arrayNode = mapper.createArrayNode();
    for (BehaviorAction<?> action : obj) {
      JsonNode node = getSerializer(ACTION_TYPE).serialize(action);
      arrayNode.add(node);
    }
    return arrayNode;
  }

  @Override
  protected List<BehaviorAction<?>> deserialize(JsonNode node) {
    List<BehaviorAction<?>> actions = new ArrayList<>();
    for (JsonNode obj : node) {
      actions.add((BehaviorAction<?>) getSerializer(ACTION_TYPE).deserialize(obj));
    }
    return actions;
  }

}
