package oogasalad.model.engine.architecture;

/**
 * This exception is thrown when a copy of a GameComponent fails.
 */

public class FailedCopyException extends RuntimeException {

  /**
   * Constructs a new FailedCopyException with the specified detail message.
   *
   * @param message the detail message
   */
  public FailedCopyException(String message) {
    super(message);
  }

  /**
   * Constructs a new FailedCopyException with the specified detail message and cause.
   *
   * @param message the detail message
   * @param cause   the cause of the exception
   */
  public FailedCopyException(String message, Throwable cause) {
    super(message, cause);
  }
}
