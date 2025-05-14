package oogasalad.model.engine.subComponent.behavior;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import oogasalad.model.engine.architecture.KeyCode;
import oogasalad.model.engine.component.InputHandler;
import oogasalad.model.engine.subComponent.behavior.constraint.KeyPressConstraint;

public class KeyPressConstraintTest extends ConstraintsTest<KeyPressConstraint> {
    @Override
    public void customSetUp() {
        getObj1().addComponent(InputHandler.class);
        KeyPressConstraint constraint = new KeyPressConstraint();
        addConstraint(getBehavior1(), constraint);
        constraint.setParameter(KeyCode.A);
        setConstraint(constraint);
    }

    @Override
    @Test
    public void check_checkPositive_returnsTrue() {
        getGame().keyPressed(KeyCode.A.getValue());
        step();
        assertTrue(getConstraint().onCheck(KeyCode.A));
    }

    @Override
    @Test
    public void check_checkNegative_returnsFalse() {
        step();
        assertFalse(getConstraint().onCheck(KeyCode.D));
    }
}
