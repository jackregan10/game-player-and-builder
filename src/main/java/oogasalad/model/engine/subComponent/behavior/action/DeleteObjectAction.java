package oogasalad.model.engine.subComponent.behavior.action;

import java.util.Set;
import oogasalad.model.engine.architecture.GameComponent;
import oogasalad.model.engine.architecture.GameObject;
import oogasalad.model.engine.subComponent.behavior.BehaviorAction;
import oogasalad.model.engine.architecture.GameScene;

/**
 * The SpawnObjectAction class is a behavior action that spawns a new object in the scene. It takes
 * the name of the object to spawn as a parameter.
 * 
 * @author Christian Bepler
 */

public class DeleteObjectAction extends BehaviorAction<String> {

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
        GameObject parent = getBehavior().getController().getParent();
        GameScene scene = parent.getScene();
        var objectToDelete = parent.getScene().getObject(parameter);
        var storedObjectToDelete = parent.getScene().getStoreObject(parameter);
        if (objectToDelete != null) {
            scene.subscribeEvent(() -> scene.unregisterObject(objectToDelete));
        } else if (storedObjectToDelete != null) {
            scene.subscribeEvent(() -> scene.removeStoreObject(storedObjectToDelete.getId()));
        } else {
            throw new IllegalArgumentException("No object found with name: " + parameter);
        }
    }

}
