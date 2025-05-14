package oogasalad.model.engine.subComponent.behavior.constraint;

import java.util.Set;
import oogasalad.model.engine.architecture.GameComponent;
import oogasalad.model.engine.subComponent.behavior.BehaviorConstraint;
import oogasalad.model.engine.component.Collider;

/**
 * This class defines a constraint that checks if a GameObject's Collider component collides with
 * another GameObject that has a Collider component with a specified tag. It extends the
 * BehaviorConstraint class, using a String to specify the target tag.
 *
 * @author Logan Dracos
 */
public class CollidesWithConstraint extends BehaviorConstraint<String> {

  private Collider collider;

  @Override
  protected String defaultParameter() {
    return "";
  }

  @Override
  public Set<Class<? extends GameComponent>> requiredComponents() {
    return Set.of(
        Collider.class
    );
  }

  @Override
  protected void awake() {
    collider = getComponent(Collider.class);
  }

  @Override
  protected boolean check(String tag) {
    return collider.collidesWith(tag);
  }
}