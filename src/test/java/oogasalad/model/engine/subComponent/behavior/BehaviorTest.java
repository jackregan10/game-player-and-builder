package oogasalad.model.engine.subComponent.behavior;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import oogasalad.model.engine.subComponent.behavior.action.VelocityXSetAction;
import oogasalad.model.engine.architecture.KeyCode;
import oogasalad.model.engine.component.InputHandler;
import oogasalad.model.engine.component.PhysicsHandler;
import oogasalad.model.engine.subComponent.behavior.constraint.KeyPressConstraint;
import org.junit.jupiter.api.Test;

public class BehaviorTest extends BehaviorBaseTest {

  @Override
  public void customSetUp() {
    getObj1().addComponent(PhysicsHandler.class);
    getObj1().addComponent(InputHandler.class);
  }

  @Test
  public void execute_passesConstraints_executeBehavior() {
    VelocityXSetAction velocityXSetAction = new VelocityXSetAction();
    velocityXSetAction.setParameter(10.0);
    addAction(getBehavior1(), velocityXSetAction);

    KeyPressConstraint constraint = new KeyPressConstraint();
    constraint.setParameter(KeyCode.A);
    addConstraint(getBehavior1(), constraint);

    getGame().keyPressed(KeyCode.A.getValue());
    step();
    assertEquals(getObj1().getComponent(PhysicsHandler.class).getVelocityX(), 10.0);
  }

  @Test
  public void execute_doesNotPassConstraints_doesNotExecuteBehavior() {
    VelocityXSetAction velocityXSetAction = new VelocityXSetAction();
    velocityXSetAction.setParameter(10.0);
    addAction(getBehavior1(), velocityXSetAction);

    KeyPressConstraint constraint = new KeyPressConstraint();
    constraint.setParameter(KeyCode.A);
    addConstraint(getBehavior1(), constraint);

    getGame().keyPressed(KeyCode.D.getValue());
    step();
    assertNotEquals(getObj1().getComponent(PhysicsHandler.class).getVelocityX(), 10.0);
  }

  @Test
  public void addConstraint_addByClass_addsConstraint() {
    KeyPressConstraint constraint = new KeyPressConstraint();
    addConstraint(getBehavior1(), constraint);
    assertNotNull(constraint);
    assertEquals(getConstraints(getBehavior1()).size(), 1);
    assertTrue(getConstraints(getBehavior1()).contains(constraint));
  }

  @Test
  public void addConstraint_addByInstance_addsConstraint() {
    KeyPressConstraint constraint = new KeyPressConstraint();
    addConstraint(getBehavior1(), constraint);
    assertNotNull(constraint);
    assertEquals(getConstraints(getBehavior1()).size(), 1);
    assertTrue(getConstraints(getBehavior1()).contains(constraint));
  }

  @Test
  public void removeConstraint_removeByClass_removesConstraint() {
    KeyPressConstraint constraint = new KeyPressConstraint();
    addConstraint(getBehavior1(), constraint);
    assertNotNull(constraint);
    assertEquals(getConstraints(getBehavior1()).size(), 1);
    removeConstraint(getBehavior1(), constraint);
    assertEquals(getConstraints(getBehavior1()).size(), 0);
  }

  @Test
  public void addAction_addByClass_addsAction() {
    VelocityXSetAction action = new VelocityXSetAction();
    addAction(getBehavior1(), action);
    assertNotNull(action);
    assertEquals(getActions(getBehavior1()).size(), 1);
    assertTrue(getActions(getBehavior1()).contains(action));
  }

  @Test
  public void addAction_addByInstance_addsAction() {
    VelocityXSetAction action = new VelocityXSetAction();
    addAction(getBehavior1(), action);
    assertNotNull(action);
    assertEquals(getActions(getBehavior1()).size(), 1);
    assertTrue(getActions(getBehavior1()).contains(action));
  }

  @Test
  public void removeAction_removeByClass_removesAction() {
    VelocityXSetAction action = new VelocityXSetAction();
    addAction(getBehavior1(), action);
    assertNotNull(action);
    assertEquals(getActions(getBehavior1()).size(), 1);
    removeAction(getBehavior1(), action);
    assertEquals(getActions(getBehavior1()).size(), 0);
  }


  public void addAction(Behavior behavior, BehaviorAction<?> action) {
    getActions(behavior).add(action);
  }

  public void addConstraint(Behavior behavior, BehaviorConstraint<?> constraint) {
    getConstraints(behavior).add(constraint);
  }

  public List<BehaviorAction<?>> getActions(Behavior behavior) {
    return (List<BehaviorAction<?>>) behavior.getSerializedField("actions").getValue();
  }

  public List<BehaviorConstraint<?>> getConstraints(Behavior behavior) {
    return (List<BehaviorConstraint<?>>) behavior.getSerializedField("constraints").getValue();
  }

  public void removeAction(Behavior behavior, BehaviorAction<?> action) {
    getActions(behavior).remove(action);
  }

  public void removeConstraint(Behavior behavior, BehaviorConstraint<?> constraint) {
    getConstraints(behavior).remove(constraint);
  }

}
