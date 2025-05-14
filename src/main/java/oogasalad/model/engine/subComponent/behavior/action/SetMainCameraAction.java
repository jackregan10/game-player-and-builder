package oogasalad.model.engine.subComponent.behavior.action;

import java.util.Set;

import oogasalad.model.engine.architecture.GameComponent;
import oogasalad.model.engine.subComponent.behavior.BehaviorAction;
import oogasalad.model.engine.component.Camera;

/**
 * The SetMainCameraAction class is a behavior action that sets the main camera of the scene to a
 * specified camera object. It takes the name of the camera object as a parameter.
 * 
 * @author Christian Bepler
 */

public class SetMainCameraAction extends BehaviorAction<String> {

    @Override
    protected String defaultParameter() {
        return "";
    }

    @Override
    public Set<Class<? extends GameComponent>> requiredComponents() {
        return Set.of();
    }

    @Override
    protected void perform(String parameter) {
        var parent = getBehavior().getController().getParent();
        var scene = parent.getScene();
        var cameraObject = scene.getObject(parameter);
        try {
            scene.setMainCamera(cameraObject.getComponent(Camera.class));
        } catch (NullPointerException e) {
            throw new IllegalArgumentException("No camera found with name: " + parameter, e);
        }
    }
}
