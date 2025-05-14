package oogasalad.model.engine.subComponent.behavior.constraint;

import java.util.Set;
import oogasalad.model.engine.architecture.GameComponent;
import oogasalad.model.engine.subComponent.behavior.BehaviorConstraint;
import oogasalad.model.engine.component.Transform;

/**
 * Constraint for an action which takes a name of an object as a parameter, and checks
 * whether that object has moved
 *
 * @author Logan Dracos
 */
public class MovementConstraint extends BehaviorConstraint<String> {
  private double lastX;
  private double lastY;

  /**
   * This method is called to get the default parameter.
   *
   * @return the default parameter
   */
  @Override
  protected String defaultParameter() {
    return "";
  }

  @Override
  public Set<Class<? extends GameComponent>> requiredComponents() {
    return Set.of();
  }

  /**
   * Check if the constraint is met.
   *
   * @param parameter the parameter to check against
   * @return true if the constraint is met, false otherwise
   */
  @Override
  protected boolean check(String parameter) {
    Transform transform = getBehavior().getController().getParent().getScene().getObject(parameter)
        .getComponent(Transform.class);
    double curX = transform.getX();
    double curY = transform.getY();

    if (curX != lastX || curY != lastY) {
      lastX = curX;
      lastY = curY;
      return true;
    }

    return false;
  }
}
