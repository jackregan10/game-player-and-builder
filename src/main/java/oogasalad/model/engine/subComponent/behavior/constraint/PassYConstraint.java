package oogasalad.model.engine.subComponent.behavior.constraint;

import oogasalad.model.engine.architecture.GameObject;
import oogasalad.model.engine.component.Transform;

/**
 * PassYConstraint is a class that extends PassConstraint and is used to check if the parent object
 * has passed another object in the y direction.
 * 
 * @author Christian Bepler
 */

public class PassYConstraint extends PassConstraint {

  @Override
  protected boolean check(String parameter) {
    GameObject parent = getBehavior().getController().getParent();
    GameObject objectToCheck = parent.getScene().getObject(parameter);
    return checkPass(getComponent(Transform.class).getY(),
        objectToCheck.getComponent(Transform.class).getY());
  }
    
}
