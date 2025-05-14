package oogasalad.model.serialization.serializer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import java.util.ArrayList;
import java.util.List;
import oogasalad.model.resource.ResourcePath;

/**
 * A GUI component for editing a serialized List<ResourcePath> field.
 *
 * @author Hsuan-Kai Liao
 */
public class ListResourcePathSerializer extends Serializer<List<ResourcePath>> {

  @Override
  protected JsonNode serialize(List<ResourcePath> obj) {
    ArrayNode arrayNode = new ArrayNode(null);
    for (ResourcePath path : obj) {
      arrayNode.add(getSerializer(ResourcePath.class).serialize(path));
    }
    return arrayNode;
  }

  @Override
  protected List<ResourcePath> deserialize(JsonNode node) {
    List<ResourcePath> result = new ArrayList<>();
    for (JsonNode obj : node) {
      result.add((ResourcePath) getSerializer(ResourcePath.class).deserialize(obj));
    }
    return result;
  }
}
