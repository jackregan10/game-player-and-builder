package oogasalad.model.engine.component;

import java.util.ArrayList;
import java.util.List;
import oogasalad.model.engine.architecture.GameComponent;
import oogasalad.model.engine.subComponent.behavior.Behavior;
import oogasalad.model.serialization.serializable.SerializableField;

/**
 * The BehaviorController class is a component that manages the behaviors of a game object. It is
 * responsible for executing the behaviors and managing the constraints and actions associated with
 * each behavior.
 *
 * @author Hsuan-Kai Liao
 */
public final class BehaviorController extends GameComponent {

  @Override
  public ComponentTag componentTag() {
    return ComponentTag.BEHAVIOR;
  }

  @SerializableField
  private List<Behavior> behaviors = new ArrayList<>();

  @Override
  protected void awake() {
    for (Behavior behavior : behaviors) {
            behavior.setBehaviorController(this);
      behavior.awake();
    }
  }

  @Override
  protected void update(double deltaTime) {
    for (Behavior behavior : behaviors) {
      behavior.execute();
    }
  }

  /**
   * @return - List of all behaviors under controller
   */
  public List<Behavior> getBehaviors() {
    return behaviors;
  }
}
