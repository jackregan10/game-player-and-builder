package oogasalad.model.serialization.nodes;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.lang.reflect.InvocationTargetException;
import oogasalad.model.config.GameConfig;
import oogasalad.model.engine.subComponent.behavior.BehaviorConstraint;
import oogasalad.model.serialization.serializable.SerializedField;
import oogasalad.model.serialization.serializer.Serializer;

/**
 * Represents a JSON node for a BehaviorConstraint object. Provides methods to convert a
 * BehaviorConstraint object to a JSON representation.
 */
public class BehaviorConstraintNode extends ObjectNode {

    private static final ObjectMapper mapper = new ObjectMapper();
    private static final String NAME = "name";
    private static final String PARAMETER_NAME = "parameter";
    private static final String CONSTRAINT_PACKAGE =
            "oogasalad.model.engine.subComponent.behavior.constraint";

    /**
     * Constructs a BehaviorConstraintNode from a BehaviorConstraint object.
     *
     * @param behaviorConstraint the BehaviorConstraint object to represent as a JSON node
     */
    public BehaviorConstraintNode(BehaviorConstraint<?> behaviorConstraint) {
        super(mapper.getNodeFactory());
        this.put(NAME, behaviorConstraint.getClass().getSimpleName());
        SerializedField parameter = behaviorConstraint.getSerializedFields().getFirst();
        this.set(PARAMETER_NAME, Serializer.serialize(parameter));
    }

    /**
     * Converts a JsonNode back into a BehaviorConstraint object.
     *
     * @param node the JsonNode representing a BehaviorConstraint
     * @return the deserialized BehaviorConstraint object
     * @throws IllegalArgumentException if the node cannot be converted
     */
    public static BehaviorConstraint<?> toBehaviorConstraint(JsonNode node) {
        try {
            String name = node.get(NAME).asText();
            String className = CONSTRAINT_PACKAGE + "." + name;
            BehaviorConstraint<?> behaviorConstraint = (BehaviorConstraint<?>) Class
                    .forName(className).getDeclaredConstructor().newInstance();
            SerializedField parameter = behaviorConstraint.getSerializedFields().getFirst();
            Serializer.deserialize(parameter, node.get(PARAMETER_NAME));
            return behaviorConstraint;
        } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException |
                 IllegalAccessException | InstantiationException e) {
            throw new IllegalArgumentException(GameConfig.getText("invalidJson"), e);
        }
    }
}
