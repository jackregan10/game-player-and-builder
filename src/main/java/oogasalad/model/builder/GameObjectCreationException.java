package oogasalad.model.builder;

/**
 * Exception thrown when a GameObject cannot be created.
 */

public class GameObjectCreationException extends RuntimeException {

  /**
   * Constructor for GameObjectCreationException.
   *
   * @param message the error message
   */
  public GameObjectCreationException(String message) {
    super(message);
  }

  /**
   * Constructor for GameObjectCreationException with a cause.
   *
   * @param message the error message
   * @param cause   the cause of the exception
   */
  public GameObjectCreationException(String message, Throwable cause) {
    super(message, cause);
  }

}
