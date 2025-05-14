package oogasalad.model.engine.subComponent.behavior;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Field;
import oogasalad.model.engine.component.Transform;
import org.junit.jupiter.api.Test;
import oogasalad.model.engine.component.InputHandler;
import oogasalad.model.engine.subComponent.behavior.constraint.MouseClickConstraint;

public class MouseClickConstraintTest extends ConstraintsTest<MouseClickConstraint> {

  @Override
  public void customSetUp() {
    getObj1().addComponent(InputHandler.class);
    getObj1().getComponent(Transform.class).setX(200);
    getObj1().getComponent(Transform.class).setY(250);
    getObj1().getComponent(Transform.class).setScaleX(100);
    getObj1().getComponent(Transform.class).setScaleY(100);

    Field field = null;
    try {
      field = InputHandler.class.getDeclaredField("transform");
    } catch (NoSuchFieldException ignore) {
    }
    field.setAccessible(true);
    try {
      field.set(getObj1().getComponent(InputHandler.class), getObj1().getComponent(Transform.class));
    } catch (IllegalAccessException ignore) {
    }

    MouseClickConstraint constraint = new MouseClickConstraint();
    addConstraint(getBehavior1(), constraint);
    setConstraint(constraint);
  }

  @Override
  @Test
  public void check_checkPositive_returnsTrue() {
    getObj1().getComponent(InputHandler.class).registerMouseClick(250.0, 300.0);
    assertTrue(getConstraint().onCheck(null), "Expected mouse click to register within object's bounds");
  }

  @Override
  @Test
  public void check_checkNegative_returnsFalse() {
    step();
    assertFalse(getConstraint().onCheck(null));
  }
}
