package oogasalad.model.serialization.serializable;

/**
 * This exception is thrown when there is an error in setting a serialized field.
 */

public class SetSerializedFieldException extends RuntimeException {

  /**
   * Constructor for SetSerializedFieldException.
   *
   * @param message the error message
   */
  public SetSerializedFieldException(String message) {
    super(message);
  }

  /**
   * Constructor for SetSerializedFieldException with a cause.
   *
   * @param message the error message
   * @param cause   the cause of the exception
   */
  public SetSerializedFieldException(String message, Throwable cause) {
    super(message, cause);
  }

}
