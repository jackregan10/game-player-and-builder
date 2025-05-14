package oogasalad.model.engine.subComponent.behavior.action;

import java.util.Set;
import java.util.function.BiConsumer;

import oogasalad.model.engine.architecture.GameComponent;
import oogasalad.model.engine.component.TextRenderer;
import oogasalad.model.engine.component.Transform;

/**
 * Generic SetTextAction that accepts any input type and converts it to a string.
 *
 * @param <T> the type of the input parameter (e.g., Transform, Integer)
 *
 * @author Logan Dracos
 */
public abstract class SetTextAction<T> extends SetComponentValueAction<T, TextRenderer> {
  private BiConsumer<TextRenderer, T> setter;

  @Override
  protected void awake() {
    super.awake();
    setter = (textRenderer, value) -> textRenderer.setText(value.toString());
  }


  @Override
  protected  BiConsumer<TextRenderer, T> provideSetter() {
    return setter;
  }

  @Override
  protected TextRenderer supplyComponent() {
    return getComponent(TextRenderer.class);
  }

  /**
   * Converts the input parameter to a String to be displayed.
   *
   * @param value the input value
   * @return string to display
   */
  protected abstract String toText(T value);

  @Override
  public Set<Class<? extends GameComponent>> requiredComponents() {
    return Set.of(
        Transform.class,
        TextRenderer.class
    );
  }}