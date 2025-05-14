package oogasalad.model.engine.subComponent.behavior.action;

import java.util.Set;
import oogasalad.model.engine.architecture.GameComponent;
import oogasalad.model.engine.subComponent.behavior.BehaviorAction;

/**
 * Action to move a store to the scene.
 * 
 * @author Christian Bepler
 */

public class MoveStoreToSceneAction extends BehaviorAction<String> {

    @Override
    protected String defaultParameter() {
        return "";
    }

    @Override
    protected void perform(String parameter) {
        var parent = getBehavior().getController().getParent();
        try {
            parent.getScene().moveStoreToScene(parameter);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("No object found with name: " + parameter, e);
        }
    }

    @Override
    public Set<Class<? extends GameComponent>> requiredComponents() {
        return Set.of();
    }
    
}
