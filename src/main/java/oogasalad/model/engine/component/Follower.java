package oogasalad.model.engine.component;

import java.awt.geom.Point2D;

import static oogasalad.model.config.GameConfig.LOGGER;

import oogasalad.model.config.GameConfig;
import oogasalad.model.engine.architecture.GameComponent;
import oogasalad.model.engine.architecture.GameObject;
import oogasalad.model.serialization.serializable.SerializableField;

/**
 * The FollowBehavior class is used to make a game object follow another game object with a
 * specified offset.
 */

public class Follower extends GameComponent {

  private static final String FOLLOW_OBJECT_MISSING_TRANSFORM =
      "Follow Object Missing Transform Component";

  @Override
  public ComponentTag componentTag() {
    return ComponentTag.FOLLOWER;
  }

  @SerializableField
  private String followObjectName;
  @SerializableField
  private boolean smoothMovement;

  private double offsetX;
  private double offsetY;
  private Transform myTransform;
  private GameObject followObject;


  private Point2D previousPosition;
  private Point2D currentPosition;
  private PhysicsHandler physicsHandler;
  private double speedLimit;

  /**
   * Constructor for Follower. Sets default values for the offset and the follow object.
   */
  public Follower() {
    super();
    this.followObject = null;
    this.offsetX = 0;
    this.offsetY = 0;
  }

  @Override
  public void awake() {
    myTransform = getParent().getComponent(Transform.class);
    followObject = getParent().getScene().getObject(followObjectName);
    Transform attachTransform = followObject.getComponent(Transform.class);
    offsetX = myTransform.getX() - attachTransform.getX();
    offsetY = myTransform.getY() - attachTransform.getY();
    if (smoothMovement) {
      currentPosition = new Point2D.Double(myTransform.getX(), myTransform.getY());
      previousPosition = new Point2D.Double(currentPosition.getX(), currentPosition.getY());
      physicsHandler = followObject.getComponent(PhysicsHandler.class);
      speedLimit = 0;
    }
  }

  @Override
  public void update(double deltaTime) {
    Transform targetTransform = followObject.getComponent(Transform.class);
    if (targetTransform == null) {
      LOGGER.error(FOLLOW_OBJECT_MISSING_TRANSFORM);
      return;
    }
    myTransform.setX(targetTransform.getX() + offsetX);
    myTransform.setY(targetTransform.getY() + offsetY);
    if (smoothMovement) {
      smoothMovement(deltaTime);
    }
  }

  /**
   * Smooths the movement of the follower to prevent jittering.
   *
   * @param deltaTime the time since the last update
   */
  private void smoothMovement(double deltaTime) {
    double minSpeed = Math.sqrt(
        Math.pow(physicsHandler.getVelocityX(), 2) + Math.pow(physicsHandler.getVelocityY(), 2));
    double playerAcceleration = Math.sqrt(
        Math.pow(physicsHandler.getAccelerationX(), 2) + Math.pow(physicsHandler.getAccelerationY(),
            2));
    double acceleration = Math.max(1, playerAcceleration);

    currentPosition = new Point2D.Double(myTransform.getX(), myTransform.getY());
    double distance = Math.sqrt(Math.pow(currentPosition.getX() - previousPosition.getX(), 2)
        + Math.pow(currentPosition.getY() - previousPosition.getY(), 2));
    double maxDistance = speedLimit * deltaTime;

    if (distance > maxDistance) {
      speedLimit = Math.max(minSpeed + acceleration * deltaTime,
          speedLimit + acceleration * deltaTime);
      maxDistance = speedLimit * deltaTime;
      double ratio = maxDistance / distance;
      double newX =
          previousPosition.getX() + (currentPosition.getX() - previousPosition.getX()) * ratio;
      double newY =
          previousPosition.getY() + (currentPosition.getY() - previousPosition.getY()) * ratio;
      myTransform.setX(newX);
      myTransform.setY(newY);
    } else {
      speedLimit = Math.max(minSpeed, speedLimit - acceleration * deltaTime);
    }
    previousPosition = new Point2D.Double(myTransform.getX(), myTransform.getY());
  }

  /**
   * Sets the object to follow.
   *
   * @param followObject the object to follow
   * @apiNote Use this method if the awake method for this component has already been called.
   */
  public void setFollowObject(GameObject followObject) {
    if (followObject == null) {
      LOGGER.error(GameConfig.getText("nullFollowObject"));
      throw new IllegalArgumentException(GameConfig.getText("nullFollowObject"));
    }
    Transform attachTransform;
    try {
      attachTransform = followObject.getComponent(Transform.class);
    } catch (IllegalArgumentException e) {
      LOGGER.error(FOLLOW_OBJECT_MISSING_TRANSFORM);
      throw new IllegalArgumentException(FOLLOW_OBJECT_MISSING_TRANSFORM, e);
    }
    myTransform = getParent().getComponent(Transform.class);
    offsetX = myTransform.getX() - attachTransform.getX();
    offsetY = myTransform.getY() - attachTransform.getY();
    this.followObject = followObject;
    this.followObjectName = followObject.getName();
  }

  /**
   * Sets the offset for the follower.
   *
   * @param offsetX the x offset
   * @param offsetY the y offset
   */
  public void setOffset(double offsetX, double offsetY) {
    this.offsetX = offsetX;
    this.offsetY = offsetY;
  }

  /**
   * Gets the object to follow.
   *
   * @return the object to follow
   */
  public GameObject getFollowObject() {
    return followObject;
  }

  /**
   * Gets the x offset.
   *
   * @return the x offset
   */
  public double getOffsetX() {
    return offsetX;
  }

  /**
   * Gets the y offset.
   *
   * @return the y offset
   */
  public double getOffsetY() {
    return offsetY;
  }

  /**
   * Sets the name of the object to follow.
   *
   * @param followObjectName the name of the object to follow
   * @apiNote Use this method if the awake method for this component has not been called yet.
   */
  public void setFollowObjectName(String followObjectName) {
    this.followObjectName = followObjectName;
  }

  /**
   * Gets the name of the object to follow.
   *
   * @return the name of the object to follow
   */
  public String getFollowObjectName() {
    return followObjectName;
  }

  /**
   * Sets the smooth movement flag.
   *
   * @param smoothMovement true if smooth movement is enabled, false otherwise
   */
  public void setSmoothMovement(boolean smoothMovement) {
    this.smoothMovement = smoothMovement;
  }

  /**
   * Gets the smooth movement flag.
   *
   * @return true if smooth movement is enabled, false otherwise
   */
  public boolean isSmoothMovement() {
    return smoothMovement;
  }

  /**
   * @return - Current position as a Point2D
   */
  protected Point2D getCurrentPosition() {
    return currentPosition;
  }

  /**
   * @return - Previous position as a Point2D
   */
  protected Point2D getPreviousPosition() {
    return previousPosition;
  }

  /**
   * @return - Current speed limit
   */
  protected double getSpeedLimit() {
    return speedLimit;
  }
}
