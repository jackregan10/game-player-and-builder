package oogasalad.model.engine.subComponent.behavior;

import java.util.ArrayList;
import java.util.List;
import oogasalad.model.engine.component.Collider;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import oogasalad.model.engine.subComponent.behavior.constraint.CollidesWithConstraint;
import oogasalad.model.engine.component.Transform;

public class CollidesWithConstraintTest extends ConstraintsTest<CollidesWithConstraint> {

    private Transform transform1;
    private Transform transform2;

    @Override
    public void customSetUp() {
        CollidesWithConstraint constraint = new CollidesWithConstraint();
        addConstraint(getBehavior1(), constraint);
        setConstraint(constraint);

        Collider collider1 = getObj1().getComponent(Collider.class);
        collider1.getSerializedFields().getFirst().setValue(List.of(getObj2().getTag()));

        transform1 = getObj1().getComponent(Transform.class);
        transform2 = getObj2().getComponent(Transform.class);
    }

    @Override
    @Test
    public void check_checkPositive_returnsTrue() {
        transform1.setX(transform2.getX());
        transform1.setY(transform2.getY());
        step();
        assertTrue(getConstraint().onCheck(getObj2().getTag()));

        Collider collider1 = getObj1().getComponent(Collider.class);
        collider1.getSerializedFields().getFirst().setValue(new ArrayList<>());
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
    }
}
