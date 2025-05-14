package oogasalad.model.engine.subComponent.behavior;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import oogasalad.model.engine.subComponent.behavior.action.SetCameraFocusAction;
import oogasalad.model.engine.component.Follower;
import oogasalad.model.engine.component.Transform;
import oogasalad.model.engine.architecture.GameObject;

public class SetCameraFocusActionTest extends ActionsTest<SetCameraFocusAction> {

    @Override
    public void customSetUp() {
        SetCameraFocusAction action = new SetCameraFocusAction();
        addAction(getBehavior1(), action);
        setAction(action);
    }

    @Override
    @Test
    public void onPerform_performAction_performAction() {
        getCamera().getComponent(Follower.class).setFollowObject(getObj1());
        assertEquals(getCamera().getComponent(Follower.class).getFollowObject(), getObj1());
        GameObject newObject = new GameObject("testObject");
        newObject.setName("testObject");
        newObject.addComponent(Transform.class);
        getObj1().getScene().registerObject(newObject);
        assertNotEquals(getCamera().getComponent(Follower.class).getFollowObject(), newObject);
        getAction().onPerform("testObject");
        assertEquals(getCamera().getComponent(Follower.class).getFollowObject(), newObject);
    }
    
}
