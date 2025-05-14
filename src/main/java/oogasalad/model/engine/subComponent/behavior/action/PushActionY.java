package oogasalad.model.engine.subComponent.behavior.action;

import java.util.List;
import oogasalad.model.engine.architecture.GameObject;
import oogasalad.model.engine.component.Collider;

/**
 * The PushActionY class is a behavior action that pushes a game object in the Y direction based on
 * the velocity of the parent object. It is used to move objects in the game world along the Y axis.
 * 
 * @author Christian Bepler
 */

public class PushActionY extends PushAction {

    public static final List<Collider.CollisionDirection> COLLISION_DIRECTIONS = List.of(
        Collider.CollisionDirection.ABOVE,
        Collider.CollisionDirection.BELOW
    );

    @Override
    protected void perform(String tag) {
        for (GameObject toPush : toPushObjects(tag, COLLISION_DIRECTIONS)) {
            push(toPush, PushDirection.Y);
        }
    }
}
