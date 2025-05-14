package oogasalad.model.engine.subComponent.behavior.action;

import java.util.Set;
import oogasalad.model.config.GameConfig;
import oogasalad.model.engine.architecture.GameComponent;
import oogasalad.model.engine.architecture.GameObject;
import oogasalad.model.engine.architecture.GameScene;
import oogasalad.model.engine.subComponent.behavior.BehaviorAction;
import oogasalad.model.engine.component.Transform;

/**
 * SwapAction is a class that extends BehaviorAction and is used to swap the position of the parent
 * object with the object in the store.
 * 
 * @author Christian Bepler
 */

public class SwapAction extends BehaviorAction<String> {

    @Override
    protected String defaultParameter() {
        return "";
    }

    @Override
    protected void perform(String parameter) {
        var parent = getBehavior().getController().getParent();
        try {
            GameObject objectToSwap = parent.getScene().getStoreObject(parameter);
            Transform swapTransform = objectToSwap.getComponent(Transform.class);
            Transform parentTransform = parent.getComponent(Transform.class);
            GameScene scene = parent.getScene();
            swapTransform.setX(parentTransform.getX());
            swapTransform.setY(parentTransform.getY());
            scene.moveSceneToStore(parent.getName());
            scene.moveStoreToScene(parameter);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(GameConfig.getText("noObjectFound", parameter), e);
        }
    }

    @Override
    public Set<Class<? extends GameComponent>> requiredComponents() {
        return Set.of(
            Transform.class
        );
    }
    
}
