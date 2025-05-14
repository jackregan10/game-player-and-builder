package oogasalad.model.builder;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.Deque;
import oogasalad.model.builder.StateMonitors.TransformState;
import oogasalad.model.builder.actions.BuilderMoveObjectAction;
import oogasalad.model.builder.actions.BuilderResizeObjectAction;
import oogasalad.model.engine.architecture.GameObject;
import oogasalad.model.engine.architecture.GameScene;
import oogasalad.model.engine.architecture.Game;
import oogasalad.model.engine.component.Camera;
import oogasalad.model.engine.component.Follower;
import oogasalad.model.engine.component.Transform;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class BuilderTest {

  private Builder builder;
  private GameObject object;
  private Game game;
  private GameScene scene;

  @BeforeEach
  public void setUp() throws Exception {
    game = new Game();
    scene = new GameScene("test");
    game.addScene(scene);
    game.changeScene("test");
    object = new GameObject("TestObject");
    object.addComponent(Transform.class);
    object.getComponent(Transform.class).setX(0);
    object.getComponent(Transform.class).setY(0);
    scene.registerObject(object);

    builder = new Builder();
    Field gameField = Builder.class.getDeclaredField("game");
    gameField.setAccessible(true);
    gameField.set(builder, game);
    builder.setCurrentScene(scene);
  }

  @Test
  public void loadGame_notSteppingGame_illegalParameterTest_doesNotThrow() {
    URL fileUrl = getClass().getResource("/jsonTestFiles/invalidFollowerTest.json");
    assertNotNull(fileUrl);
    String filePath = fileUrl.getPath();
    assertDoesNotThrow(() -> builder.loadGame(filePath));
  }

  @Test
  public void deleteSelectedObject_selectedObject_unregister() {
    builder.selectExistingObject(object);
    assertEquals(builder.getSelectedObject(), object);
    builder.deleteSelectedObject();
    assertFalse(builder.getCurrentScene().getActiveObjects().contains(object));
  }

  @Test
  public void deleteSelectedObject_noSelectedObject_returnsTrue()
  {
    builder.deleteSelectedObject();
    assertTrue(builder.getCurrentScene().getActiveObjects().contains(object));

  }

  @Test
  public void placeObject_selectedObject_movesToCorrectLocation() {

    builder.selectExistingObject(object);

    double newX = 100;
    double newY = 200;

    builder.placeObject(newX, newY);

    // Check that object has been moved to the new location
    assertEquals(newX, object.getComponent(Transform.class).getX());
    assertEquals(newY, object.getComponent(Transform.class).getY());
  }

  @Test
  public void placeObject_noSelectedObject_doesNothing() {
    double originalX = object.getComponent(Transform.class).getX();
    double originalY = object.getComponent(Transform.class).getY();
    builder.placeObject(500, 500); // should not affect anything
    assertEquals(object.getComponent(Transform.class).getX(), originalX);
    assertEquals(object.getComponent(Transform.class).getY(), originalY);
  }

  @Test
  public void undoLastAction_emptyStack_doesNothing() {
    double originalX = object.getComponent(Transform.class).getX();
    double originalY = object.getComponent(Transform.class).getY();

    // No actions yet, so undo shouldn't do anything
    builder.undoLastAction();

    assertEquals(object.getComponent(Transform.class).getX(), originalX);
    assertEquals(object.getComponent(Transform.class).getY(), originalY);
  }

  @Test
  public void getSelectedObject_returnsCorrectObject() {
    assertNull(builder.getSelectedObject());
    builder.selectExistingObject(object);
    assertEquals(object, builder.getSelectedObject());
  }

  @Test
  public void resizeObject_selectedObject_resizesCorrectly() {
    builder.selectExistingObject(object);

    double newX = 120.0;
    double newY = 80.0;
    double newWidth = 40.0;
    double newHeight = 60.0;

    builder.resizeObject(newX, newY, newWidth, newHeight);

    Transform t = object.getComponent(Transform.class);

    assertEquals(newX, t.getX());
    assertEquals(newY, t.getY());
    assertEquals(newWidth, t.getScaleX());
    assertEquals(newHeight, t.getScaleY());
  }


  @Test
  public void objectIsSelected_returnsCorrectStatus() {
    assertFalse(builder.objectIsSelected());
    builder.selectExistingObject(object);
    assertTrue(builder.objectIsSelected());
    builder.deselect();
    assertFalse(builder.objectIsSelected());
  }

  @Test
  public void getCurrentScene_returnsCorrectScene() {
    assertNotNull(builder.getCurrentScene());
    assertEquals("test", builder.getCurrentScene().getName());
  }

  @Test
  public void pushAction_pushesCorrectAction() throws Exception {
    BuilderMoveObjectAction action = new BuilderMoveObjectAction(object, 0, 0, 10, 10);
    builder.pushAction(action);

    Field undoStackField = Builder.class.getDeclaredField("undoStack");
    undoStackField.setAccessible(true);
    @SuppressWarnings("unchecked")
    Deque<EditorAction> undoStack = (Deque<EditorAction>) undoStackField.get(builder);

    assertFalse(undoStack.isEmpty(), "Undo stack should contain the pushed action");
    assertSame(action, undoStack.peek(), "The pushed action should be on top of the undo stack");
  }

  @Test
  public void redoLastAction_moveObject_redoesMove() {
    builder.selectExistingObject(object);

    double newX = 150;
    double newY = 250;

    // Place the object at a new position (records the action)
    builder.placeObject(newX, newY);

    // Undo the move (restores old position)
    builder.undoLastAction();

    // Redo the move (should go back to new position)
    builder.redoLastAction();

    assertEquals(newX, object.getComponent(Transform.class).getX());
    assertEquals(newY, object.getComponent(Transform.class).getY());
  }

  @Test
  public void moveObject_updatesTransformPosition() {
    builder.selectExistingObject(object);
    builder.moveObject(20, 30);
    Transform t = object.getComponent(Transform.class);
    assertEquals(20, t.getX());
    assertEquals(30, t.getY());
  }

  @Test
  public void isSaved_returnsFalseInitially() {
    assertFalse(builder.isSaved());
  }

  @Test
  public void setCurrentScene_setsCorrectScene() {
    GameScene newScene = new GameScene("newScene");
    game.addScene(newScene);
    builder.setCurrentScene(newScene);

    assertEquals("newScene", builder.getCurrentScene().getName());
  }

  @Test
  public void undoLastAction_resizeObject_restoresFromState() {
    GameObject obj = new GameObject("ResizableObject");
    obj.addComponent(Transform.class);
    scene.registerObject(obj);
    Transform t = obj.getComponent(Transform.class);
    t.setX(10);
    t.setY(20);
    t.setScaleX(30);
    t.setScaleY(40);

    TransformState fromState = new TransformState(10, 20, 30, 40);
    TransformState toState = new TransformState(50, 60, 70, 80);

    BuilderResizeObjectAction resizeAction = new BuilderResizeObjectAction(obj, fromState, toState);
    UndoRedoManager.setCurrentScene(scene);
    UndoRedoManager.addAction(resizeAction);
    UndoRedoManager.undo(scene);

    assertEquals(10, t.getX(), "X should be restored to original after undo");
    assertEquals(20, t.getY(), "Y should be restored to original after undo");
    assertEquals(30, t.getScaleX(), "Width should be restored to original after undo");
    assertEquals(40, t.getScaleY(), "Height should be restored to original after undo");
  }

  @Test
  public void redoLastAction_resizeObject_appliesToState() {
    GameObject obj = new GameObject("ResizableObject");
    obj.addComponent(Transform.class);
    scene.registerObject(obj);
    Transform t = obj.getComponent(Transform.class);
    t.setX(10);
    t.setY(20);
    t.setScaleX(30);
    t.setScaleY(40);

    TransformState fromState = new TransformState(10, 20, 30, 40);
    TransformState toState = new TransformState(50, 60, 70, 80);

    BuilderResizeObjectAction resizeAction = new BuilderResizeObjectAction(obj, fromState, toState);
    UndoRedoManager.setCurrentScene(scene);
    UndoRedoManager.addAction(resizeAction);
    UndoRedoManager.undo(scene);
    UndoRedoManager.redo(scene);

    assertEquals(50, t.getX(), "X should be set to new value after redo");
    assertEquals(60, t.getY(), "Y should be set to new value after redo");
    assertEquals(70, t.getScaleX(), "Width should be set to new value after redo");
    assertEquals(80, t.getScaleY(), "Height should be set to new value after redo");
  }

  @Test
  public void saveGameAs_noGameLoaded_throwsException() throws Exception {
    Field gameField = Builder.class.getDeclaredField("game");
    gameField.setAccessible(true);
    gameField.set(builder, null);

    assertThrows(SaveGameException.class, () -> builder.saveGameAs("test.json"));
  }

  @Test
  public void selectExistingObject_withOnChange_callbackInvoked() {
    final boolean[] changed = {false};
    builder.setOnChange((oldObj, newObj) -> changed[0] = true);

    builder.selectExistingObject(object);

    assertTrue(changed[0], "OnChange callback should be triggered when selecting an object");
  }

  @Test
  public void deleteSelectedObject_nullSelectedObject_doesNothing() {
    builder.deselect();
    assertDoesNotThrow(() -> builder.deleteSelectedObject(), "Deleting with no selected object should not throw");
  }
}
