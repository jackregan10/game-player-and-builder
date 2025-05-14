package oogasalad.model.engine.subComponent.behavior;

import java.util.ArrayList;
import java.util.List;
import oogasalad.model.serialization.serializable.Serializable;
import oogasalad.model.serialization.serializable.SerializableField;
import oogasalad.model.engine.component.BehaviorController;

/**
 * Game Behavior is a special component that aims to be inherited and implement specific object's
 * behavior. Normally, like playerController should be one of the game behavior.
 *
 * @author Hsuan-Kai Liao
 */
public class Behavior implements Serializable {

  @SerializableField
  private String name;
  @SerializableField
  private List<BehaviorConstraint<?>> constraints = new ArrayList<>();
  @SerializableField
  private List<BehaviorAction<?>> actions = new ArrayList<>();

  private BehaviorController controller;

  /**
   * Constructor of the Behavior class. This is used to create a new behavior object
   */
  public Behavior(String name) {
    this.name = name;
  }

  /**
   * Gets the name for the behavior
   *
   * @return String being behavior name
   */
  public String getName() {
    return name;
  }

  /**
   * Get the controller of the behavior. This is used to get the controller that the behavior
   */
  public BehaviorController getController() {
    return controller;
  }

  /**
   * Sets the controller of the behavior
   *
   * @param controller the controller to set
   */
  public void setBehaviorController(BehaviorController controller) {
    this.controller = controller;
  }

  /**
   * Execute the behavior. This method checks all the constraints and performs the actions if all
   * the constraints are met.
   */
  public void execute() {
    for (BehaviorConstraint<?> constraint : constraints) {
      if (!constraint.onCheck(constraint.getParameter())) {
        return;
      }
    }

    for (BehaviorAction<?> action : actions) {
      action.onPerform(action.getParameter());
    }
  }

  /**
   * Awake the behavior. This method is used to awake the behavior and all the constraints and
   * actions.
   */
  public void awake() {
    for (BehaviorConstraint<?> constraint : constraints) {
      constraint.setBehavior(this);
      constraint.awake();
    }
    for (BehaviorAction<?> action : actions) {
      action.setBehavior(this);
      action.awake();
    }
  }

  /**
   * @return - return all actions
   */
  public List<BehaviorAction<?>> getActions() {
    return actions;
  }
}

