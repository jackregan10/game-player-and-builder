package oogasalad.model.engine.component;

import oogasalad.model.engine.architecture.GameComponent;
import oogasalad.model.serialization.serializable.SerializableField;

/**
 * Handles the physics of a game object, including mass, velocity, and acceleration.
 *
 * @author Hsuan-Kai Liao
 */
public class PhysicsHandler extends GameComponent {

  @Override
  public ComponentTag componentTag() {
    return ComponentTag.PHYSICS;
  }

  @SerializableField
  private double mass = 1.0;
  @SerializableField
  private double velocityX;
  @SerializableField
  private double velocityY;
  @SerializableField
  private double accelerationX;
  @SerializableField
  private double accelerationY;

  private Transform transform;

  @Override
  public void awake() {
    transform = getComponent(Transform.class);
  }

  @Override
  public void update(double deltaTime) {
    // Update velocity based on acceleration
    velocityX += accelerationX * deltaTime;
    velocityY += accelerationY * deltaTime;

    // Update position based on velocity
    transform.setX(transform.getX() + velocityX * deltaTime);
    transform.setY(transform.getY() + velocityY * deltaTime);
  }

  /**
   * Returns the velocityX of the object.
   */
  public double getVelocityX() {
    return velocityX;
  }

  /**
   * Sets the velocityX of the object.
   */
  public void setVelocityX(double velocityX) {
    this.velocityX = velocityX;
  }

  /**
   * Returns the velocityY of the object.
   */
  public double getVelocityY() {
    return velocityY;
  }

  /**
   * Sets the velocityY of the object.
   */
  public void setVelocityY(double velocityY) {
    this.velocityY = velocityY;
  }

  /**
   * Sets the accelerationX of the object.
   */
  public void setAccelerationX(double accelerationX) {
    this.accelerationX = accelerationX;
  }

  /**
   * Returns the accelerationX of the object.
   */
  public double getAccelerationX() {
    return accelerationX;
  }

  /**
   * Sets the accelerationY of the object.
   */
  public void setAccelerationY(double accelerationY) {
    this.accelerationY = accelerationY;
  }

  /**
   * Returns the accelerationY of the object.
   */
  public double getAccelerationY() {
    return accelerationY;
  }


  /**
   * Applies an impulse to the object, changing its velocityX.
   *
   * @param impulseX the impulse in the X direction
   */
  public void applyImpulseX(double impulseX) {
    velocityX += impulseX / mass;
  }

  /**
   * Applies an impulse to the object, changing its velocityY.
   *
   * @param impulseY the impulse in the Y direction
   */
  public void applyImpulseY(double impulseY) {
    velocityY += impulseY / mass;
  }
}
