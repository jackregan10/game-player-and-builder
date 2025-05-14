package oogasalad.model.builder.actions;

import oogasalad.model.builder.EditorAction;
import oogasalad.model.engine.architecture.GameObject;
import oogasalad.model.engine.component.Transform;

/**
 * Class that tracks when GameObjects are moved
 *
 * @author Reyan Shariff
 */

public class BuilderMoveObjectAction implements EditorAction {

  private final GameObject object;
  private final double fromX, fromY;
  private final double toX, toY;

  /**
   * Constructor for MoveObjectAction
   *
   * @param object - GameObject that is being moved
   * @param fromX  - X coordinate of the object before the move
   * @param fromY  - Y coordinate of the object before the move
   * @param toX    - X coordinate of the object after the move
   * @param toY    - Y coordinate of the object after the move
   */
  public BuilderMoveObjectAction(GameObject object, double fromX, double fromY, double toX, double toY) {
    this.object = object;
    this.fromX = fromX;
    this.fromY = fromY;
    this.toX = toX;
    this.toY = toY;
  }

  @Override
  public void undo() {
    object.getComponent(Transform.class).setX(fromX);
    object.getComponent(Transform.class).setY(fromY);
  }

  @Override
  public void redo() {
    object.getComponent(Transform.class).setX(toX);
    object.getComponent(Transform.class).setY(toY);
  }
}
