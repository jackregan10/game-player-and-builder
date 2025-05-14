package oogasalad.model.builder;

/**
 * Interface for Storing actions in the builder editor
 *
 * @author Reyan Shariff
 */
public interface EditorAction {

  /**
   * undoes the last action
   */
  void undo();

  /**
   * redoes the last action
   */
  void redo();
}
