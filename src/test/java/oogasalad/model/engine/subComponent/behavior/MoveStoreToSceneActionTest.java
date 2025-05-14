package oogasalad.model.engine.subComponent.behavior;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import oogasalad.model.engine.subComponent.behavior.action.MoveStoreToSceneAction;

public class MoveStoreToSceneActionTest extends ActionsTest<MoveStoreToSceneAction> {
    @Override
    public void customSetUp() {
        MoveStoreToSceneAction action = new MoveStoreToSceneAction();
        addAction(getBehavior1(), action);
        setAction(action);
    }

    @Override
    @Test
    public void onPerform_performAction_performAction() {
        getObj1().getScene().moveSceneToStore(getObj1().getName());
        assertNull(getObj1().getScene().getObject(getObj1().getName()));
        getAction().onPerform(getObj1().getName());
        assertNotNull(getObj1().getScene().getObject(getObj1().getName()));
    }
    
}
