package oogasalad.model.serialization.nodes;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.JsonNode;
import oogasalad.model.config.GameConfig;
import oogasalad.model.engine.subComponent.behavior.Behavior;
import oogasalad.model.serialization.serializable.SerializedField;
import oogasalad.model.serialization.serializer.Serializer;
import oogasalad.model.serialization.serializer.SerializerException;

/**
 * Class for behavior-related JSON nodes. Provides shared functionality for serialization and
 * deserialization of behaviors.
 */
public class BehaviorNode extends ObjectNode {

    private static final ObjectMapper mapper = new ObjectMapper();
    private static final String NAME = "name";
    private static final String ACTIONS_NAME = "actions";
    private static final String CONSTRAINTS_NAME = "constraints";

    /**
     * Constructs a BehaviorNode with the given behavior's class name and serialized parameter.
     *
     */
    public BehaviorNode(Behavior obj) {
        super(mapper.getNodeFactory());

        SerializedField name = obj.getSerializedField(NAME);
        SerializedField actions = obj.getSerializedField(ACTIONS_NAME);
        SerializedField constraints = obj.getSerializedField(CONSTRAINTS_NAME);

        this.set(NAME, Serializer.serialize(name));
        this.set(ACTIONS_NAME, Serializer.serialize(actions));
        this.set(CONSTRAINTS_NAME, Serializer.serialize(constraints));
    }

    /**
     * Converts a JsonNode back into a behavior object.
     *
     * @param node the JsonNode representing a behavior
     * @return the deserialized behavior object
     * @throws IllegalArgumentException if the node cannot be converted
     */
    public static Behavior toBehavior(JsonNode node) {
        try {
            String name = node.get(NAME).asText();
            Behavior behavior = new Behavior(name);
            SerializedField actions = behavior.getSerializedField(ACTIONS_NAME);
            SerializedField constraints = behavior.getSerializedField(CONSTRAINTS_NAME);

            Serializer.deserialize(actions, node.get(ACTIONS_NAME));
            Serializer.deserialize(constraints, node.get(CONSTRAINTS_NAME));

            return behavior;
        } catch (SerializerException e) {
            throw new IllegalArgumentException(GameConfig.getText("invalidJson"), e);
        }
    }
}
