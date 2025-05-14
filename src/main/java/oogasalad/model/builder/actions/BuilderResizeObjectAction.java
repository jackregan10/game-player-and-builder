package oogasalad.model.builder.actions;

import oogasalad.model.builder.EditorAction;
import oogasalad.model.builder.StateMonitors.TransformState;
import oogasalad.model.engine.architecture.GameObject;
import oogasalad.model.engine.component.Transform;

/**
 * Class that tracks when GameObjects are resized
 *
 * @author Reyan Shariff
 */

public class BuilderResizeObjectAction implements EditorAction {

  private final GameObject object;
  private final TransformState fromState;
  private final TransformState toState;

  /**
   * Construct the action for resizing the Action.
   *
   * @param object    the selected object.
   * @param fromState the old transform state
   * @param toState   the new transform state
   */
  public BuilderResizeObjectAction(GameObject object, TransformState fromState, TransformState toState) {
    this.object = object;
    this.fromState = fromState;
    this.toState = toState;
  }

  @Override
  public void undo() {
    Transform t = object.getComponent(Transform.class);
    t.setX(fromState.x());
    t.setY(fromState.y());
    t.setScaleX(fromState.width());
    t.setScaleY(fromState.height());
  }

  @Override
  public void redo() {
    Transform t = object.getComponent(Transform.class);
    t.setX(toState.x());
    t.setY(toState.y());
    t.setScaleX(toState.width());
    t.setScaleY(toState.height());
  }
}