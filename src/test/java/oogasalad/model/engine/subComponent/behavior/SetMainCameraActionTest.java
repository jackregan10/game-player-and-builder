package oogasalad.model.engine.subComponent.behavior;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import oogasalad.model.engine.subComponent.behavior.action.SetMainCameraAction;
import oogasalad.model.engine.architecture.GameObject;
import oogasalad.model.engine.component.Camera;
import oogasalad.model.engine.component.Transform;

public class SetMainCameraActionTest extends ActionsTest<SetMainCameraAction> {

    private Camera camera;
    private GameObject cameraObject;

    @Override
    @BeforeEach
    public void customSetUp() {
        SetMainCameraAction action = new SetMainCameraAction();
        addAction(getBehavior1(), action);
        setAction(action);

        cameraObject = new GameObject("CameraObject2");
        cameraObject.setName("CameraObject2");
        camera = new Camera();
        cameraObject.addComponent(camera);
        cameraObject.addComponent(Transform.class);
        getScene1().registerObject(cameraObject);
    }

    @Override
    @Test
    public void onPerform_performAction_performAction() {
        assertNotEquals(camera.getParent().getName(),
                getScene1().getMainCamera().getParent().getName());
        getAction().onPerform("CameraObject2");
        assertEquals(camera.getParent().getName(),
                getScene1().getMainCamera().getParent().getName());
    }

}
