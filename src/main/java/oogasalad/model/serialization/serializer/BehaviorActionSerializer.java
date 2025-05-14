package oogasalad.model.serialization.serializer;

import com.fasterxml.jackson.databind.JsonNode;
import oogasalad.model.engine.subComponent.behavior.BehaviorAction;
import oogasalad.model.serialization.nodes.BehaviorActionNode;

/**
 * Concrete implementation for serializer of BehaviorActions. Serializes and deserializes
 * BehaviorAction objects to and from JSON.
 * 
 * @param <T> the type of the BehaviorAction parameter
 */
final class BehaviorActionSerializer extends Serializer<BehaviorAction<?>> {

  @Override
  protected BehaviorActionNode serialize(BehaviorAction obj) {
    return new BehaviorActionNode(obj);
  }

  @Override
  protected BehaviorAction deserialize(JsonNode node) throws SerializerException {
    return BehaviorActionNode.toBehaviorAction(node);
  }
}
