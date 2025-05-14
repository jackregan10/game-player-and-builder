package oogasalad.model.engine.subComponent.behavior.constraint;

import oogasalad.model.engine.architecture.GameObject;
import oogasalad.model.engine.component.Transform;

/**
 * IsPassedXConstraint checks if the parent GameObject
 * has been passed by another object in the X direction
 *
 * @author Justin Aronwald
 */
public class IsPassedXConstraint extends PassConstraint {
  private boolean alreadyPassed = false;

  @Override
  protected boolean check(String targetName) {
    if (alreadyPassed) {
      return false;
    }

    GameObject parent = getBehavior().getController().getParent();
    GameObject objectToCheck = parent.getScene().getObject(targetName);

    if (objectToCheck == null) {
      throw new IllegalArgumentException("No object found with name: " + targetName);
    }

    double triggerX = parent.getComponent(Transform.class).getX();
    double playerX = objectToCheck.getComponent(Transform.class).getX();

    boolean passed = checkPass(playerX, triggerX);
    if (passed) {
      alreadyPassed = true;
    }
    return passed;
  }
}