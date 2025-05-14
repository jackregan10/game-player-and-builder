package oogasalad.model.engine.architecture;

/**
 * This exception is thrown when a GameObject does not have a parent scene.
 */

public class MissingParentSceneException extends RuntimeException {

  /**
   * Constructor for MissingParentSceneException.
   *
   * @param message the error message
   */
  public MissingParentSceneException(String message) {
    super(message);
  }

  /**
   * Constructor for MissingParentSceneException with a cause.
   *
   * @param message the error message
   * @param cause   the cause of the exception
   */
  public MissingParentSceneException(String message, Throwable cause) {
    super(message, cause);
  }

}
