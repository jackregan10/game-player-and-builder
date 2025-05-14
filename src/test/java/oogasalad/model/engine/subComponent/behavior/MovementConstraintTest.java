package oogasalad.model.engine.subComponent.behavior;

import oogasalad.model.engine.component.Transform;
import oogasalad.model.engine.subComponent.behavior.constraint.MovementConstraint;
import org.junit.jupiter.api.Test;

public class MovementConstraintTest extends ConstraintsTest<MovementConstraint> {

  @Override
  public void customSetUp() {
    Transform transform = getObj1().getComponent(Transform.class);
    transform.setX(10);
    transform.setY(20);

    MovementConstraint constraint = new MovementConstraint();
    addConstraint(getBehavior1(), constraint);
    setConstraint(constraint);
  }

  @Override
  @Test
  public void check_checkPositive_returnsTrue() {
    getConstraint().onCheck("Object1"); // prime it
    getObj1().getComponent(Transform.class).setX(15); // change position
    assert(getConstraint().onCheck("Object1"));
  }

  @Override
  @Test
  public void check_checkNegative_returnsFalse() {
    getConstraint().onCheck("Object1"); // prime it
    assert(!getConstraint().onCheck("Object1")); // no movement
  }
}
