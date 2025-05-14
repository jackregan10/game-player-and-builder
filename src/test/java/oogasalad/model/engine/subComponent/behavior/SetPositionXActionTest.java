package oogasalad.model.engine.subComponent.behavior;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import oogasalad.model.engine.subComponent.behavior.action.SetPositionXAction;
import oogasalad.model.engine.component.Transform;

public class SetPositionXActionTest extends ActionsTest<SetPositionXAction> {
    @Override
    public void customSetUp() {
        SetPositionXAction action = new SetPositionXAction();
        addAction(getBehavior1(), action);
        setAction(action);
    }

    @Override
    @Test
    public void onPerform_performAction_performAction() {
        getObj1().getComponent(Transform.class).setX(0.0);
        assertEquals(getObj1().getComponent(Transform.class).getX(), 0.0);
        getAction().onPerform(5.0);
        assertEquals(getObj1().getComponent(Transform.class).getX(), 5.0);
    }
}
