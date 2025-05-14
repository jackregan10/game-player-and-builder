package oogasalad.model.engine.subComponent.behavior.action;

import java.util.Set;
import oogasalad.model.engine.architecture.GameComponent;
import oogasalad.model.engine.component.Transform;
import oogasalad.model.engine.subComponent.behavior.BehaviorAction;
import oogasalad.model.engine.component.Follower;


/**
 * The SetCameraFocusAction class is a behavior action that sets the camera focus to a specific object.
 * It takes the name of the object to focus on as a parameter.
 * 
 * @author Christian Bepler
 */

public class SetCameraFocusAction extends BehaviorAction<String> {

    @Override
    protected String defaultParameter() {
        return "";
    }

    @Override
    public Set<Class<? extends GameComponent>> requiredComponents() {
        return Set.of(Transform.class);
    }

    @Override
    protected void perform(String parameter) {
        var parent = getBehavior().getController().getParent();
        var objectToFocus = parent.getScene().getObject(parameter);
        if (objectToFocus != null) {
            try {
                parent.getScene().getMainCamera().getComponent(Follower.class).setFollowObject(objectToFocus);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("No Follower component found on the camera", e);
            }
        } else {
            throw new IllegalArgumentException("No object found with name: " + parameter);
        }
    }
    
}
