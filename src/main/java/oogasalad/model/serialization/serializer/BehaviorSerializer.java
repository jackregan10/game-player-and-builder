package oogasalad.model.serialization.serializer;

import com.fasterxml.jackson.databind.JsonNode;
import oogasalad.model.engine.subComponent.behavior.Behavior;
import oogasalad.model.serialization.nodes.BehaviorNode;


/**
 * Concrete implementation for serializer of Behaviors.
 *
 * @author Hsuan-Kai Liao
 */
final class BehaviorSerializer extends Serializer<Behavior> {

  @Override
  protected JsonNode serialize(Behavior obj) {
    return new BehaviorNode(obj);
  }

  @Override
  protected Behavior deserialize(JsonNode node) {
    return BehaviorNode.toBehavior(node);
  }
}
