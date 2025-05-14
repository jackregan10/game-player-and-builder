package oogasalad.model.engine.subComponent.behavior;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import oogasalad.model.engine.subComponent.behavior.action.PushActionY;
import oogasalad.model.engine.component.PhysicsHandler;
import oogasalad.model.engine.component.Transform;
import oogasalad.model.engine.subComponent.behavior.constraint.TouchingFromTopBottomConstraint;
import oogasalad.model.engine.component.Collider;

public class PushActionYTest extends ActionsTest<PushActionY> {

    @Override
    public void customSetUp() {
        PushActionY action = new PushActionY();
        getObj1().addComponent(new PhysicsHandler());
        addAction(getBehavior1(), action);
        setAction(action);
        addCollidableTag(getObj1().getComponent(Collider.class), getObj2().getTag());
        addCollidableTag(getObj2().getComponent(Collider.class), getObj1().getTag());
    }

    @Override
    @Test
    public void onPerform_performAction_performAction() {
        Transform transform1 = getObj1().getComponent(Transform.class);
        Transform transform2 = getObj2().getComponent(Transform.class);
        PhysicsHandler physicsHandler = getObj1().getComponent(PhysicsHandler.class);
        transform1.setX(0);
        transform1.setY(87);
        transform2.setX(0);
        transform2.setY(0);
        physicsHandler.setVelocityX(10);
        physicsHandler.setVelocityY(10);
        assertEquals(0, transform2.getX());
        assertEquals(0, transform2.getY());
        getScene1().setDeltaTime(1.0);
        TouchingFromTopBottomConstraint constraint = new TouchingFromTopBottomConstraint();
        addConstraint(getBehavior1(), constraint);
        step();
        constraint.onCheck(getObj2().getTag());
        getAction().onPerform(getObj2().getTag());
        assertEquals(10 * getObj1().getScene().getDeltaTime(), transform2.getY());
        assertEquals(0, transform2.getX());
    }
    
}
