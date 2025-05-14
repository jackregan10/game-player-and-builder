package oogasalad.model.profile;

/**
 * Custom Exception class to display errors when handling a session
 */
public class SessionException extends Exception {

  /**
   * Exception thrown when attempting to access the current session and it fails
   */
  public SessionException(String message) {
    super(message);
  }

}
