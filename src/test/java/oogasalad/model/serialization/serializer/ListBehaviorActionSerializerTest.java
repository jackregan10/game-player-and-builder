package oogasalad.model.serialization.serializer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import java.util.List;

import oogasalad.model.engine.subComponent.behavior.BehaviorAction;
import oogasalad.model.engine.subComponent.behavior.action.SwapAction;
import oogasalad.model.engine.subComponent.behavior.action.VelocityXSetAction;

public class ListBehaviorActionSerializerTest {

    private final ListBehaviorActionSerializer serializer = new ListBehaviorActionSerializer();
    private List<BehaviorAction<?>> behaviorActions;

    @BeforeEach
    public void setup() {
        SwapAction action1 = new SwapAction();
        action1.setParameter("testParameter");
        VelocityXSetAction action2 = new VelocityXSetAction();
        action2.setParameter(5.0);
        behaviorActions = List.of(action1, action2);
    }

    @Test
    void serialize_ValidBehaviorActions_ReturnsArrayNode() {
        JsonNode result = serializer.serialize(behaviorActions);
        assertInstanceOf(ArrayNode.class, result);
        ArrayNode arrayNode = (ArrayNode) result;

        assertEquals(behaviorActions.size(), arrayNode.size());
        BehaviorActionSerializer behaviorActionSerializer = new BehaviorActionSerializer();
        for (int i = 0; i < behaviorActions.size(); i++) {
            assertTrue(compareBehaviorActions(behaviorActions.get(i),
                    behaviorActionSerializer.deserialize(arrayNode.get(i))));
        }
    }

    @Test
    void deserialize_ValidArrayNode_ReturnsBehaviorActions() {
        JsonNode result = serializer.serialize(behaviorActions);
        List<BehaviorAction<?>> deserializedActions = serializer.deserialize(result);

        assertTrue(compareBehaviorActionLists(deserializedActions, behaviorActions));
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
}
