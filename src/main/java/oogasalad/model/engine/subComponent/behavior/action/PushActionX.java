package oogasalad.model.engine.subComponent.behavior.action;

import java.util.List;

import oogasalad.model.engine.architecture.GameObject;
import oogasalad.model.engine.component.Collider;

/**
 * The PushActionX class is a behavior action that pushes a game object in the X direction based on
 * the velocity of the parent object. It is used to move objects in the game world along the X axis.
 * 
 * @author Christian Bepler
 */

public class PushActionX extends PushAction {

    public static final List<Collider.CollisionDirection> COLLISION_DIRECTIONS = List.of(
            Collider.CollisionDirection.LEFT,
            Collider.CollisionDirection.RIGHT
    );

    @Override
    protected void perform(String tag) {
        for (GameObject toPush : toPushObjects(tag, COLLISION_DIRECTIONS)) {
            push(toPush, PushDirection.X);
        }
    }
}
