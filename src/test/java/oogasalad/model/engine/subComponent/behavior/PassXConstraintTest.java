package oogasalad.model.engine.subComponent.behavior;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import oogasalad.model.engine.subComponent.behavior.constraint.PassXConstraint;
import oogasalad.model.engine.component.Transform;

public class PassXConstraintTest extends ConstraintsTest<PassXConstraint> {
    @Override
    public void customSetUp() {
        PassXConstraint constraint = new PassXConstraint();
        addConstraint(getBehavior1(), constraint);
        setConstraint(constraint);
    }

    @Override @Test
    public void check_checkPositive_returnsTrue() {
        getObj1().getComponent(Transform.class).setX(1.0);
        getObj2().getComponent(Transform.class).setX(0.0);
        assertTrue(getConstraint().onCheck(getObj2().getName()));
    }

    @Override @Test
    public void check_checkNegative_returnsFalse() {
        getObj1().getComponent(Transform.class).setX(0.0);
        getObj2().getComponent(Transform.class).setX(1.0);
        assertFalse(getConstraint().onCheck(getObj2().getName()));
    }
}
