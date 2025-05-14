package oogasalad.model.serialization.serializable;

/**
 * This exception is thrown when there is an error in getting a serialized field.
 */

public class GetSerializedFieldException extends RuntimeException {

  /**
   * Constructor for GetSerializedFieldException.
   *
   * @param message the error message
   */
  public GetSerializedFieldException(String message) {
    super(message);
  }

  /**
   * Constructor for GetSerializedFieldException with a cause.
   *
   * @param message the error message
   * @param cause   the cause of the exception
   */
  public GetSerializedFieldException(String message, Throwable cause) {
    super(message, cause);
  }
}
