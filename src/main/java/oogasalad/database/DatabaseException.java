package oogasalad.database;

/**
 * Exception created for when working with the database
 *
 * @author Justin Aronwald
 */
public class DatabaseException extends Exception {

  /**
   * Constructor for exception with only message
   *
   * @param message - the message for the error
   */
  public DatabaseException(String message) {
    super(message);
  }

  /**
   * Constructor for the exception with both a message and the throwable
   *
   * @param message - the message explaining the error
   * @param cause   - the actual throwable for the error
   */
  public DatabaseException(String message, Throwable cause) {
    super(message, cause);
  }
}
