package oogasalad.model.serialization.serializer;

/**
 * This exception is thrown when there is an error in serializing or deserializing.
 *
 * @author Hsuan-Kai Liao
 */
public class SerializerException extends RuntimeException {

  /**
   * Constructor for TypeReferenceException.
   * @param message the error message
   */
  public SerializerException(String message) {
    super(message);
  }

  /**
   * Constructor for GetSerializedFieldException with a cause.
   * @param message the error message
   * @param cause the cause of the exception
   */
  public SerializerException(String message, Throwable cause) {
    super(message, cause);
  }

}
