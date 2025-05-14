package oogasalad.model.engine.subComponent.behavior.constraint;

import java.util.Set;

import oogasalad.model.engine.architecture.GameComponent;
import oogasalad.model.engine.subComponent.behavior.BehaviorConstraint;

/**
 * OutOfViewConstraint is a class that extends BehaviorConstraint and is used to check if the
 * parent object is out of view.
 * 
 * @author Christian Bepler
 */

public class OutOfViewConstraint extends BehaviorConstraint<Boolean> {

    @Override
    protected Boolean defaultParameter() {
        return false;
    }

    @Override
    public Set<Class<? extends GameComponent>> requiredComponents() {
        return Set.of();
    }

    @Override
    protected boolean check(Boolean parameter) {
    return !getBehavior().getController().getParent().getScene().getAllObjectsInView()
        .contains(getBehavior().getController().getParent());
    }
}
