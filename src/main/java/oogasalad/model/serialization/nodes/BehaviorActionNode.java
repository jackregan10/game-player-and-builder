package oogasalad.model.serialization.nodes;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.lang.reflect.InvocationTargetException;
import oogasalad.model.config.GameConfig;
import oogasalad.model.engine.subComponent.behavior.BehaviorAction;
import oogasalad.model.serialization.serializable.SerializedField;
import oogasalad.model.serialization.serializer.Serializer;

/**
 * Represents a JSON node for a BehaviorAction object.
 * Provides methods to convert a BehaviorAction object to a JSON representation.
 * 
 * This class extends ObjectNode to integrate with Jackson's JSON processing.
 * 
 */
public class BehaviorActionNode extends ObjectNode {

  private static final ObjectMapper mapper = new ObjectMapper();
  private static final String NAME = "name";
  private static final String PARAMETER_NAME = "parameter";

  /**
   * Constructs a BehaviorActionNode from a BehaviorAction object.
   * 
   * @param behaviorAction the BehaviorAction object to represent as a JSON node
   */
  public BehaviorActionNode(BehaviorAction<?> behaviorAction) {
    super(mapper.getNodeFactory());
    this.put(NAME, behaviorAction.getClass().getSimpleName());
    SerializedField parameter = behaviorAction.getSerializedFields().getFirst();
    this.set(PARAMETER_NAME, Serializer.serialize(parameter));
  }

  /**
   * Converts a JsonNode back into a BehaviorAction object.
   * 
   * @param node the JsonNode representing a BehaviorAction
   * @return the deserialized BehaviorAction object
   * @throws IllegalArgumentException if the node cannot be converted
   */
  public static BehaviorAction<?> toBehaviorAction(JsonNode node) {
    try {
        String name = node.get(NAME).asText();
        String className = "oogasalad.model.engine.subComponent.behavior.action." + name;
        BehaviorAction<?> behaviorAction = (BehaviorAction<?>) Class.forName(className)
            .getDeclaredConstructor().newInstance();
        SerializedField parameter = behaviorAction.getSerializedFields().getFirst();
        Serializer.deserialize(parameter, node.get(PARAMETER_NAME));
        return behaviorAction;
    } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException |
             IllegalAccessException | InstantiationException e) {
      throw new IllegalArgumentException(GameConfig.getText("invalidJson"), e);
    }
  }
}
