package oogasalad.model.config;

/**
 * A custom exception indicating a failure during password hashing or verification
 */
public class PasswordHashingException extends Exception {

  /**
   * Create an instance of the exception with a message and a cause
   *
   * @param message - a message indicating failure
   * @param cause   - the throwable cause for the failure
   */
  public PasswordHashingException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * Create an instance of the exception with just a message
   *
   * @param message - the message indicating a failure
   */
  public PasswordHashingException(String message) {
    super(message);
  }
}

