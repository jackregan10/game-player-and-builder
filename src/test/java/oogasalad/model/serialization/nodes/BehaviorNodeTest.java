package oogasalad.model.serialization.nodes;

import java.util.List;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

import oogasalad.model.engine.subComponent.behavior.Behavior;
import oogasalad.model.engine.subComponent.behavior.BehaviorAction;
import oogasalad.model.engine.subComponent.behavior.BehaviorConstraint;
import oogasalad.model.engine.subComponent.behavior.action.SwapAction;
import oogasalad.model.engine.subComponent.behavior.constraint.KeyPressConstraint;
import oogasalad.model.engine.architecture.KeyCode;

public class BehaviorNodeTest {
    @Test
    public void toBehavior_ValidNode_ReturnsBehavior() {
        Behavior behavior = new Behavior("TestBehavior");
        SwapAction swapAction = new SwapAction();
        swapAction.setParameter("TestParameter");
        KeyPressConstraint keyPressConstraint = new KeyPressConstraint();
        keyPressConstraint.setParameter(KeyCode.A);
        addAction(behavior, swapAction);
        addConstraint(behavior, keyPressConstraint);
        BehaviorNode node = new BehaviorNode(behavior);
        Behavior testBehavior = BehaviorNode.toBehavior(node);
        assertEquals(behavior.getClass().getSimpleName(), testBehavior.getClass().getSimpleName());
        assertEquals(behavior.getName(), testBehavior.getName());
        List<BehaviorAction<?>> actions = getActions(testBehavior);
        assertEquals(1, actions.size());
        assertEquals(swapAction.getClass().getSimpleName(),
                actions.get(0).getClass().getSimpleName());
        assertEquals(swapAction.getParameter(), actions.get(0).getParameter());
        List<BehaviorConstraint<?>> constraints = getConstraints(testBehavior);
        assertEquals(1, constraints.size());
        assertEquals(keyPressConstraint.getClass().getSimpleName(),
                constraints.get(0).getClass().getSimpleName());
        assertEquals(keyPressConstraint.getParameter(), constraints.get(0).getParameter());
    }


    public void addAction(Behavior behavior, BehaviorAction<?> action) {
        getActions(behavior).add(action);
        action.setBehavior(behavior);
    }

    public void addConstraint(Behavior behavior, BehaviorConstraint<?> constraint) {
        getConstraints(behavior).add(constraint);
        constraint.setBehavior(behavior);
    }

    public List<BehaviorAction<?>> getActions(Behavior behavior) {
        return (List<BehaviorAction<?>>) behavior.getSerializedField("actions").getValue();
    }

    public List<BehaviorConstraint<?>> getConstraints(Behavior behavior) {
        return (List<BehaviorConstraint<?>>) behavior.getSerializedField("constraints").getValue();
    }


}
