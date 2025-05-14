package oogasalad.model.serialization.serializer;

import com.fasterxml.jackson.databind.JsonNode;
import oogasalad.model.engine.subComponent.behavior.BehaviorConstraint;
import oogasalad.model.serialization.nodes.BehaviorConstraintNode;


/**
 * Concrete implementation for serializer of BehaviorConstraints.
 *
 * @author Hsuan-Kai Liao
 */
final class BehaviorConstraintSerializer extends Serializer<BehaviorConstraint<?>> {

  @Override
  protected JsonNode serialize(BehaviorConstraint obj) {
    return new BehaviorConstraintNode(obj);
  }

  @Override
  protected BehaviorConstraint deserialize(JsonNode node) throws SerializerException {
    return BehaviorConstraintNode.toBehaviorConstraint(node);
  }
}
