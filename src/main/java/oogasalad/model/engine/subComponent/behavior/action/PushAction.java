package oogasalad.model.engine.subComponent.behavior.action;

import java.util.ArrayList;
import java.util.List;
import java.util.Collection;

import java.util.Set;
import oogasalad.model.engine.architecture.GameComponent;
import oogasalad.model.engine.architecture.GameObject;
import oogasalad.model.engine.subComponent.behavior.BehaviorAction;
import oogasalad.model.engine.component.PhysicsHandler;
import oogasalad.model.engine.component.Transform;
import oogasalad.model.engine.component.Collider;


/**
 * The PushAction class is a behavior action that pushes a game object in the direction of the
 * velocity of the parent object. It is used to move objects in the game world.
 * 
 * @author Christian Bepler
 */

public abstract class PushAction extends BehaviorAction<String> {

    private PhysicsHandler myPhysics;

    /// Enum for checking for directional pushing, to be used in below push method
    public enum PushDirection {
        X,
        Y
    }

    @Override
    protected String defaultParameter() {
        return "";
    }

    @Override
    public Set<Class<? extends GameComponent>> requiredComponents() {
        return Set.of(
            Transform.class,
            PhysicsHandler.class,
            Collider.class
        );
    }

    @Override
    protected void awake(){
        myPhysics = getBehavior().getController().getParent().getComponent(PhysicsHandler.class);
    }

    protected void push(GameObject toPush, PushDirection direction) {
        Transform pushTransform = toPush.getComponent(Transform.class);
        if (direction == PushDirection.X) {
            pushTransform.setX(pushTransform.getX() + myPhysics.getVelocityX() * getBehavior().getController().getParent().getScene().getDeltaTime());
        } else if (direction == PushDirection.Y) {
            pushTransform.setY(pushTransform.getY() + myPhysics.getVelocityY() * getBehavior().getController().getParent().getScene().getDeltaTime());
        }
    }

    protected Collection<GameObject> toPushObjects(String tag, List<Collider.CollisionDirection> collisionDirections) {
        Collider myCollider = getBehavior().getController().getParent().getComponent(Collider.class);
        List<GameObject> toPushObjects = new ArrayList<>();
        for (Collider.CollisionDirection direction : collisionDirections) {
            myCollider.getTouching(direction).stream()
                      .map(Collider::getParent)
                      .filter(obj -> obj.getTag().equals(tag))
                      .forEach(toPushObjects::add);
        }
        return toPushObjects;
    }
}
