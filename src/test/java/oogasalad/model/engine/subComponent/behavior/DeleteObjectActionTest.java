package oogasalad.model.engine.subComponent.behavior;

import static org.junit.jupiter.api.Assertions.*;

import oogasalad.model.engine.subComponent.behavior.action.DeleteObjectAction;
import org.junit.jupiter.api.Test;

import oogasalad.model.engine.architecture.GameObject;

public class DeleteObjectActionTest extends ActionsTest<DeleteObjectAction> {

    @Override
    public void customSetUp() {
        DeleteObjectAction action = new DeleteObjectAction();
        addAction(getBehavior1(), action);
        setAction(action);
    }

    @Override
    @Test
    public void onPerform_performAction_performAction() {
        String objectName = "testObject";
        GameObject newObject = new GameObject(objectName);
        newObject.setName(objectName);
        getObj1().getScene().registerObject(newObject);
        assertNotNull(getObj1().getScene().getObject(objectName));
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
        assertNull(getObj1().getScene().getObject(objectName));
    }

}
