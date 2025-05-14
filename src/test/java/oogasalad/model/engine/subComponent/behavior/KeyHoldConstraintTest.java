package oogasalad.model.engine.subComponent.behavior;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import oogasalad.model.engine.component.InputHandler;
import oogasalad.model.engine.subComponent.behavior.constraint.KeyHoldConstraint;
import oogasalad.model.engine.architecture.KeyCode;

public class KeyHoldConstraintTest extends ConstraintsTest<KeyHoldConstraint> {
    
    @Override
    public void customSetUp() {
        getObj1().addComponent(InputHandler.class);
        KeyHoldConstraint constraint = new KeyHoldConstraint();
        addConstraint(getBehavior1(), constraint);
        constraint.setParameter(KeyCode.A);
        setConstraint(constraint);
    }

    @Override @Test
    public void check_checkPositive_returnsTrue() {
        getGame().keyPressed(KeyCode.A.getValue());
        step();
        assertTrue(getConstraint().onCheck(KeyCode.A));
    }

    @Override @Test
    public void check_checkNegative_returnsFalse() {
        step();
        assertFalse(getConstraint().onCheck(KeyCode.D));
    }
}
