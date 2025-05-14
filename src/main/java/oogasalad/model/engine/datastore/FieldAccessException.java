package oogasalad.model.engine.datastore;

/**
 * Custom exception to be thrown when data store cannot access a field
 *
 * @author Logan Dracos
 */
public class FieldAccessException extends RuntimeException {

  /**
   * Constructor for Field Access Exception
   *
   * @param message - Message to be shown in error
   */
  public FieldAccessException(String message) {
    super(message);
  }

  /**
   * Constructor for Field Access Exception
   *
   * @param message - Message to be displayed with error
   * @param cause - the error being thrown
   */
  public FieldAccessException(String message, Throwable cause) {
    super(message, cause);
  }
}
