package oogasalad.model.engine.subComponent.behavior.action;

import oogasalad.model.engine.architecture.GameObject;
import oogasalad.model.engine.component.Transform;

/**
 * The SpawnObjectFromCameraAction class is a behavior action that spawns a new prefabricated object in the scene.
 * It takes the name of the object inside the prefab scene to spawn as a parameter.
 * It spawns the object at the camera's position plus the object's transform.
 * 
 * @author Christian Bepler, Hsuan-Kai Liao, Justin Aronwald
 */

public class SpawnObjectFromCameraActionX extends SpawnObjectGeneralAction {
    private static final int SPAWN_OFFSET = 1800;

    @Override
    protected void perform(String parameter) {
        GameObject parent = getBehavior().getController().getParent();
        GameObject camera = parent.getScene().getMainCamera().getParent();
        GameObject templateObject = parent.getScene().getGame().getPrefabScene()
            .getObject(parameter);

        GameObject newObject = templateObject.clone();
        newObject.setName(parameter + "_" + System.currentTimeMillis());

        Transform newTransform = newObject.getComponent(Transform.class);
        Transform templateTransform = templateObject.getComponent(Transform.class);
        Transform cameraTransform = camera.getComponent(Transform.class);

        newTransform.setX(cameraTransform.getX() + SPAWN_OFFSET);
        newTransform.setY(templateTransform.getY());

        newObject.setParentScene(parent.getScene());
        parent.getScene().subscribeEvent(() -> parent.getScene().registerObject(newObject));
    }
}
