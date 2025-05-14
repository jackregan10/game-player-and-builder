package oogasalad.model.engine.subComponent.behavior;

import oogasalad.model.serialization.serializable.Serializable;

/**
 * The BehaviorConstraint class is the base class for all behavior action constraints. Constraints
 * are conditions that must be met in order for an BehaviorAction to be triggered.
 *
 * @param <T> the type of the parameter that the constraint will check against
 * @author Hsuan-Kai Liao
 */
public abstract class BehaviorConstraint<T> extends BehaviorComponent<T> implements Serializable {

  /**
   * Check if the constraint is met.
   *
   * @param parameter the parameter to check against
   * @return true if the constraint is met, false otherwise
   */
  @SuppressWarnings("unchecked")
  public final boolean onCheck(Object parameter) {
    if (parameter == null) {
      return check(defaultParameter());
    } else {
      return check((T) parameter);
    }
  }

  /**
   * Check if the constraint is met.
   *
   * @param parameter the parameter to check against
   * @return true if the constraint is met, false otherwise
   */
  protected abstract boolean check(T parameter);
}
