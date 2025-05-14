package oogasalad.model.engine.component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Map;
import java.util.HashMap;
import java.util.Collection;
import oogasalad.model.engine.architecture.GameComponent;
import oogasalad.model.engine.architecture.GameObject;
import oogasalad.model.serialization.serializable.SerializableField;

/**
 * A physics component responsible for detecting collisions and executing behaviors based on object
 * type. Primary responsibilities are calculating which other objects the parent is currently
 * colliding with (collidedColliders) based off of a pre-set group of objects (collidableTags),
 * resolving collisions with non-permeable objects, and providing helper functions pertaining to
 * specific collisions (isTouchingFromAbove, horizontallyAligned, etc.)
 *
 * @author Logan Dracos
 */
public class Collider extends GameComponent {

  /**
   * Enum for checking for directional touching, to be used in below touchingFromDirection method
   * (use cases doodle jump platforms, geo dash floor)
   */
  public enum CollisionDirection {
    ABOVE, BELOW, LEFT, RIGHT
  }

  private static final double COLLISION_OFFSET = 0.1;
  private static final double OVERLAP_TOLERANCE = 2;

  @Override
  public ComponentTag componentTag() {
    return ComponentTag.COLLISION;
  }

  @SerializableField
  private final List<String> collidableTags = new ArrayList<>();

  @SerializableField
  private boolean isPermeable = false;

  private final Set<Collider> collidedColliders = new HashSet<>();
  private final Map<CollisionDirection, Set<Collider>> touchMap = new HashMap<>();
  private Transform transform;

  @Override
  protected void awake() {
    transform = getComponent(Transform.class);
    for (CollisionDirection direction : CollisionDirection.values()) {
      touchMap.put(direction, new HashSet<>());
    }
  }

  @Override
  protected void update(double deltaTime) {
    collidedColliders.clear();
    for (CollisionDirection direction : CollisionDirection.values()) {
      touchMap.get(direction).clear();
    }
    for (GameComponent collider : getParent().getScene().getAllComponents()
        .get(ComponentTag.COLLISION)) {
      if (collidableTags.contains(collider.getParent().getTag())) {
        processCollision(collider.getParent());
      }
    }
    if (getParent().hasComponent(PhysicsHandler.class)) {
      resolveCollisions();
    }
  }

  private void processCollision(GameObject obj) {
    Collider collider = obj.getComponent(Collider.class);

    if (collider == this) {
      return;
    }

    Transform collidedTransform = obj.getComponent(Transform.class);
    if (isOverlapping(collidedTransform)) {
      collidedColliders.add(collider);
    }
  }

  private boolean isOverlapping(Transform other) {
    return transform.getX() < other.getX() + other.getScaleX() + OVERLAP_TOLERANCE &&
        transform.getX() + transform.getScaleX() > other.getX() - OVERLAP_TOLERANCE &&
        transform.getY() < other.getY() + other.getScaleY() + OVERLAP_TOLERANCE &&
        transform.getY() + transform.getScaleY() > other.getY() - OVERLAP_TOLERANCE;
  }

  private void resolveCollisions() {
    for (Collider collider : collidedColliders) {
      if (collider.isPermeable) {
        continue;
      }

      Transform other = collider.getComponent(Transform.class);

      double overlapX = calculateOverlapX(transform, other);
      double overlapY = calculateOverlapY(transform, other);

      if (overlapX < overlapY) {
        resolveCollisionX(transform, other, overlapX);
      } else {
        resolveCollisionY(transform, other);
      }
    }
  }

  private double calculateOverlapX(Transform thisTransform, Transform otherTransform) {
    double thisLeft = thisTransform.getX();
    double thisRight = thisLeft + thisTransform.getScaleX();
    double otherLeft = otherTransform.getX();
    double otherRight = otherLeft + otherTransform.getScaleX();

    return Math.min(thisRight, otherRight) - Math.max(thisLeft, otherLeft);
  }

  private double calculateOverlapY(Transform thisTransform, Transform otherTransform) {
    double thisTop = thisTransform.getY();
    double thisBottom = thisTop + thisTransform.getScaleY();
    double otherTop = otherTransform.getY();
    double otherBottom = otherTop + otherTransform.getScaleY();

    return Math.min(thisBottom, otherBottom) - Math.max(thisTop, otherTop);
  }

  private void resolveCollisionX(Transform thisTransform, Transform otherTransform,
      double overlapX) {
    double thisRight = thisTransform.getX() + thisTransform.getScaleX();
    double thisLeft = thisTransform.getX();
    double otherLeft = otherTransform.getX();

    if (thisRight > otherLeft && thisLeft < otherLeft) {
      thisTransform.setX(thisTransform.getX() - overlapX - COLLISION_OFFSET);
    } else {
      thisTransform.setX(thisTransform.getX() + overlapX + COLLISION_OFFSET);
    }
  }


  private void resolveCollisionY(Transform thisTransform, Transform otherTransform) {
    double thisBottom = thisTransform.getY() + thisTransform.getScaleY();
    double otherTop = otherTransform.getY();

    if (thisBottom > otherTop && thisTransform.getY() < otherTop) {
      thisTransform.setY(otherTop - thisTransform.getScaleY());

      if (getParent().hasComponent(PhysicsHandler.class)) {
        PhysicsHandler physics = getParent().getComponent(PhysicsHandler.class);
        physics.setVelocityY(0);
      }
    }
  }


  /**
   * Check if the collider has collided with another collider.
   *
   * @param tag the gameobject tag of the collider to check for collision
   * @return true if the collider has collided with another collider, false otherwise
   */
  public boolean collidesWith(String tag) {
    for (Collider collider : collidedColliders) {
      if (collider.getParent().getTag().equals(tag)) {
        return true;
      }
    }
    return false;
  }

  /**
   * check if the collider has collided with another collider.
   * @param obj the gameobject to check for collision
   * @return true if the collider has collided with another collider, false otherwise
   */
  public boolean collidesWith(GameObject obj) {
    for (Collider collider : collidedColliders) {
      if (collider.getParent() == obj) {
        return true;
      }
    }
    return false;
  }

  /**
   * Get the collection of game objects that this collider has collided with.
   *
   * @return a collection of game objects that this collider has collided with
   */
  public Collection<GameObject> getCollidedObjects() {
    Set<GameObject> collidedObjects = new HashSet<>();
    for (Collider collider : collidedColliders) {
      collidedObjects.add(collider.getParent());
    }
    return collidedObjects;
  }

  /**
   * Returns all the colliders that are touching this collider in a specific direction.
   * @param direction the direction to check for touching colliders
   * @return a collection of colliders that are touching this collider in the specified direction
   */
  public Collection<Collider> getTouching(CollisionDirection direction) {
    return touchMap.get(direction);
  }

  /**
   * Check if the collider is touching another collider from above, within a tolerance.
   *
   * @param tag       the gameobject tag of the collider to check for collision
   * @param tolerance the allowable difference between the bottom of this collider and the top of
   *                  the other collider.
   * @return true if the collider is touching another collider from above, false otherwise
   */
  public boolean touchingFromDirection(String tag, CollisionDirection direction, double tolerance) {
    for (GameObject obj : getParent().getScene().getActiveObjects()) {
      if (isTouchingFromDirection(obj, tag, direction, tolerance)) {
        touchMap.get(direction).add(obj.getComponent(Collider.class));
        return true;
      }
    }
    return false;
  }

  private boolean isTouchingFromDirection(GameObject obj, String tag, CollisionDirection direction, double tolerance) {
    if (!obj.hasComponent(Collider.class) || !obj.getTag().equals(tag)) {
      return false;
    }

    Transform other = obj.getComponent(Transform.class);

    double thisLeft = transform.getX();
    double thisRight = thisLeft + transform.getScaleX();
    double thisTop = transform.getY();
    double thisBottom = thisTop + transform.getScaleY();

    double otherLeft = other.getX();
    double otherRight = otherLeft + other.getScaleX();
    double otherTop = other.getY();
    double otherBottom = otherTop + other.getScaleY();

    return switch (direction) {
      case ABOVE -> touchingVertically(thisBottom, otherTop, tolerance) && horizontalOverlap(thisLeft, thisRight, otherLeft, otherRight);
      case BELOW -> touchingVertically(thisTop, otherBottom, tolerance) && horizontalOverlap(thisLeft, thisRight, otherLeft, otherRight);
      case LEFT  -> touchingHorizontally(thisRight, otherLeft, tolerance) && verticalOverlap(thisTop, thisBottom, otherTop, otherBottom);
      case RIGHT -> touchingHorizontally(thisLeft, otherRight, tolerance) && verticalOverlap(thisTop, thisBottom, otherTop, otherBottom);
    };
  }

  private boolean horizontalOverlap(double left1, double right1, double left2, double right2) {
    return right1 >= left2 && left1 <= right2;
  }

  private boolean verticalOverlap(double top1, double bottom1, double top2, double bottom2) {
    return bottom1 >= top2 && top1 <= bottom2;
  }

  private boolean touchingVertically(double edge1, double edge2, double tolerance) {
    return Math.abs(edge1 - edge2) <= tolerance;
  }

  private boolean touchingHorizontally(double edge1, double edge2, double tolerance) {
    return Math.abs(edge1 - edge2) <= tolerance;
  }

  /**
   * Protected method to provide all current collided colliders
   *
   * @return - All collided colliders
   */
  protected Set<Collider> getCollidedColliders() {
    return collidedColliders;
  }

  /**
   * Protected method to provide all current collidable tags
   *
   * @return - All tags
   */
  protected List<String> getCollidableTags() {
    return collidableTags;
  }
}
