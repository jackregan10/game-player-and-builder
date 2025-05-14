package oogasalad.model.serialization.serializer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import java.util.List;

import oogasalad.model.engine.subComponent.behavior.BehaviorConstraint;
import oogasalad.model.engine.subComponent.behavior.constraint.KeyHoldConstraint;
import oogasalad.model.engine.subComponent.behavior.constraint.KeyPressConstraint;
import oogasalad.model.engine.architecture.KeyCode;

public class ListBehaviorConstraintSerializerTest {

    private final ListBehaviorConstraintSerializer serializer =
            new ListBehaviorConstraintSerializer();
    private List<BehaviorConstraint<?>> behaviorConstraints;

    @BeforeEach
    public void setup() {
        KeyPressConstraint constraint1 = new KeyPressConstraint();
        constraint1.setParameter(KeyCode.A);
        KeyHoldConstraint constraint2 = new KeyHoldConstraint();
        constraint2.setParameter(KeyCode.B);
        behaviorConstraints = List.of(constraint1, constraint2);
    }

    @Test
    void serialize_ValidBehaviorConstraints_ReturnsArrayNode() {
        JsonNode result = serializer.serialize(behaviorConstraints);
        assertInstanceOf(ArrayNode.class, result);
        ArrayNode arrayNode = (ArrayNode) result;

        assertEquals(behaviorConstraints.size(), arrayNode.size());
        BehaviorConstraintSerializer behaviorConstraintSerializer =
                new BehaviorConstraintSerializer();
        for (int i = 0; i < behaviorConstraints.size(); i++) {
            assertTrue(compareBehaviorConstraints(behaviorConstraints.get(i),
                    behaviorConstraintSerializer.deserialize(arrayNode.get(i))));
        }
    }

    @Test
    void deserialize_ValidArrayNode_ReturnsBehaviorActions() {
        JsonNode result = serializer.serialize(behaviorConstraints);
        List<BehaviorConstraint<?>> deserializedActions = serializer.deserialize(result);

        assertTrue(compareBehaviorConstraintLists(deserializedActions, behaviorConstraints));
    }

    private boolean compareBehaviorConstraintLists(List<BehaviorConstraint<?>> a,
            List<BehaviorConstraint<?>> b) {
        if (a.size() != b.size()) {
            return false;
        }
        for (int i = 0; i < a.size(); i++) {
            if (!compareBehaviorConstraints(a.get(i), b.get(i))) {
                return false;
            }
        }
        return true;
    }

    private boolean compareBehaviorConstraints(BehaviorConstraint<?> a, BehaviorConstraint<?> b) {
        return a.getClass().getSimpleName().equals(b.getClass().getSimpleName())
                && a.getParameter().equals(b.getParameter());
    }
}
