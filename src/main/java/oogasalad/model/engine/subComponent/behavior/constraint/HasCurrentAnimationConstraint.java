package oogasalad.model.engine.subComponent.behavior.constraint;

import java.util.Set;
import oogasalad.model.engine.architecture.GameComponent;
import oogasalad.model.engine.subComponent.behavior.BehaviorConstraint;
import oogasalad.model.engine.component.AnimationController;

/**
 * hasCurrentAnimation is a class that extends BehaviorConstraint and is used to check if the
 * current animation of an entity matches a given parameter.
 */
public class HasCurrentAnimationConstraint extends BehaviorConstraint<String> {

    private AnimationController animationController;

    @Override
    protected String defaultParameter() {
        return "";
    }

    @Override
    public Set<Class<? extends GameComponent>> requiredComponents() {
        return Set.of(
            AnimationController.class
        );
    }

    @Override
    protected void awake() {
        animationController = getComponent(AnimationController.class);
    }

    @Override
    public boolean check(String parameter) {
        return animationController.getCurrentAnimation().equals(parameter);
    }
}
