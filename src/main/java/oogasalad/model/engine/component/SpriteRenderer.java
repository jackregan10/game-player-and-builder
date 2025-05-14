package oogasalad.model.engine.component;

import oogasalad.model.engine.architecture.GameComponent;
import oogasalad.model.resource.ResourcePath;
import oogasalad.model.serialization.serializable.SerializableField;

/**
 * A visual component that represents an image to be rendered at a specific position. This component
 * stores the image path and its intended render coordinates (x, y).
 *
 * @author Logan Dracos
 */
public class SpriteRenderer extends GameComponent implements Renderable {

  @SerializableField
  private ResourcePath imagePath = new ResourcePath();
  @SerializableField
  private double offsetX;
  @SerializableField
  private double offsetY;
  @SerializableField
  private boolean tiled;
  @SerializableField
  private double horizontalTiledAmount;
  @SerializableField
  private double verticalTiledAmount;

  @Override
  public ComponentTag componentTag() {
    return ComponentTag.SPRITE;
  }

  /**
   * Returns the x-coordinate for rendering the image.
   *
   * @return the x position
   */
  public double getOffsetX() {
    return offsetX;
  }

  /**
   * Sets the x-coordinate for rendering the image.
   *
   * @param x the new x position
   */
  public void setOffsetX(double x) {
    this.offsetX = x;
  }

  /**
   * Returns the y-coordinate for rendering the image.
   *
   * @return the y position
   */
  public double getOffsetY() {
    return offsetY;
  }

  /**
   * Sets the y-coordinate for rendering the image.
   *
   * @param y the new y position
   */
  public void setOffsetY(double y) {
    this.offsetY = y;
  }

  /**
   * Returns the horizontalTiledAmount of the image.
   *
   * @return the horizontalTiledAmount
   */
  public double getHorizontalTiledAmount() {
    return horizontalTiledAmount;
  }

  /**
   * Sets the horizontalTiledAmount of the image.
   *
   * @param horizontalTiledAmount the new horizontalTiledAmount
   */
  public void setHorizontalTiledAmount(double horizontalTiledAmount) {
    this.horizontalTiledAmount = Math.max(0, horizontalTiledAmount);
  }

  /**
   * Returns the verticalTiledAmount of the image.
   *
   * @return the verticalTiledAmount
   */
  public double getVerticalTiledAmount() {
    return verticalTiledAmount;
  }

  /**
   * Sets the verticalTiledAmount of the image.
   *
   * @param verticalTiledAmount the new verticalTiledAmount
   */
  public void setVerticalTiledAmount(double verticalTiledAmount) {
    this.verticalTiledAmount = Math.max(0, verticalTiledAmount);
  }

  /**
   * Sets the path to the image file used for rendering.
   *
   * @param imagePath the new image path
   */
  public void setImagePath(ResourcePath imagePath) {
    this.imagePath = imagePath;
  }

  /**
   * Returns the path to the image file used for rendering.
   *
   * @return the image path
   */
  public ResourcePath getImagePath() {
    return imagePath;
  }

  /**
   * Returns whether the image should be tiled horizontally.
   *
   * @return true if the image is horizontally tiled, false otherwise
   */
  public boolean getTiled() {
    return tiled;
  }
}
