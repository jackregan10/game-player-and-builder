package oogasalad.model.engine.subComponent.behavior.action;

import java.util.Set;
import oogasalad.model.engine.architecture.GameComponent;
import oogasalad.model.engine.architecture.GameObject;
import oogasalad.model.engine.subComponent.behavior.BehaviorAction;
import oogasalad.model.engine.component.Transform;

/**
 * General Spawn object superclass. Allows customizing from where the object is spawned.
 * 
 * @author Christian Bepler
 */

public abstract class SpawnObjectGeneralAction extends BehaviorAction<String> {


    @Override
    protected String defaultParameter() {
        return "";
    }


    @Override
    public Set<Class<? extends GameComponent>> requiredComponents() {
        return Set.of(Transform.class);
    }

    protected void spawn(GameObject spawnPoint, String parameter) {
        GameObject parent = getBehavior().getController().getParent();
        GameObject templateObject;
        try {
            templateObject = parent.getScene().getGame().getPrefabScene().getObject(parameter);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("No object found with name: " + parameter, e);
        }
        GameObject newObject = templateObject.clone();
        newObject.setName(parameter + "_" + System.currentTimeMillis());
        Transform transform = newObject.getComponent(Transform.class);
        Transform spawnTransform = spawnPoint.getComponent(Transform.class);
        transform.setX(spawnTransform.getX() + transform.getX());
        transform.setY(spawnTransform.getY() + transform.getY());
        newObject.setParentScene(parent.getScene());
        parent.getScene().subscribeEvent(() -> parent.getScene().registerObject(newObject));
    }

}
