package oogasalad.model.builder;
import java.util.Deque;
import java.util.HashMap;
import java.util.ArrayDeque;
import java.util.Map;
import oogasalad.model.engine.architecture.GameScene;

/**
 * API to manage undo/redo
 *
 * @author Reyan Shariff
 */
public final class UndoRedoManager {
  private UndoRedoManager() {}
  private static GameScene currentScene;
  private static final Map<GameScene, Deque<EditorAction>> undoStacks = new HashMap<>();
  private static final Map<GameScene, Deque<EditorAction>> redoStacks = new HashMap<>();

  /**
   * Adds an action to the Undo Stack
   * @param action is an editor action class from the actions folder.
   */
  public static void addAction(EditorAction action)
  {
    undoStacks.computeIfAbsent(currentScene, k -> new ArrayDeque<>()).push(action);
  }

  /**
   *Sets the current scene for the API
   * @param scene is the GameScene being switched to.
   */

  public static void setCurrentScene(GameScene scene)
  {
    currentScene = scene;
  }

  /**
   *Retrieves the current scene
   */
  public static GameScene getCurrentScene()
  {
    return currentScene;
  }

  /**
   *Undoes the most recent action of the selected scene
   * @param scene is the scene in question
   */
  public static void undo(GameScene scene) {
    Deque<EditorAction> undoStack = getUndoStack(scene);
    if (!undoStack.isEmpty()) {
      EditorAction action = undoStack.pop();
      action.undo();
      getRedoStack(scene).push(action);
    }
  }
  /**
   *Redoes the most recently undone action of the selected scene
   * @param scene is the scene in question
   */

  public static void redo(GameScene scene) {
    Deque<EditorAction> redoStack = getRedoStack(scene);
    if (!redoStack.isEmpty()) {
      EditorAction action = redoStack.pop();
      action.redo();
      getUndoStack(scene).push(action);
    }
  }
  /**
   *Clears the action history for a selected scene
   * @param scene is the scene in question
   */
  public static void clear(GameScene scene) {
    getUndoStack(scene).clear();
    getRedoStack(scene).clear();
  }

  /**
   * Discards the most recent action of the current scene.
   */
  public static void popLastAction() {
    Deque<EditorAction> undoStack = getUndoStack(currentScene);
    if (!undoStack.isEmpty()) {
      undoStack.pop();
    }
  }


  private static Deque<EditorAction> getUndoStack(GameScene scene) {
    return undoStacks.computeIfAbsent(scene, k -> new ArrayDeque<>());
  }

  private static Deque<EditorAction> getRedoStack(GameScene scene) {
    return redoStacks.computeIfAbsent(scene, k -> new ArrayDeque<>());
  }
}
