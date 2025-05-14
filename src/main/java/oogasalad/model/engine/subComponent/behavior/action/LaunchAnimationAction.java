package oogasalad.model.engine.subComponent.behavior.action;

import java.util.Set;
import oogasalad.model.engine.architecture.GameComponent;
import oogasalad.model.engine.subComponent.behavior.BehaviorAction;
import oogasalad.model.engine.component.AnimationController;

/**
 * The LaunchAnimationAction class is a behavior action that launches an animation on the parent
 * object. It takes the name of the animation to launch as a parameter.
 * 
 * @author Christian Bepler
 */

public class LaunchAnimationAction extends BehaviorAction<String> {

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
    protected void perform(String parameter) {
        var parent = getBehavior().getController().getParent();
        var animationController = parent.getComponent(AnimationController.class);
        if (animationController == null) {
            throw new IllegalArgumentException("No AnimationController found on the parent object.");
        }
        try {
            animationController.setCurrentAnimation(parameter);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("No animation found with name: " + parameter, e);
        }
    }
}
