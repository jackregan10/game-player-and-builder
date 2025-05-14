package oogasalad.model.serialization.serializer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import java.util.ArrayList;
import java.util.List;

import oogasalad.model.engine.subComponent.animation.Animation;

/**
 * Concrete implementation for serializer of List<Animation>.
 * Serializes and deserializes lists of Animation objects to and from JSON.
 * 
 * @author Christian Bepler
 */
final class ListAnimationSerializer extends Serializer<List<Animation>> {

  @Override
  protected JsonNode serialize(List<Animation> obj) {
    ArrayNode arrayNode = new ArrayNode(null);
    for (Animation animation : obj) {
      arrayNode.add(getSerializer(Animation.class).serialize(animation));
    }
    return arrayNode;
  }

  @Override
  protected List<Animation> deserialize(JsonNode node) throws SerializerException {
    List<Animation> result = new ArrayList<>();
    for (JsonNode obj : node) {
      result.add((Animation) getSerializer(Animation.class).deserialize(obj));
    }
    return result;
  }
}
