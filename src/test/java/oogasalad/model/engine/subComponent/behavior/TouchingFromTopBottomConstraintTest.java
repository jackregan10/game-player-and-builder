package oogasalad.model.engine.subComponent.behavior;

import static org.junit.jupiter.api.Assertions.*;

import oogasalad.model.engine.subComponent.behavior.constraint.TouchingFromTopBottomConstraint;
import org.junit.jupiter.api.Test;
import oogasalad.model.engine.component.Transform;

public class TouchingFromTopBottomConstraintTest extends ConstraintsTest<TouchingFromTopBottomConstraint> {

  private Transform transform1;
  private Transform transform2;

  @Override
  public void customSetUp() {
    TouchingFromTopBottomConstraint constraint = new TouchingFromTopBottomConstraint();
    addConstraint(getBehavior1(), constraint);
    setConstraint(constraint);
    transform1 = getObj1().getComponent(Transform.class);
    transform2 = getObj2().getComponent(Transform.class);
  }

  @Override
  @Test
  public void check_checkPositive_returnsTrue() {
    transform1.setX(100);
    transform1.setY(90);
    transform2.setX(100);
    transform2.setY(190);
    step();
    assertTrue(getConstraint().onCheck(getObj2().getTag()));

    transform1.setY(110);
    transform2.setY(10);
    step();
    assertTrue(getConstraint().onCheck(getObj2().getTag()));
  }

  @Override
  @Test
  public void check_checkNegative_returnsFalse() {
    // No horizontal contact
    transform1.setX(0);
    transform1.setY(0);
    transform2.setX(0);
    transform2.setY(1000);
    step();
    assertFalse(getConstraint().onCheck(getObj2().getTag()));

    // Overlap in X but no vertical overlap
    transform1.setX(0);
    transform1.setY(90);
    transform2.setX(100);
    transform2.setY(100);
    step();
    assertFalse(getConstraint().onCheck(getObj2().getTag()));
  }
}
