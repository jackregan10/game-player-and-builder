package oogasalad.model.engine.component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import oogasalad.model.engine.architecture.GameComponent;
import oogasalad.model.engine.architecture.KeyCode;

/**
 * Handles raw keyboard input with custom key codes.
 *
 * @author Hsuan-Kai Liao
 */
public final class InputHandler extends GameComponent {

  private final Set<Integer> currentKeys = new HashSet<>();
  private final Set<Integer> previousKeys = new HashSet<>();

  private boolean currentClicked = false;
  private boolean previousClicked = false;

  private double mouseX;
  private double mouseY;

  private Transform transform;

  @Override
  public ComponentTag componentTag() {
    return ComponentTag.INPUT;
  }

  @Override
  protected void awake() {
    transform = getComponent(Transform.class);
  }

  @Override
  protected void update(double deltaTime) {
    previousKeys.clear();
    previousKeys.addAll(currentKeys);
    currentKeys.clear();
    currentKeys.addAll(getParent().getScene().getGame().getCurrentInputKeys());

    List<Double> mouseInputs = getParent().getScene().getGame().getInputMouses();
    previousClicked = currentClicked;
    currentClicked = mouseInputs.get(0) > 0;
    mouseX = mouseInputs.get(1);
    mouseY = mouseInputs.get(2);
  }

  /**
   * @return - Current keys being pressed
   */
  public Set<Integer> getPressedKeys() {
    return currentKeys;
  }

  /**
   * Manually set the inputHandler's inputs.
   *
   * @param pressedKeys - Keys to be pressed
   */
  public void setPressedKeys(Set<Integer> pressedKeys) {
    this.currentKeys.clear();
    this.currentKeys.addAll(pressedKeys);
  }

  /**
   * Manually clear all previous and current keys (inputs)
   */
  public void resetInputState() {
    currentKeys.clear();
    previousKeys.clear();
  }

  /**
   * Returns true if the key was just pressed this frame.
   */
  public boolean isKeyPressed(KeyCode keyCode) {
    if (keyCode == null) {
      return false;
    }
    return currentKeys.contains(keyCode.getValue()) && !previousKeys.contains(keyCode.getValue());
  }

  /**
   * Returns true if the key is being held down.
   */
  public boolean isKeyHold(KeyCode keyCode) {
    if (keyCode == null) {
      return false;
    }
    return currentKeys.contains(keyCode.getValue());
  }

  /**
   * Returns true if the key was just released this frame.
   */
  public boolean isKeyReleased(KeyCode keyCode) {
    if (keyCode == null) {
      return false;
    }
    return !currentKeys.contains(keyCode.getValue()) && previousKeys.contains(keyCode.getValue());
  }

  /**
   * Manually to register mouse click and location
   *
   * @param x - X position of click
   * @param y - Y position of click
   */
  public void registerMouseClick(double x, double y) {
    currentClicked = true;
    mouseX = x;
    mouseY = y;
  }

  /**
   * @return - Checks if mouse is clicked on this frame.
   */
  public boolean isMouseClicked() {
    return !previousClicked && currentClicked && isClickInObject();
  }

  /**
   * @return - Checks if the mouse is under clicking.
   */
  public boolean isMouseHold() {
    return previousClicked && currentClicked && isClickInObject();
  }

  /**
   * @return - Checks if the mouse is released on this frame
   */
  public boolean isMouseReleased() {
    return previousClicked && !currentClicked && isClickInObject();
  }

  /**
   * @return - X position of mouse click
   */
  public double getMouseX() {
    return mouseX;
  }

  /**
   * @return - Y position of mouse click
   */
  public double getMouseY() {
    return mouseY;
  }

  // TODO: Handles Rotation
  private boolean isClickInObject() {
    return transform.getX() < mouseX &&
        transform.getX() + transform.getScaleX() > mouseX &&
        transform.getY() < mouseY &&
        transform.getY() + transform.getScaleY() > mouseY;
  }
}
