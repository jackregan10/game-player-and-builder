package oogasalad.model.builder.actions;

import oogasalad.model.builder.EditorAction;

/**
 * An undo/redo action that modifies a field using runnables.
 * @author Reyan Shariff
 */
public class BuilderPanelEditAction<T> implements EditorAction {

  private final Runnable undoAction;
  private final Runnable redoAction;

  /**
   * An undo/redo action that modifies a field using runnables.
   */
  public BuilderPanelEditAction(Runnable undoAction, Runnable redoAction) {
    this.undoAction = undoAction;
    this.redoAction = redoAction;
  }

  @Override
  public void undo() {
    undoAction.run();
  }

  @Override
  public void redo() {
    redoAction.run();
  }
}
