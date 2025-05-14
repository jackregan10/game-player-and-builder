package oogasalad.model.engine.subComponent.behavior;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import oogasalad.model.engine.subComponent.behavior.action.RotationSetAction;
import oogasalad.model.engine.component.Transform;

public class RotationSetActionTest extends ActionsTest<RotationSetAction> {

    @Override
    public void customSetUp() {
        RotationSetAction action = new RotationSetAction();
        addAction(getBehavior1(), action);
        setAction(action);
    }

    @Override
    @Test
    public void onPerform_performAction_performAction() {
        double rotation = 10.0;
        assertEquals(0, getObj1().getComponent(Transform.class).getRotation());
        getAction().onPerform(rotation);
        assertEquals(rotation, getObj1().getComponent(Transform.class).getRotation());
    }
}
