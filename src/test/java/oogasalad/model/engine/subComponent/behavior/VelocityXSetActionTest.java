package oogasalad.model.engine.subComponent.behavior;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import oogasalad.model.engine.subComponent.behavior.action.VelocityXSetAction;
import oogasalad.model.engine.component.PhysicsHandler;

public class VelocityXSetActionTest extends ActionsTest<VelocityXSetAction> {

    @Override
    public void customSetUp() {
        getObj1().addComponent(PhysicsHandler.class);
        VelocityXSetAction action = new VelocityXSetAction();
        addAction(getBehavior1(), action);
        setAction(action);
    }

    @Override
    @Test
    public void onPerform_performAction_performAction() {
        double velocityX = 10.0;
        assertEquals(0.0, getObj1().getComponent(PhysicsHandler.class).getVelocityX());
        getAction().onPerform(velocityX);
        assertEquals(velocityX, getObj1().getComponent(PhysicsHandler.class).getVelocityX());
    }

}
