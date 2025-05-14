package oogasalad.model.engine.subComponent.behavior;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import oogasalad.model.engine.architecture.GameScene;
import org.junit.jupiter.api.Test;

import oogasalad.model.engine.subComponent.behavior.action.SpawnObjectFromCameraActionX;
import oogasalad.model.engine.architecture.GameObject;
import oogasalad.model.engine.component.Transform;

/**
 * Needed help from Chat to debug an issue with the tests below
 */
public class SpawnObjectFromCameraActionXTest extends ActionsTest<SpawnObjectFromCameraActionX> {

    private static final int SPAWN_OFFSET = 1800;
    private static final double FIXED_GROUND_SPAWN = 440.0;

    @Override
    public void customSetUp() {
        SpawnObjectFromCameraActionX action = new SpawnObjectFromCameraActionX();
        addAction(getBehavior1(), action);
        setAction(action);

        // Mock subscribeEvent to immediately run events
        GameScene realScene = getObj1().getScene();
        GameScene spyScene = spy(realScene);

        doAnswer(invocation -> {
            Runnable event = invocation.getArgument(0);
            event.run(); // Immediately run the event instead of queuing
            return null;
        }).when(spyScene).subscribeEvent(any());

        // Update parent scenes to use the spy
        getObj1().setParentScene(spyScene);
        getBehavior1().getController().getParent().setParentScene(spyScene);
    }

    @Override
    @Test
    public void onPerform_performAction_performAction() {
        String objectName = "testObject";

        // Create and register prefab object
        GameObject newObject = new GameObject(objectName);
        Transform newTransform = newObject.addComponent(Transform.class);
        newObject.setName(objectName);

// Set its initial y-position to something known (e.g., 440)
        double prefabY = 440.0;
        newTransform.setY(prefabY);

        GameScene prefabScene = new GameScene(GameScene.PREFAB_SCENE_NAME);
        prefabScene.registerObject(newObject);
        getGame().addScene(prefabScene);

        // Confirm object doesn't exist yet in the active scene
        assertNull(getObj1().getScene().getObject(objectName));

        // Perform the spawn action
        getAction().onPerform(objectName);

        // Now find the spawned object
        GameObject spawnedObject = getObj1().getScene().getActiveObjects().stream()
            .filter(object -> object.getName().contains(objectName))
            .findFirst()
            .orElse(null);

        assertNotNull(spawnedObject, "Spawned object should not be null");

        // Validate position of spawned object
        Transform spawnedTransform = spawnedObject.getComponent(Transform.class);
        Transform cameraTransform = getObj1().getScene().getMainCamera().getParent().getComponent(Transform.class);

        assertEquals(cameraTransform.getX() + SPAWN_OFFSET, spawnedTransform.getX(), "Spawned X position should match camera X + offset");
        assertEquals(FIXED_GROUND_SPAWN, spawnedTransform.getY(), "Spawned Y position should match fixed ground spawn");
    }
}