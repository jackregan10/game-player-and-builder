package oogasalad.model.engine.subComponent.behavior.constraint;

import static org.junit.jupiter.api.Assertions.*;

import oogasalad.model.engine.component.Transform;
import oogasalad.model.engine.subComponent.behavior.ConstraintsTest;
import org.junit.jupiter.api.Test;


public class IsPassedXConstraintsTest extends ConstraintsTest<IsPassedXConstraint> {

  @Override
  public void customSetUp() {
    IsPassedXConstraint constraint = new IsPassedXConstraint();
    addConstraint(getBehavior1(), constraint);
    setConstraint(constraint);
  }

  @Override
  @Test
  public void check_checkPositive_returnsTrue() {
    getObj1().getComponent(Transform.class).setX(1.0);
    getObj2().getComponent(Transform.class).setX(2.0);
    assertTrue(getConstraint().onCheck(getObj2().getName()));
  }

  @Override
  @Test
  public void check_checkNegative_returnsFalse() {
    getObj1().getComponent(Transform.class).setX(2.0);
    getObj2().getComponent(Transform.class).setX(1.0);
    assertFalse(getConstraint().onCheck(getObj2().getName()));
  }
}