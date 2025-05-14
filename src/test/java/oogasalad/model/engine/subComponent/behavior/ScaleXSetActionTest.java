package oogasalad.model.engine.subComponent.behavior;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import oogasalad.model.engine.subComponent.behavior.action.ScaleXSetAction;
import oogasalad.model.engine.component.Transform;

public class ScaleXSetActionTest extends ActionsTest<ScaleXSetAction> {

    @Override
    public void customSetUp() {
        ScaleXSetAction action = new ScaleXSetAction();
        addAction(getBehavior1(), action);
        setAction(action);
    }

    @Override
    @Test
    public void onPerform_performAction_performAction() {
        double scaleX = 10.0;
        assertEquals(100.0, getObj1().getComponent(Transform.class).getScaleX());
        getAction().onPerform(scaleX);
        assertEquals(scaleX, getObj1().getComponent(Transform.class).getScaleX());
    }
}
