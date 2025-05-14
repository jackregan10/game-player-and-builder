package oogasalad.model.engine.subComponent.behavior;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import oogasalad.model.engine.subComponent.behavior.constraint.OutOfViewConstraint;
import oogasalad.model.engine.component.Transform;

public class OutOfViewConstraintTest extends ConstraintsTest<OutOfViewConstraint> {

    @Override
    public void customSetUp() {
        OutOfViewConstraint constraint = new OutOfViewConstraint();
        addConstraint(getBehavior1(), constraint);
        setConstraint(constraint);
    }

    @Override @Test
    public void check_checkPositive_returnsTrue() {
        assertFalse(getConstraint().onCheck(true));
    }

    @Override @Test
    public void check_checkNegative_returnsFalse() {
        Transform transform = getObj1().getComponent(Transform.class);
        transform.setX(-1000);
        transform.setY(-1000);
        assertTrue(getConstraint().onCheck(false));
    }
}
