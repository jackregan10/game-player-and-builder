package oogasalad.model.serialization.nodes;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import oogasalad.model.engine.subComponent.behavior.BehaviorAction;
import oogasalad.model.engine.subComponent.behavior.action.SwapAction;

public class BehaviorActionNodeTest {
    @Test
    public void toBehaviorAction_ValidNode_ReturnsBehaviorAction() {
        SwapAction behaviorAction = new SwapAction();
        behaviorAction.setParameter("testParameter");
        BehaviorActionNode node = new BehaviorActionNode(behaviorAction);
        BehaviorAction<?> testBehaviorAction = BehaviorActionNode.toBehaviorAction(node);
        assertEquals(behaviorAction.getClass().getSimpleName(), testBehaviorAction.getClass().getSimpleName());
        assertEquals(behaviorAction.getParameter(), testBehaviorAction.getParameter());
    }
}
