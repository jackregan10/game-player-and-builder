package oogasalad.model.engine.subComponent.behavior;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import oogasalad.model.engine.subComponent.behavior.constraint.PassYConstraint;
import oogasalad.model.engine.component.Transform;

public class PassYConstraintsTest extends ConstraintsTest<PassYConstraint> {
    @Override
    public void customSetUp() {
        PassYConstraint constraint = new PassYConstraint();
        addConstraint(getBehavior1(), constraint);
        setConstraint(constraint);
    }

    @Override @Test
    public void check_checkPositive_returnsTrue() {
        getObj1().getComponent(Transform.class).setY(1.0);
        getObj2().getComponent(Transform.class).setY(0.0);
        assertTrue(getConstraint().onCheck(getObj2().getName()));
    }

    @Override @Test
    public void check_checkNegative_returnsFalse() {
        getObj1().getComponent(Transform.class).setY(0.0);
        getObj2().getComponent(Transform.class).setY(1.0);
        assertFalse(getConstraint().onCheck(getObj2().getName()));
    }
    
}
