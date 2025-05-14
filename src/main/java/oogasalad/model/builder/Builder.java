package oogasalad.model.builder;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Deque;
import java.util.ArrayDeque;
import oogasalad.model.builder.StateMonitors.TransformState;
import java.util.function.BiConsumer;
import oogasalad.model.builder.actions.BuilderDeleteObjectAction;
import oogasalad.model.builder.actions.BuilderMoveObjectAction;
import oogasalad.model.builder.actions.BuilderResizeObjectAction;
import oogasalad.model.config.GameConfig;
import oogasalad.model.engine.architecture.Game;
import oogasalad.model.engine.architecture.GameObject;
import oogasalad.model.engine.architecture.GameScene;
import oogasalad.model.engine.component.Transform;
import oogasalad.model.parser.JsonParser;
import oogasalad.model.parser.Parser;
import oogasalad.model.parser.ParsingException;
import oogasalad.model.resource.ResourceIO;
import oogasalad.model.resource.ResourcePath;

/**
 * Builder API that manages drag/drop and delete functions of the Editor UI
 * @author Reyan Shariff
 */

public class Builder {
  private GameObject selectedObject;
  private Game game;
  private boolean fileSaved = false;
  private GameScene currentScene;
  private double selectedObjectPrevX;
  private double selectedObjectPrevY;

  private final Deque<EditorAction> undoStack = new ArrayDeque<>();
  private final Deque<EditorAction> redoStack = new ArrayDeque<>();

  private BiConsumer<GameObject, GameObject> onChange;

  /**
   * Set the callback for the event when the object selection is changed
   * @param onChange the callback to subscribed
   */
  public void setOnChange(BiConsumer<GameObject, GameObject> onChange) {
    this.onChange = onChange;
  }

  /**
   * Allows for actions to be pushed into the undo deque outside of Builder.
   */
  public void pushAction(EditorAction action) {
    undoStack.push(action);
  }

  /**
   * Undoes Last User Action
   */
  public void undoLastAction() {
    if (!undoStack.isEmpty()) {
      EditorAction action = undoStack.pop();
      action.undo();
      redoStack.push(action);
    }
  }

  /**
   * Redoes Last User Action
   */
  public void redoLastAction() {
    if (!redoStack.isEmpty()) {
      EditorAction action = redoStack.pop();
      action.redo();
      undoStack.push(action);
    }
  }

  /**
   * Checks if the file was saved
   */
  public boolean isSaved() {
    return fileSaved;
  }

  /**
   * Records when a game object has been selected to be dragged and dropped on the UI
   */
  public void selectExistingObject(GameObject object)
  {
    if (object.hasComponent(Transform.class))
    {
      selectedObjectPrevX = object.getComponent(Transform.class).getX();
      selectedObjectPrevY = object.getComponent(Transform.class).getY();
    }

    if (selectedObject != object) {
      if (onChange != null) {
        onChange.accept(selectedObject, object);
      }
    }

    selectedObject = object;
  }

  /**
   * Sets selectedObject pointer to null
   * */
  public void deselect()
  {
    if (onChange != null) {
      onChange.accept(selectedObject, null);
    }

    selectedObject = null;
  }

  /**
   *  Stops the preview if the user lifts mouse and cursor is not on the editor screen.
   *  Game object should be instantiated after mouse is released
   */
  public void placeObject(double x, double y) {
    if (selectedObject != null && selectedObject.hasComponent(Transform.class)) {
      UndoRedoManager.addAction(new BuilderMoveObjectAction(selectedObject, selectedObjectPrevX, selectedObjectPrevY, x, y));
     // undoStack.push(new BuilderMoveObjectAction(selectedObject, selectedObjectPrevX, selectedObjectPrevY, x, y));
      selectedObject.getComponent(Transform.class).setX(x);
      selectedObject.getComponent(Transform.class).setY(y);
    }
  //  selectedObject = null; //should I add exception?
  }

  /**
   *  Checks if the user is currently dragging around a game object
   */
  public boolean objectIsSelected() {
    return selectedObject != null;
  }

  /**
   * Tracks coordinates of the object as its dragged
   * @param x tracks the x position of the object
   * @param y tracks the y position of the object
   * */
  public void moveObject(double x, double y)
  {
    if (selectedObject != null && selectedObject.hasComponent(Transform.class))
    {
      selectedObject.getComponent(Transform.class).setX(x);
      selectedObject.getComponent(Transform.class).setY(y);
    }
  }

  /**
   *  Deletes selected game object from the screen
   */
  public void deleteSelectedObject() {
    if (selectedObject != null)
    {
      currentScene.unregisterObject(selectedObject);
      UndoRedoManager.addAction(new BuilderDeleteObjectAction(getCurrentScene(), selectedObject));
      //undoStack.push(new BuilderDeleteObjectAction(getCurrentScene(), selectedObject));
      deselect();
    }
  }

  /**
   *  Resizes the selected object
   * @param x - x position
   * @param y  - y position
   * @param h - height
   * @param w - width
   */
  public void resizeObject(double x, double y, double w, double h) {
    if (selectedObject != null && selectedObject.hasComponent(Transform.class)) {
      Transform t = selectedObject.getComponent(Transform.class);
      TransformState prev = new TransformState(t.getX(), t.getY(), t.getScaleX(), t.getScaleY());
      TransformState next = new TransformState(x, y, w, h);
      UndoRedoManager.addAction(new BuilderResizeObjectAction(selectedObject, prev, next));
     // undoStack.push(new BuilderResizeObjectAction(selectedObject, prev, next));
      t.setX(x);
      t.setY(y);
      t.setScaleX(w);
      t.setScaleY(h);
    }
  }


  /**
   *  Returns the currently selected object
   */
  public GameObject getSelectedObject()
  {
    return selectedObject;
  }

  /**
   *  Returns the current scene
   */

  public GameScene getCurrentScene()
  {
    return currentScene;
  }

  /**
   *  Returns the game being edited.
   */
  public Game getGame() {
    return game;
  }

  /**
   * Save the currently loaded Game object as a JSON file using the JsonParser
   *
   * @param filepath location of JSON file
   */
  public void saveGameAs(String filepath) {
    if (game == null) {
      throw new SaveGameException(GameConfig.getText("noSuchGameToSave"));
    }

    try {
      String jsonPath = ResourceIO.createGameFileDirectoryFromJsonPath(filepath);
      JsonParser parser = new JsonParser(jsonPath);
      String parentDirectory = Paths.get(jsonPath).getParent().toString();
      ResourcePath.setToContext(parentDirectory, ResourcePath.PathType.PACKED);
      parser.write(game);
    } catch (IOException e) {
      throw new SaveGameException(GameConfig.getText("errorSavingGame"), e);
    } finally {
      ResourcePath.cleanToContext();
    }

    fileSaved = true;
  }

  /**
   * Sets the current Scene
   */
  public void setCurrentScene(GameScene currentScene)
  {
    this.currentScene = currentScene;
    game.changeScene(currentScene.getName());
    selectedObject = null;
  }

  /**
   * Load a new Game into the Builder via a JSON file node
   */
  public void loadGame(String filepath) {
    try {
      Parser<?> parser = new JsonParser(filepath);
      ObjectMapper mapper = new ObjectMapper();
      JsonNode newNode = mapper.createObjectNode();
      game = (Game) parser.parse(newNode);
    } catch (ParsingException e) {
      throw new IllegalStateException(GameConfig.getText("errorParsingGame"), e);
    }

    currentScene = game.getCurrentScene();
  }

  /**
   * Adds a new scene
   * @param sceneName - Name user selects
   */
  public void addScene(String sceneName)
  {
    GameScene newScene = new GameScene(sceneName);
    game.getLevelOrder().add(sceneName);
    game.addScene(newScene);
    selectedObject = null;
  }
}
