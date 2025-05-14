package oogasalad.view.gui.panel;

/**
 * Exception thrown when an error occurs during the dynamic creation of a {@code GameComponent}.
 * <p>
 * Typically used to wrap reflection-related exceptions such as {@code InstantiationException},
 * {@code IllegalAccessException}, {@code InvocationTargetException}, and {@code NoSuchMethodException}
 * when instantiating components at runtime.
 */
public class ComponentCreationException extends RuntimeException {
  /**
   * Constructs a new {@code ComponentCreationException} with the specified detail message and cause.
   *
   * @param message the detail message explaining the error
   * @param cause the underlying exception that caused the component creation to fail
   */
  public ComponentCreationException(String message, Throwable cause) {
    super(message, cause);
  }
}