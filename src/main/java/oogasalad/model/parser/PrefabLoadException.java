package oogasalad.model.parser;

/**
 * This exception is thrown when there is an error in loading a prefab.
 */

public class PrefabLoadException extends RuntimeException {

  /**
   * Constructor for PrefabLoadException.
   *
   * @param message the error message
   */
  public PrefabLoadException(String message) {
    super(message);
  }

  /**
   * Constructor for PrefabLoadException with a cause.
   *
   * @param message the error message
   * @param cause   the cause of the exception
   */
  public PrefabLoadException(String message, Throwable cause) {
    super(message, cause);
  }

}
