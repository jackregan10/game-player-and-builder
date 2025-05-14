package oogasalad.model.builder.actions;

import oogasalad.model.builder.EditorAction;
import oogasalad.model.engine.architecture.GameObject;
import oogasalad.model.engine.architecture.GameScene;

/**
 * Class that tracks when GameObjects are unregistered
 *
 * @author Reyan Shariff
 */

public class BuilderDeleteObjectAction implements EditorAction {

  private final GameScene scene;
  private final GameObject object;

  /**
   * Constructor for DeleteObjectAction
   *
   * @param scene   - Game that the object is being placed in
   * @param object - GameObject that is being placed
   */
  public BuilderDeleteObjectAction(GameScene scene, GameObject object) {
    this.scene = scene;
    this.object = object;
  }

  @Override
  public void undo() {
    scene.registerObject(object);
  }

  @Override
  public void redo() {
    scene.unregisterObject(object);
  }
}

