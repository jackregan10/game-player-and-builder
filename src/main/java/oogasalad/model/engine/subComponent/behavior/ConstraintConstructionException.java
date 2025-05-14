package oogasalad.model.engine.subComponent.behavior;

/**
 * This exception is thrown when there is an error in constructing a constraint.
 */

public class ConstraintConstructionException extends RuntimeException {

  /**
   * Constructor for ConstraintConstructionException.
   *
   * @param message the error message
   */
  public ConstraintConstructionException(String message) {
    super(message);
  }

  /**
   * Constructor for ConstraintConstructionException with a cause.
   *
   * @param message the error message
   * @param cause   the cause of the exception
   */
  public ConstraintConstructionException(String message, Throwable cause) {
    super(message, cause);
  }
}
