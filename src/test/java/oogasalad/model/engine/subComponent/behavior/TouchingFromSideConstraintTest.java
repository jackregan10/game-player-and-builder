package oogasalad.model.engine.subComponent.behavior;

import static org.junit.jupiter.api.Assertions.*;

import oogasalad.model.engine.subComponent.behavior.constraint.TouchingFromSideConstraint;
import org.junit.jupiter.api.Test;
import oogasalad.model.engine.component.Transform;

public class TouchingFromSideConstraintTest extends ConstraintsTest<TouchingFromSideConstraint> {

  private Transform transform1;
  private Transform transform2;

  @Override
  public void customSetUp() {
    TouchingFromSideConstraint constraint = new TouchingFromSideConstraint();
    addConstraint(getBehavior1(), constraint);
    setConstraint(constraint);
    transform1 = getObj1().getComponent(Transform.class);
    transform2 = getObj2().getComponent(Transform.class);
  }

  @Override
  @Test
  public void check_checkPositive_returnsTrue() {
    // Right side touch: Obj1's right edge touches Obj2's left edge
    transform1.setX(90);
    transform1.setY(100);
    transform2.setX(190);
    transform2.setY(100);
    step();
    assertTrue(getConstraint().onCheck(getObj2().getTag()));

    // Left side touch: Obj1's left edge touches Obj2's right edge
    transform1.setX(110);
    transform2.setX(10);
    step();
    assertTrue(getConstraint().onCheck(getObj2().getTag()));
  }

  @Override
  @Test
  public void check_checkNegative_returnsFalse() {
    // No horizontal contact
    transform1.setX(0);
    transform1.setY(0);
    transform2.setX(1000);
    transform2.setY(0);
    step();
    assertFalse(getConstraint().onCheck(getObj2().getTag()));

    // Overlap in X but no vertical overlap
    transform1.setX(90);
    transform1.setY(0);
    transform2.setX(100);
    transform2.setY(100);
    step();
    assertFalse(getConstraint().onCheck(getObj2().getTag()));
  }
}
