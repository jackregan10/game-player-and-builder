package oogasalad.model.engine.subComponent.behavior;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import oogasalad.model.engine.subComponent.behavior.action.SwapAction;
import oogasalad.model.engine.architecture.GameObject;
import oogasalad.model.engine.component.Transform;

public class SwapActionTest extends ActionsTest<SwapAction> {
    @Override
    public void customSetUp() {
        SwapAction action = new SwapAction();
        addAction(getBehavior1(), action);
        setAction(action);
    }

    @Override
    @Test
    public void onPerform_performAction_performAction() {
        GameObject obj2 = new GameObject("testObject");
        obj2.setName("testObject");
        obj2.addComponent(Transform.class);
        getObj1().getScene().storeObject(obj2);
        assertNotNull(getObj1().getScene().getObject(getObj1().getName()));
        assertNull(getObj1().getScene().getObject(obj2.getName()));
        assertNotNull(getObj1().getScene().getStoreObject(obj2.getName()));
        assertNull(getObj1().getScene().getStoreObject(getObj1().getName()));
        getAction().onPerform(obj2.getName());
        assertNotNull(getObj1().getScene().getObject(obj2.getName()));
        assertNotNull(getObj1().getScene().getStoreObject(getObj1().getName()));
    }
    
}
