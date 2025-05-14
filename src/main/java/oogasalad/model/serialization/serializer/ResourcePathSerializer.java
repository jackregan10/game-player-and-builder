package oogasalad.model.serialization.serializer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.TextNode;
import oogasalad.model.resource.ResourceIO;
import oogasalad.model.resource.ResourcePath;

/**
 * A GUI component for editing a serialized ResourcePath field.
 *
 * @author Hsuan-Kai Liao
 */
public class ResourcePathSerializer extends Serializer<ResourcePath> {

  @Override
  public JsonNode serialize(ResourcePath obj) {
    if (obj == null) {
      return NullNode.getInstance();
    }

    String path = obj.getPath();

    if (ResourcePath.getPathType() == ResourcePath.PathType.PACKED) {
      String movedPath = ResourceIO.copyToResourcesFolder(path);
      return new TextNode(movedPath);
    } else {
      return new TextNode(path);
    }
  }


  @Override
  public ResourcePath deserialize(JsonNode node) {
    ResourcePath resourcePath = new ResourcePath();
    if (node == null || node.isNull()) {
      resourcePath.setPath("");
    } else {
      resourcePath.setPath(node.asText());
    }

    return resourcePath;
  }
}
