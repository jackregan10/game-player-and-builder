package oogasalad.model.builder;

/**
 * This exception is thrown when there is an error loading a game. It extends the RuntimeException
 * class.
 */

public class LoadGameException extends RuntimeException {

  /**
   * Constructs a new loadGameException with the specified detail message.
   *
   * @param message the detail message
   */
  public LoadGameException(String message) {
    super(message);
  }

  /**
   * Constructs a new loadGameException with the specified detail message and cause.
   *
   * @param message the detail message
   * @param cause   the cause of the exception
   */
  public LoadGameException(String message, Throwable cause) {
    super(message, cause);
  }

}
