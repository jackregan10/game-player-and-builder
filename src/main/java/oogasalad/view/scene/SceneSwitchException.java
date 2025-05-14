package oogasalad.view.scene;

/**
 * Custom exception for any issues with scene switching. Especially for the reflection in String
 */
public class SceneSwitchException extends RuntimeException {

  /**
   * Constructor for SceneSwitchException
   *
   * @param message exception messages
   * @param cause   the cause of the exception
   */
  public SceneSwitchException(String message, Throwable cause) {
    super(message, cause);
  }
}
