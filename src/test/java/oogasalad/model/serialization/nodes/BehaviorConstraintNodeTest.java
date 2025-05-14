package oogasalad.model.serialization.nodes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import oogasalad.model.engine.subComponent.behavior.BehaviorConstraint;
import oogasalad.model.engine.subComponent.behavior.constraint.KeyPressConstraint;
import oogasalad.model.engine.architecture.KeyCode;



public class BehaviorConstraintNodeTest {
    @Test
    public void toBehaviorConstraint_ValidNode_ReturnsBehaviorConstraint() {
        KeyPressConstraint behaviorConstraint = new KeyPressConstraint();
        behaviorConstraint.setParameter(KeyCode.A);
        BehaviorConstraintNode node = new BehaviorConstraintNode(behaviorConstraint);
        BehaviorConstraint<?> testBehaviorConstraint =
                BehaviorConstraintNode.toBehaviorConstraint(node);
        assertEquals(behaviorConstraint.getClass().getSimpleName(),
                testBehaviorConstraint.getClass().getSimpleName());
        assertEquals(behaviorConstraint.getParameter(), testBehaviorConstraint.getParameter());
    }

}
