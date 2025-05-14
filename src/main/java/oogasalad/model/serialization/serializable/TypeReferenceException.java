package oogasalad.model.serialization.serializable;

/**
 * This exception is thrown when there is an error in creating a new TypeRef without specifying the
 * generic type parameter <?>.
 *
 * @author Hsuan-Kai Liao
 */
public class TypeReferenceException extends RuntimeException {

  /**
   * Constructor for TypeReferenceException.
   * @param message the error message
   */
  public TypeReferenceException(String message) {
    super(message);
  }

  /**
   * Constructor for GetSerializedFieldException with a cause.
   * @param message the error message
   * @param cause the cause of the exception
   */
  public TypeReferenceException(String message, Throwable cause) {
    super(message, cause);
  }

}
