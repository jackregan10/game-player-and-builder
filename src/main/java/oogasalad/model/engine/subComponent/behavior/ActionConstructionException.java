package oogasalad.model.engine.subComponent.behavior;

/**
 * This exception is thrown when there is an error in constructing an action.
 */

public class ActionConstructionException extends RuntimeException {

  /**
   * Constructor for ActionConstructionException.
   *
   * @param message the error message
   */
  public ActionConstructionException(String message) {
    super(message);
  }

  /**
   * Constructor for ActionConstructionException with a cause.
   *
   * @param message the error message
   * @param cause   the cause of the exception
   */
  public ActionConstructionException(String message, Throwable cause) {
    super(message, cause);
  }

}
