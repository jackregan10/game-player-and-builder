package oogasalad.model.engine.architecture;

/**
 * This exception is thrown when a component cannot be added to a game object. It extends the
 * RuntimeException class.
 */

public class ComponentAddException extends RuntimeException {

  /**
   * Constructs a new ComponentAddException with the specified detail message.
   *
   * @param message the detail message
   */
  public ComponentAddException(String message) {
    super(message);
  }

  /**
   * Constructs a new ComponentAddException with the specified detail message and cause.
   *
   * @param message the detail message
   * @param cause   the cause of the exception
   */
  public ComponentAddException(String message, Throwable cause) {
    super(message, cause);
  }
}
