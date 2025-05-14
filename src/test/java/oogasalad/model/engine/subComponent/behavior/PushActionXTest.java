package oogasalad.model.engine.subComponent.behavior;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import oogasalad.model.engine.subComponent.behavior.action.PushActionX;
import oogasalad.model.engine.component.Collider;
import oogasalad.model.engine.component.PhysicsHandler;
import oogasalad.model.engine.component.Transform;
import oogasalad.model.engine.subComponent.behavior.constraint.TouchingFromSideConstraint;

public class PushActionXTest extends ActionsTest<PushActionX> {
    private Transform transform1;
    private Transform transform2;

    @Override
    public void customSetUp() {
        PushActionX action = new PushActionX();
        getObj1().addComponent(new PhysicsHandler());
        addAction(getBehavior1(), action);
        setAction(action);
        addCollidableTag(getObj1().getComponent(Collider.class), getObj2().getTag());
        addCollidableTag(getObj2().getComponent(Collider.class), getObj1().getTag());
        transform1 = getObj1().getComponent(Transform.class);
        transform2 = getObj2().getComponent(Transform.class);
    }

    @Override
    @Test
    public void onPerform_performAction_performAction() {
        PhysicsHandler physicsHandler = getObj1().getComponent(PhysicsHandler.class);
        transform1.setX(87);
        transform1.setY(0);
        transform2.setX(0);
        transform2.setY(0);
        physicsHandler.setVelocityX(10);
        physicsHandler.setVelocityY(10);
        assertEquals(0, transform2.getX());
        assertEquals(0, transform2.getY());
        getScene1().setDeltaTime(1.0);
        TouchingFromSideConstraint constraint = new TouchingFromSideConstraint();
        addConstraint(getBehavior1(), constraint);
        step();
        constraint.onCheck(getObj2().getTag());
        getAction().onPerform(getObj2().getTag());
        assertEquals(10 * getObj1().getScene().getDeltaTime(), transform2.getX());
        assertEquals(0, transform2.getY());
    }
    
}
