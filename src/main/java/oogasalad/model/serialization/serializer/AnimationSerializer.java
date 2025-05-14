package oogasalad.model.serialization.serializer;

import com.fasterxml.jackson.databind.JsonNode;
import oogasalad.model.engine.subComponent.animation.Animation;
import oogasalad.model.serialization.nodes.AnimationNode;

/**
 * Concrete implementation for serializer of Animation.
 * Serializes and deserializes Animation objects to and from JSON.
 * 
 * @author Christian Bepler
 */
final class AnimationSerializer extends Serializer<Animation> {

  @Override
  protected AnimationNode serialize(Animation obj) {
    return new AnimationNode(obj);
  }

  @Override
  protected Animation deserialize(JsonNode node) throws SerializerException {
    return AnimationNode.toAnimation(node);
  }
}
