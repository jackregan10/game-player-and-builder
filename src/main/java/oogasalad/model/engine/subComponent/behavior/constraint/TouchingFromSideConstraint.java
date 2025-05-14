package oogasalad.model.engine.subComponent.behavior.constraint;

import oogasalad.model.engine.component.Collider;
import oogasalad.model.engine.component.Collider.CollisionDirection;

/**
 * This class defines a constraint that checks if a GameObject's Collider component is touching
 * another GameObject's Collider component from either side, within a specified tolerance. It extends the
 * BehaviorConstraint class, using a String to specify the target tag.
 *
 * @author Logan Dracos
 */
public class TouchingFromSideConstraint extends TouchingFromDirectionConstraint{
  private Collider collider;

  @Override
  protected void awake() {
    collider = getComponent(Collider.class);
  }

  /**
   * Checks if touches from left or right side, and if so returns true
   *
   * @param tag - Object to collide with
   */
  @Override
  protected boolean isTouchingFromDirection(String tag) {
    return collider.touchingFromDirection(tag, CollisionDirection.LEFT, TOLERANCE) || collider.touchingFromDirection(tag, CollisionDirection.RIGHT, TOLERANCE);
  }
}
