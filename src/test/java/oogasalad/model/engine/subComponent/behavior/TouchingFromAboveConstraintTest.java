package oogasalad.model.engine.subComponent.behavior;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import oogasalad.model.engine.component.Transform;
import oogasalad.model.engine.subComponent.behavior.constraint.TouchingFromAboveConstraint;

public class TouchingFromAboveConstraintTest extends ConstraintsTest<TouchingFromAboveConstraint> {

    private Transform transform1;
    private Transform transform2;

    @Override
    public void customSetUp() {
        TouchingFromAboveConstraint constraint = new TouchingFromAboveConstraint();
        addConstraint(getBehavior1(), constraint);
        setConstraint(constraint);
        transform1 = getObj1().getComponent(Transform.class);
        transform2 = getObj2().getComponent(Transform.class);
    }

    @Override
    @Test
    public void check_checkPositive_returnsTrue() {
        transform1.setX(transform2.getX());
        transform1.setY(transform2.getY() - 99);
        step();
        assertTrue(getConstraint().onCheck(getObj2().getTag()));
    }

    @Override
    @Test
    public void check_checkNegative_returnsFalse() {
        transform1.setX(0);
        transform1.setY(0);
        transform2.setX(1000);
        transform2.setY(1000);
        step();
        assertFalse(getConstraint().onCheck(getObj2().getTag()));

        transform2.setX(0);
        transform2.setY(50);
        step();
        assertFalse(getConstraint().onCheck(getObj2().getTag()));
    }

}
