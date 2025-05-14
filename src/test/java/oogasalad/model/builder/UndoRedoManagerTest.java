package oogasalad.model.builder;

import static org.junit.jupiter.api.Assertions.*;

import oogasalad.model.builder.actions.BuilderCreateObjectAction;
import oogasalad.model.engine.architecture.GameObject;
import oogasalad.model.engine.architecture.GameScene;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class UndoRedoManagerTest {

  private GameScene scene;
  private TestEditorAction action;

  private static class TestEditorAction implements EditorAction {
    boolean undone = false;
    boolean redone = false;

    @Override
    public void undo() {
      undone = true;
    }

    @Override
    public void redo() {
      redone = true;
    }
  }

  @BeforeEach
  public void setUp() {
    scene = new GameScene("TestScene");
    UndoRedoManager.setCurrentScene(scene);
    UndoRedoManager.clear(scene);
    action = new TestEditorAction();
  }

  @Test
  public void setCurrentScene_setsCorrectScene() {
    assertSame(scene, UndoRedoManager.getCurrentScene());
  }

  @Test
  public void addAction_actionAdded_canUndo() {
    UndoRedoManager.addAction(action);
    UndoRedoManager.undo(scene);
    assertTrue(action.undone, "Undo should call the undo method on action");
  }

  @Test
  public void undo_actionInStack_callsUndoAndMovesToRedoStack() {
    UndoRedoManager.addAction(action);
    UndoRedoManager.undo(scene);
    assertTrue(action.undone, "Action should be undone after undo call");
  }

  @Test
  public void undo_emptyUndoStack_doesNothing() {
    assertDoesNotThrow(() -> UndoRedoManager.undo(scene));
  }

  @Test
  public void redo_actionInRedoStack_callsRedoAndMovesToUndoStack() {
    UndoRedoManager.addAction(action);
    UndoRedoManager.undo(scene);
    action.undone = false; // Reset to track redo separately

    UndoRedoManager.redo(scene);
    assertTrue(action.redone, "Redo should call redo method on action");
  }

  @Test
  public void redo_emptyRedoStack_doesNothing() {
    assertDoesNotThrow(() -> UndoRedoManager.redo(scene));
  }

  @Test
  public void clear_actionsInStack_stacksAreCleared() {
    UndoRedoManager.addAction(action);
    UndoRedoManager.clear(scene);

    assertDoesNotThrow(() -> {
      UndoRedoManager.undo(scene);
      UndoRedoManager.redo(scene);
    });

    assertFalse(action.undone, "Undo should not happen after clearing stack");
    assertFalse(action.redone, "Redo should not happen after clearing stack");
  }

  @Test
  public void popLastAction_actionAdded_popsWithoutUndo() {
    UndoRedoManager.addAction(action);
    UndoRedoManager.popLastAction();
    UndoRedoManager.undo(scene);
    assertFalse(action.undone, "Undo should not happen after popping last action");
  }

  @Test
  public void undoLastAction_createObject_objectUnregistered() {
    GameObject obj = new GameObject("TestObject");
    scene.registerObject(obj);
    BuilderCreateObjectAction createAction = new BuilderCreateObjectAction(scene, obj);

    UndoRedoManager.addAction(createAction);
    UndoRedoManager.undo(scene);

    assertFalse(scene.getActiveObjects().contains(obj), "Object should be unregistered after undoing creation");
  }

  @Test
  public void redoLastAction_createObject_objectReRegistered() {
    GameObject obj = new GameObject("TestObject");
    scene.registerObject(obj);
    BuilderCreateObjectAction createAction = new BuilderCreateObjectAction(scene, obj);

    UndoRedoManager.addAction(createAction);
    UndoRedoManager.undo(scene);
    UndoRedoManager.redo(scene);

    assertTrue(scene.getActiveObjects().contains(obj), "Object should be re-registered after redoing creation");
  }

}
