package oogasalad.model.engine.subComponent.behavior.constraint;

import java.util.Set;
import oogasalad.model.engine.architecture.GameComponent;
import oogasalad.model.engine.component.Collider;
import oogasalad.model.engine.subComponent.behavior.BehaviorConstraint;

/**
 * Generalized abstract constraint to check if a Collider is touching another collider from a
 * specific direction, defined by subclasses.
 *
 * @author Logan Dracos
 */
public abstract class TouchingFromDirectionConstraint extends BehaviorConstraint<String> {

  protected final static double TOLERANCE = 5.0;

  @Override
  abstract protected void awake();

  @Override
  protected String defaultParameter() {
    return "";
  }

  @Override
  public Set<Class<? extends GameComponent>> requiredComponents() {
    return Set.of(Collider.class);
  }

  @Override
  protected boolean check(String tag) {
    return isTouchingFromDirection(tag);
  }

  /**
   * Implement this in subclasses to determine direction-specific contact logic.
   */
  protected abstract boolean isTouchingFromDirection(String tag);
}
