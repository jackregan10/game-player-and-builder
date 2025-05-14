package oogasalad.model.engine.component;

import oogasalad.model.engine.architecture.GameComponent;
import oogasalad.model.serialization.serializable.SerializableField;

/**
 * A component that defines the spatial and visual properties of a GameObject, including position,
 * scale, rotation, and image path.
 * <p>
 * This is typically the foundational component for rendering and positioning game entities within
 * the scene.
 *
 * @author Hsuan-Kai Liao, Christian Bepler
 */
public class Transform extends GameComponent implements Renderable {

  @Override
  public ComponentTag componentTag() {
    return ComponentTag.TRANSFORM;
  }

  @SerializableField
  private double x;
  @SerializableField
  private double y;
  @SerializableField
  private int zIndex;
  @SerializableField
  private double rotation;
  @SerializableField
  private double scaleX;
  @SerializableField
  private double scaleY;

  /**
   * Returns the x-coordinate of the GameObject.
   *
   * @return the x position
   */
  public double getX() {
    return x;
  }

  /**
   * Sets the x-coordinate of the GameObject.
   *
   * @param x the new x position
   */
  public void setX(double x) {
    this.x = x;
  }

  /**
   * Returns the y-coordinate of the GameObject.
   *
   * @return the y position
   */
  public double getY() {
    return y;
  }

  /**
   * Sets the y-coordinate of the GameObject.
   *
   * @param y the new y position
   */
  public void setY(double y) {
    this.y = y;
  }

  /**
   * Returns the rotation of the GameObject.
   *
   * @return the rotation angle
   */
  public double getRotation() {
    return rotation;
  }

  /**
   * Sets the rotation of the GameObject.
   *
   * @param rotation the new rotation angle
   */
  public void setRotation(double rotation) {
    this.rotation = rotation;
  }

  /**
   * Returns the x-axis scale factor.
   *
   * @return the scale along the x-axis
   */
  public double getScaleX() {
    return scaleX;
  }

  /**
   * Sets the x-axis scale factor.
   *
   * @param scaleX the new scale value along the x-axis
   */
  public void setScaleX(double scaleX) {
    this.scaleX = Math.max(scaleX, 0);
  }

  /**
   * Returns the y-axis scale factor.
   *
   * @return the scale along the y-axis
   */
  public double getScaleY() {
    return scaleY;
  }

  /**
   * Sets the y-axis scale factor.
   *
   * @param scaleY the new scale value along the y-axis
   */
  public void setScaleY(double scaleY) {
    this.scaleY = Math.max(scaleY, 0);
  }

  /**
   * Returns the z-index for rendering the image.
   *
   * @return the z-index
   */
  public int getZIndex() {
    return zIndex;
  }
}