package oogasalad.model.engine.subComponent.behavior;

import oogasalad.model.serialization.serializable.Serializable;

/**
 * The BehaviorAction class is the base class for all behavior actions. the actions will be called
 * only under certain conditions.
 *
 * @param <T> the type of the parameter that the action will take as input parameter
 * @author Hsuan-Kai Liao
 */
public abstract class BehaviorAction<T> extends BehaviorComponent<T> implements Serializable {

  /**
   * This method is called to perform the action
   *
   * @param parameter the parameter for the action
   */
  @SuppressWarnings("unchecked")
  public final void onPerform(Object parameter) {
    if (parameter == null) {
      perform(defaultParameter());
    } else {
      perform((T) parameter);
    }
  }

  /**
   * Performs the action
   *
   * @param parameter the parameter for the action
   */
  protected abstract void perform(T parameter);
}
