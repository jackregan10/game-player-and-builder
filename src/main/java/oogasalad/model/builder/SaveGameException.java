package oogasalad.model.builder;

/**
 * This exception is thrown when there is an error saving a game. It extends the RuntimeException
 * class.
 */

public class SaveGameException extends RuntimeException {

  /**
   * Constructs a new SaveGameException with the specified detail message.
   *
   * @param message the detail message
   */
  public SaveGameException(String message) {
    super(message);
  }

  /**
   * Constructs a new SaveGameException with the specified detail message and cause.
   *
   * @param message the detail message
   * @param cause   the cause of the exception
   */
  public SaveGameException(String message, Throwable cause) {
    super(message, cause);
  }
}
