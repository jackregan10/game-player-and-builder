package oogasalad.model.engine.datastore;

/**
 * Exception to be thrown when data store does not have requested key stored.
 *
 *  @author Logan Dracos
 */
public class MissingKeyException extends RuntimeException {


  /**
   * Constructor for Missing Key Exception
   *
   * @param message - Message to be displayed with error
   */
  public MissingKeyException(String message) {
    super(message);
  }

  /**
   * Constructor for Missing Key Exception
   *
   * @param message - Message to be displayed with error
   * @param cause - the error being thrown
   */
  public MissingKeyException(String message, Throwable cause) {
    super(message, cause);
  }
}
