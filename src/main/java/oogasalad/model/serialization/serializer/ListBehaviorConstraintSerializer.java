package oogasalad.model.serialization.serializer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import oogasalad.model.engine.subComponent.behavior.BehaviorConstraint;
import oogasalad.model.serialization.serializable.TypeRef;

/**
 * Concrete implementation for serializer of List<BehaviorConstraint<?>>.
 *
 * @author Hsuan-Kai Liao
 */
final class ListBehaviorConstraintSerializer extends Serializer<List<BehaviorConstraint<?>>>{

  private static final Type CONSTRAINT_TYPE = new TypeRef<BehaviorConstraint<?>>() {}.getType();
  private static final ObjectMapper mapper = new ObjectMapper();

  @Override
  protected JsonNode serialize(List<BehaviorConstraint<?>> obj) {
    ArrayNode arrayNode = mapper.createArrayNode();
    for (BehaviorConstraint<?> constraint : obj) {
      JsonNode node = getSerializer(CONSTRAINT_TYPE).serialize(constraint);
      arrayNode.add(node);
    }
    return arrayNode;
  }

  @Override
  protected List<BehaviorConstraint<?>> deserialize(JsonNode node) {
    if(node == null) return new ArrayList<>();
    List<BehaviorConstraint<?>> constraints = new ArrayList<>();
    for (JsonNode obj : node) {
      constraints.add((BehaviorConstraint<?>) getSerializer(CONSTRAINT_TYPE).deserialize(obj));
    }
    return constraints;
  }

}
