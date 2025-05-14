package oogasalad.model.profile;

import static oogasalad.model.config.GameConfig.LOGGER;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import oogasalad.database.DatabaseException;
import oogasalad.model.config.GameConfig;
import oogasalad.model.service.PlayerService;

/**
 * Handles the authentication of users and stores the current user within a session
 *
 * @author Justin Aronwald
 */
public class SessionManagement {

  //singleton
  private static final SessionManagement instance = new SessionManagement();

  private static final String FILE_PATH = "src/main/resources/oogasalad/auth/rememberme.properties";
  private static final String USERNAME_KEY = "rememberedUser";
  private final PlayerService playerService = new PlayerService();

  private static PlayerData currentUser;

  /**
   * Returns the singleton instance of SessionManagement
   *
   * @return - a sessionManagement instance
   */
  public static SessionManagement getInstance() {
    return instance;
  }

  /**
   * Authentication to start a new session and log in a user
   *
   * @param user - the user wishing to login
   */
  public void login(PlayerData user, boolean rememberMe) throws IOException {
    currentUser = user;

    if (rememberMe) {
      saveRememberedUser(user.getUsername());
    } else {
      clearRememberedUser();
    }
  }

  /**
   * Authentication to end a session and log out a user
   */
  public void logout() {
    currentUser = null;
    clearRememberedUser();
  }

  /**
   * Method to automatically log a user in if he is saved
   *
   * @return - true if a user gets saved automatically
   */
  public boolean tryAutoLogin() {
    String rememberedUsername = loadRememberedUser();
    if (rememberedUsername != null) {
      try {
        currentUser = playerService.getPlayerByUsername(rememberedUsername);
        return true;
      } catch (DatabaseException e) {
        clearRememberedUser();
      }
    }
    return false;
  }


  /**
   * Returns the current user of the session
   *
   * @return - the logged-in user
   * @throws SessionException - if a user is not logged in
   */
  public PlayerData getCurrentUser() throws SessionException {
    if (currentUser == null) {
      throw new SessionException(GameConfig.getText("noSuchUser"));
    }

    return currentUser;
  }

  /**
   * Method determining if a user is logged in
   *
   * @return - true if a user is logged in, otherwise false
   */
  public boolean isLoggedIn() {
    return currentUser != null;
  }

  /**
   * Handles the saving of a user in a properties file so that we can auto-login
   *
   * @param username - the inputted username of a player
   * @throws IOException - true if there's an error with file output stream
   */
  private void saveRememberedUser(String username) throws IOException {
    try {
      Properties prop = new Properties();
      prop.setProperty(USERNAME_KEY, username);
      FileOutputStream fos = new FileOutputStream(FILE_PATH);
      prop.store(fos, "Remember Me Preferences");
      fos.close();
    } catch (IOException e) {
      LOGGER.warn(GameConfig.getText("databaseOutputError"), e);
      throw new IOException(GameConfig.getText("databaseOutputError"), e);
    }
  }


  private String loadRememberedUser() {
    try {
      Properties props = new Properties();
      FileInputStream in = new FileInputStream(FILE_PATH);
      props.load(in);
      in.close();
      return props.getProperty(USERNAME_KEY);
    } catch (IOException e) {
      return null;
    }
  }

  private void clearRememberedUser() {
    File file = new File(FILE_PATH);
    if (file.exists()) {
      if (file.delete()) {
        LOGGER.info(GameConfig.getText("deleteRememberMePreferenceInfo"));
      }
    }
  }

}
