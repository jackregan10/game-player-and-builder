package oogasalad.model.engine.subComponent.behavior.constraint;

import java.util.Set;
import oogasalad.model.engine.architecture.GameComponent;
import oogasalad.model.engine.subComponent.behavior.BehaviorConstraint;
import oogasalad.model.engine.architecture.KeyCode;
import oogasalad.model.engine.component.InputHandler;

/**
 * KeyReleaseConstraint is a class that extends BehaviorConstraint and is used to check if a key is
 * ever released.
 *
 * @author Hsuan-Kai Liao
 */
public class KeyReleaseConstraint extends BehaviorConstraint<KeyCode> {

  private InputHandler inputHandler;

  @Override
  public Set<Class<? extends GameComponent>> requiredComponents() {
    return Set.of(InputHandler.class);
  }

  @Override
  protected KeyCode defaultParameter() {
    return KeyCode.NONE;
  }

  @Override
  protected void awake() {
    inputHandler = getComponent(InputHandler.class);
  }

  @Override
  public boolean check(KeyCode parameter) {
    return inputHandler != null && inputHandler.isKeyReleased(parameter);
  }
}
