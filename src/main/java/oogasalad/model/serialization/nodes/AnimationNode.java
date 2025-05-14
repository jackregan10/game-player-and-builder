package oogasalad.model.serialization.nodes;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import oogasalad.model.config.GameConfig;
import oogasalad.model.engine.subComponent.animation.Animation;
import oogasalad.model.serialization.serializable.SerializedField;
import oogasalad.model.serialization.serializer.Serializer;

/**
 * Represents a JSON node for an Animation object.
 * Provides methods to convert an Animation object to a JSON representation.
 * 
 * This class extends JsonNode to integrate with Jackson's JSON processing.
 * 
 * @author Christian Bepler
 */
public class AnimationNode extends ObjectNode {

  private static final ObjectMapper mapper = new ObjectMapper();
  private static final String NAME = "name";
  private static final String LENGTH_NAME = "animationLength";
  private static final String LOOP_NAME = "loop";
  private static final String FILE_PATHS_NAME = "filePaths";

  /**
   * Constructs an AnimationNode from an Animation object.
   * 
   * @param animation the Animation object to represent as a JSON node
   */
  public AnimationNode(Animation animation) {
    super(mapper.getNodeFactory());

    SerializedField name = animation.getSerializedField(NAME);
    SerializedField length = animation.getSerializedField(LENGTH_NAME);
    SerializedField loop = animation.getSerializedField(LOOP_NAME);
    SerializedField filePaths = animation.getSerializedField(FILE_PATHS_NAME);

    this.set(NAME, Serializer.serialize(name));
    this.set(LENGTH_NAME, Serializer.serialize(length));
    this.set(LOOP_NAME, Serializer.serialize(loop));
    this.set(FILE_PATHS_NAME, Serializer.serialize(filePaths));
  }

  /**
   * Converts a JsonNode back into an Animation object.
   * 
   * @param node the JsonNode representing an Animation
   * @return the deserialized Animation object
   * @throws IllegalArgumentException if the node cannot be converted
   */
  public static Animation toAnimation(JsonNode node) {
    try {
      Animation animation = new Animation();
      SerializedField name = animation.getSerializedField(NAME);
      SerializedField length = animation.getSerializedField(LENGTH_NAME);
      SerializedField loop = animation.getSerializedField(LOOP_NAME);
      SerializedField filePaths = animation.getSerializedField(FILE_PATHS_NAME);

      Serializer.deserialize(name, node.get(NAME));
      Serializer.deserialize(length, node.get(LENGTH_NAME));
      Serializer.deserialize(loop, node.get(LOOP_NAME));
      Serializer.deserialize(filePaths, node.get(FILE_PATHS_NAME));

      return animation;
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException(GameConfig.getText("invalidJson"), e);
    }


  }
}
