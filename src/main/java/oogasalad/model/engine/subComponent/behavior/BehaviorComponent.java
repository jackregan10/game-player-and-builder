package oogasalad.model.engine.subComponent.behavior;

import java.util.Set;
import oogasalad.model.config.GameConfig;
import oogasalad.model.engine.architecture.GameComponent;
import oogasalad.model.serialization.serializable.Serializable;
import oogasalad.model.serialization.serializable.SerializableField;

/**
 * Abstract base class for behavior components that use a parameter and reference a parent
 * behavior.
 *
 * @param <T> the type of parameter this component handles
 * @author Hsuan-Kai Liao
 */
public abstract class BehaviorComponent<T> implements Serializable {

  @SerializableField
  private T parameter = defaultParameter();

  private Behavior behavior;

  /**
   * Set the behavior that this component belongs to.
   */
  public final void setBehavior(Behavior behavior) {
    this.behavior = behavior;
  }

  /**
   * Get the behavior this component belongs to.
   */
  public final Behavior getBehavior() {
    return behavior;
  }

  /**
   * Get the parameter of the component.
   */
  public final T getParameter() {
    return parameter;
  }

  /**
   * Set the parameter of the component.
   */
  public final void setParameter(T parameter) {
    this.parameter = parameter;
  }

  /**
   * Retrieve a game component from the controller.
   */
  public final <U extends GameComponent> U getComponent(Class<U> componentClass) {
    if (requiredComponents().contains(componentClass)) {
      return behavior.getController().getComponent(componentClass);
    }
    throw new IllegalArgumentException(GameConfig.getText("notDeclaredRequiredComponents", componentClass.getSimpleName()));
  }

  /**
   * This method is called to get the default parameter.
   *
   * @return the default parameter
   */
  protected abstract T defaultParameter();

  /**
   * Return a list of required GameComponent classes that this behavior component depends on.
   *
   * @return list of GameComponent class types
   */
  public abstract Set<Class<? extends GameComponent>> requiredComponents();

  /**
   * Method to initialize component references.
   */
  protected void awake() {
    // NOTE: This should be override if needed.
  }
}
