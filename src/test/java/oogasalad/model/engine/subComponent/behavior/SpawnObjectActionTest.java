package oogasalad.model.engine.subComponent.behavior;

import static org.junit.jupiter.api.Assertions.*;

import oogasalad.model.engine.architecture.GameScene;
import org.junit.jupiter.api.Test;
import oogasalad.model.engine.subComponent.behavior.action.SpawnObjectAction;
import oogasalad.model.engine.architecture.GameObject;
import oogasalad.model.engine.component.Transform;

public class SpawnObjectActionTest extends ActionsTest<SpawnObjectAction> {

    @Override
    public void customSetUp() {
        SpawnObjectAction action = new SpawnObjectAction();
        addAction(getBehavior1(), action);
        setAction(action);
    }

    @Override
    @Test
    public void onPerform_performAction_performAction() {
        String objectName = "testObject";
        GameObject newObject = new GameObject(objectName);
        newObject.addComponent(Transform.class);
        newObject.setName(objectName);
        GameScene prefabScene = new GameScene(GameScene.PREFAB_SCENE_NAME);
        prefabScene.registerObject(newObject);
        getGame().addScene(prefabScene);

        assertNull(getObj1().getScene().getObject(objectName));
        GameObject toSpawn = getObj1().getScene().getGame().getPrefabScene().getObject(objectName);
        getAction().onPerform(objectName);
        try {
            java.lang.reflect.Method method =
                    getScene1().getClass().getDeclaredMethod("runSubscribedEvents");
            method.setAccessible(true);
            method.invoke(getScene1());
        } catch (Exception e) {
            e.printStackTrace();
            fail("Failed to invoke runSubscribedEvents using reflection: " + e.getMessage());
        }
        GameObject spawnedObject = getObj1().getScene().getActiveObjects().stream()
                .filter(object -> object.getName().contains(objectName)).findFirst().orElse(null);
        assertNotNull(spawnedObject);
        Transform toSpawnTransform = toSpawn.getComponent(Transform.class);
        Transform spawnedTransform = spawnedObject.getComponent(Transform.class);
        Transform parentTransform = getObj1().getComponent(Transform.class);
        assertEquals(parentTransform.getX() + toSpawnTransform.getX(), spawnedTransform.getX());
        assertEquals(parentTransform.getY() + toSpawnTransform.getY(), spawnedTransform.getY());
    }

}
