package oogasalad.model.serialization.serializer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import java.util.List;

import oogasalad.model.engine.subComponent.behavior.BehaviorConstraint;
import oogasalad.model.engine.subComponent.behavior.action.SwapAction;
import oogasalad.model.engine.subComponent.behavior.action.VelocityXSetAction;
import oogasalad.model.engine.subComponent.behavior.Behavior;
import oogasalad.model.engine.subComponent.behavior.BehaviorAction;
import oogasalad.model.engine.subComponent.behavior.constraint.KeyHoldConstraint;
import oogasalad.model.engine.subComponent.behavior.constraint.KeyPressConstraint;
import oogasalad.model.engine.architecture.KeyCode;

public class ListBehaviorSerializerTest {

    private final ListBehaviorSerializer serializer = new ListBehaviorSerializer();
    private List<Behavior> behaviors;

    @BeforeEach
    public void setup() {
        KeyPressConstraint constraint1 = new KeyPressConstraint();
        constraint1.setParameter(KeyCode.A);
        KeyHoldConstraint constraint2 = new KeyHoldConstraint();
        constraint2.setParameter(KeyCode.B);
        SwapAction action1 = new SwapAction();
        action1.setParameter("testParameter");
        VelocityXSetAction action2 = new VelocityXSetAction();
        action2.setParameter(5.0);
        Behavior behavior1 = new Behavior("Behavior1");
        Behavior behavior2 = new Behavior("Behavior2");
        addAction(behavior2, action2);
        addAction(behavior1, action1);
        addConstraint(behavior1, constraint1);
        addConstraint(behavior2, constraint2);
        behaviors = List.of(behavior1, behavior2);
    }

    @Test
    void serialize_ValidBehaviorConstraints_ReturnsArrayNode() {
        JsonNode result = serializer.serialize(behaviors);
        assertInstanceOf(ArrayNode.class, result);
        ArrayNode arrayNode = (ArrayNode) result;

        assertEquals(behaviors.size(), arrayNode.size());
        BehaviorSerializer behaviorSerializer = new BehaviorSerializer();
        for (int i = 0; i < behaviors.size(); i++) {
            assertTrue(compareBehaviors(behaviors.get(i),
                    behaviorSerializer.deserialize(arrayNode.get(i))));
        }
    }

    @Test
    void deserialize_ValidArrayNode_ReturnsBehaviorActions() {
        JsonNode result = serializer.serialize(behaviors);
        List<Behavior> deserializedActions = serializer.deserialize(result);

        assertTrue(compareBehaviorsList(deserializedActions, behaviors));
    }

    private boolean compareBehaviorsList(List<Behavior> a, List<Behavior> b) {
        if (a.size() != b.size()) {
            return false;
        }
        for (int i = 0; i < a.size(); i++) {
            if (!compareBehaviors(a.get(i), b.get(i))) {
                return false;
            }
        }
        return true;
    }

    private boolean compareBehaviors(Behavior a, Behavior b) {
        return a.getName().equals(b.getName())
                && compareBehaviorActionLists(getActions(a), getActions(b))
                && compareBehaviorConstraintLists(getConstraints(a), getConstraints(b));
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

    private boolean compareBehaviorActionLists(List<BehaviorAction<?>> a,
            List<BehaviorAction<?>> b) {
        if (a.size() != b.size()) {
            return false;
        }
        for (int i = 0; i < a.size(); i++) {
            if (!compareBehaviorActions(a.get(i), b.get(i))) {
                return false;
            }
        }
        return true;
    }

    private boolean compareBehaviorActions(BehaviorAction<?> a, BehaviorAction<?> b) {
        return a.getClass().getSimpleName().equals(b.getClass().getSimpleName())
                && a.getParameter().equals(b.getParameter());
    }

    public List<BehaviorAction<?>> getActions(Behavior behavior) {
        return (List<BehaviorAction<?>>) behavior.getSerializedField("actions").getValue();
    }

    public List<BehaviorConstraint<?>> getConstraints(Behavior behavior) {
        return (List<BehaviorConstraint<?>>) behavior.getSerializedField("constraints").getValue();
    }

    public void addAction(Behavior behavior, BehaviorAction<?> action) {
        getActions(behavior).add(action);
        action.setBehavior(behavior);
    }

    public void addConstraint(Behavior behavior, BehaviorConstraint<?> constraint) {
        getConstraints(behavior).add(constraint);
        constraint.setBehavior(behavior);
    }

}
