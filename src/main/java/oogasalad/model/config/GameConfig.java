package oogasalad.model.config;

import java.text.MessageFormat;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import oogasalad.view.scene.MainViewManager;
import oogasalad.view.scene.auth.LogInScene;
import oogasalad.view.scene.auth.SignUpScene;
import oogasalad.view.scene.builder.BuilderScene;
import oogasalad.view.scene.display.GameDisplayScene;
import oogasalad.view.scene.leaderboard.LeaderboardScene;
import oogasalad.view.scene.menu.GameMenuScene;
import oogasalad.view.scene.menu.MainMenuScene;
import oogasalad.view.scene.profile.EditProfileScene;
import oogasalad.view.scene.profile.FollowScene;
import oogasalad.view.scene.profile.ProfileScene;
import oogasalad.view.scene.profile.SearchProfileScene;
import oogasalad.view.scene.profile.SocialHubScene;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Main configurations for the Game and program
 *
 * @author Justin Aronwald
 */
public class GameConfig {

  public static final Logger LOGGER = LogManager.getLogger(); // The logger for this program
  private static final String DEFAULT_LANGUAGE = "english";
  private static final String LANGUAGE_FILE_PREFIX = "oogasalad.languages.";
  private static final String KEY_NOT_FOUND_ID = "keyNotFound";
  private static ResourceBundle myMessages =
      ResourceBundle.getBundle(LANGUAGE_FILE_PREFIX + DEFAULT_LANGUAGE);

  public static final String DEFAULT_PFP_URL = "https://firebasestorage.googleapis.com/v0/b/oogasalad-a908c.firebasestorage.app/o/profile_photos%2FdefaultPfp.png?alt=media&token=be6ed26d-2eaa-411e-9e43-43311f197a56";

  /**
   * A method to switch the language to a new language
   *
   * @param language - the new language that is being swapped
   */
  public static void setLanguage(String language) {
    try {
      MainViewManager viewManager = MainViewManager.getInstance();
      myMessages = ResourceBundle.getBundle(LANGUAGE_FILE_PREFIX + language.toLowerCase());
      renderScenes(viewManager);
      viewManager.switchToMainMenu();
    } catch (MissingResourceException e) {
      LOGGER.warn(getText("languageFileNotFound", language));
      myMessages = ResourceBundle.getBundle(LANGUAGE_FILE_PREFIX + DEFAULT_LANGUAGE);
    }
  }

  /**
   * A renderer that runs if the languages are changes and also runs initially to initiate the
   * program.
   *
   * @param viewManager The main view manager to add viewScenes to.
   */
  public static void renderScenes(MainViewManager viewManager) {
    viewManager.addViewScene(MainMenuScene.class, GameConfig.getText("defaultScene"));
    viewManager.addViewScene(SocialHubScene.class, "SocialHubScene");
    viewManager.addViewScene(ProfileScene.class, "ProfileScene");
    viewManager.addViewScene(GameDisplayScene.class, "GamePlayerScene");
    viewManager.addViewScene(EditProfileScene.class, "EditProfileScene");
    viewManager.addViewScene(LogInScene.class, "LogInScene");
    viewManager.addViewScene(SignUpScene.class, "SignUpScene");
    viewManager.addViewScene(FollowScene.class, "FollowScene");
    viewManager.addViewScene(GameDisplayScene.class, "GameDisplayScene");
    viewManager.addViewScene(BuilderScene.class, "BuilderScene");
    viewManager.addViewScene(GameDisplayScene.class, "GamePreviewScene");
    viewManager.addViewScene(SearchProfileScene.class, "SearchProfileScene");
    viewManager.addViewScene(GameMenuScene.class, "GameMenuScene");
    viewManager.addViewScene(LeaderboardScene.class, "LeaderboardScene");
  }

  /**
   * A getter to use the language properties file message
   *
   * @param key - the message you want that is stored in the properties file
   * @return - the value of the message you wish to display/log
   */
  public static String getText(String key, Object... args) {
    try {
      String rawMessage = myMessages.getString(key);
      return MessageFormat.format(rawMessage, args);
    } catch (MissingResourceException e) {
      LOGGER.warn(getText(KEY_NOT_FOUND_ID, key));
    }
    return key;
  }

  /**
   * A getter to use the language properties file message that have number values
   *
   * @param key - the message you want that is stored in the properties file
   * @return - the value of the message you wish to display/log
   */
  public static Double getNumber(String key) {
    try {
      String rawMessage = myMessages.getString(key);
      return Double.parseDouble(rawMessage);
    } catch (MissingResourceException e) {
      LOGGER.warn(getText(KEY_NOT_FOUND_ID, key));
    } catch (NumberFormatException e) {
      LOGGER.warn(getText("keyIncompatibleType"));
    }
    return 0.0;
  }

  /**
   * A getter to use the language properties file message that have list of string values
   *
   * @param key - the message you want that is stored in the properties file
   * @return - the value of the message you wish to display/log
   */
  public static List<String> getTextList(String key) {
    try {
      String rawMessage = myMessages.getString(key);
      return List.of(rawMessage.split(","));
    } catch (MissingResourceException e) {
      LOGGER.warn(getText(KEY_NOT_FOUND_ID, key));
    }
    return List.of();
  }

}
