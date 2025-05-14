package oogasalad.model.engine.subComponent.behavior.constraint;

import java.util.Set;
import oogasalad.model.engine.architecture.GameComponent;
import oogasalad.model.engine.component.Transform;
import oogasalad.model.engine.subComponent.behavior.BehaviorConstraint;

/**
 * Checks whether an object has passed another object
 * 
 * @author Christian Bepler
 */

public abstract class PassConstraint extends BehaviorConstraint<String> {

    private boolean isPass = false;

    @Override
    protected String defaultParameter() {
        return "";
    }

    @Override
    public Set<Class<? extends GameComponent>> requiredComponents() {
        return Set.of(
            Transform.class
        );
    }

    protected boolean checkPass(Double myLocation, Double checkLocation) {
        boolean previous = isPass;
        isPass = myLocation > checkLocation;
        return isPass != previous;
    }
}
