package oogasalad.model.builder.actions;

import oogasalad.model.builder.EditorAction;
import oogasalad.model.engine.architecture.GameObject;
import oogasalad.model.engine.architecture.GameScene;

/**
 * Class that tracks when GameObjects are registered
 *
 * @author Reyan Shariff
 */

public class BuilderCreateObjectAction implements EditorAction {

  private GameObject placedObject;
  private GameScene scene;

  /**
   * Constructor for CreateObjectAction
   *
   * @param scene - Game that the object is being placed in
   * @param obj  - GameObject that is being placed
   */
  public BuilderCreateObjectAction(GameScene scene, GameObject obj) {
    this.scene = scene;
    this.placedObject = obj;
  }

  @Override
  public void undo() {
    scene.unregisterObject(placedObject);
  }

  @Override
  public void redo() {
    scene.registerObject(placedObject);
  }
}