package oogasalad.model.engine.subComponent.behavior;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import oogasalad.model.engine.subComponent.behavior.action.MoveSceneToStoreAction;

public class MoveSceneToStoreActionTest extends ActionsTest<MoveSceneToStoreAction> {
    @Override
    public void customSetUp() {
        MoveSceneToStoreAction action = new MoveSceneToStoreAction();
        addAction(getBehavior1(), action);
        setAction(action);
    }

    @Override
    @Test
    public void onPerform_performAction_performAction() {
        assertNotNull(getObj1().getScene().getObject(getObj1().getName()));
        getAction().onPerform(getObj1().getName());
        assertNull(getObj1().getScene().getObject(getObj1().getName()));
    }
    
}
