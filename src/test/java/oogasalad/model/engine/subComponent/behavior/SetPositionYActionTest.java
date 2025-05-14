package oogasalad.model.engine.subComponent.behavior;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import oogasalad.model.engine.subComponent.behavior.action.SetPositionYAction;
import oogasalad.model.engine.component.Transform;

public class SetPositionYActionTest extends ActionsTest<SetPositionYAction> {
    @Override
    public void customSetUp() {
        SetPositionYAction action = new SetPositionYAction();
        addAction(getBehavior1(), action);
        setAction(action);
    }

    @Override
    @Test
    public void onPerform_performAction_performAction() {
        getObj1().getComponent(Transform.class).setY(0.0);
        assertEquals(getObj1().getComponent(Transform.class).getY(), 0.0);
        getAction().onPerform(5.0);
        assertEquals(getObj1().getComponent(Transform.class).getY(), 5.0);
    }
    
}
