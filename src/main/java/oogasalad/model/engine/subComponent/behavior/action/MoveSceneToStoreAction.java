package oogasalad.model.engine.subComponent.behavior.action;

import java.util.Set;
import oogasalad.model.engine.architecture.GameComponent;
import oogasalad.model.engine.subComponent.behavior.BehaviorAction;

/**
 * Action to move a scene to the store.
 * 
 * @author Christian Bepler
 */

public class MoveSceneToStoreAction extends BehaviorAction<String> {

    @Override
    public Set<Class<? extends GameComponent>> requiredComponents() {
        return Set.of();
    }

    @Override
    protected void perform(String parameter) {
        var parent = getBehavior().getController().getParent();
        try {
            parent.getScene().moveSceneToStore(parameter);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("No object found with name: " + parameter, e);
        }
    }

    @Override
    protected String defaultParameter() {
        return "";
    }
    
}
