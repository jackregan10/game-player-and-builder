package oogasalad.model.engine.subComponent.behavior.action;

import java.util.Set;
import oogasalad.model.engine.architecture.GameComponent;
import oogasalad.model.engine.subComponent.behavior.BehaviorAction;
import oogasalad.view.scene.MainViewManager;

/**
 * ChangeViewSceneAction is a class that extends BehaviorAction and is used to change the scene.
 *
 * @author Hsuan-Kai Liao
 */
public class ChangeViewSceneAction extends BehaviorAction<Void> {

  private MainViewManager mainViewManager;

  @Override
  protected Void defaultParameter() {
    return null;
  }

  @Override
  public Set<Class<? extends GameComponent>> requiredComponents() {
    return Set.of();
  }

  @Override
  protected void awake() {
    mainViewManager = MainViewManager.getInstance();
  }

  @Override
  protected void perform(Void unused) {
    mainViewManager.switchToPrevScene();
  }
}
