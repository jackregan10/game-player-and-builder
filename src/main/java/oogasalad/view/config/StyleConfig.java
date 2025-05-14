package oogasalad.view.config;

import static oogasalad.model.config.GameConfig.LOGGER;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import javafx.scene.Scene;
import oogasalad.model.config.GameConfig;

/**
 * Configuration class for UI styling.
 *
 */
public class StyleConfig {

  private static final List<Consumer<String>> SCENE_CALLBACK_POOL = new ArrayList<>();
  private static final Set<Scene> SCENE_POOL = new HashSet<>();
  private static final String BASE_THEME = "base";
  private static final String DEFAULT_STYLE = "light";
  private static final String STYLE_FILE_TYPE = ".css";
  private static final String STYLE_FILE_PREFIX = "/oogasalad/stylesheets/";

  private static String currentTheme = DEFAULT_STYLE;
  private static String currentStyleSheet = STYLE_FILE_PREFIX + DEFAULT_STYLE + STYLE_FILE_TYPE;

  /**
   * Register the scene to the style config controller
   */
  public static void registerScene(Scene scene) {
    setTheme(scene, BASE_THEME);
    SCENE_POOL.add(scene);
    setTheme(scene, currentTheme);
  }

  /**
   * Add a callback to be run when the scene changes.
   * @param callback the callback to be run
   */
  public static void registerOnSceneChange(Consumer<String> callback) {
    SCENE_CALLBACK_POOL.add(callback);
  }

  /**
   * Apply a theme to all the registered scenes
   * @param theme the new theme that is being swapped
   */
  public static void setTheme(String theme) {
    for (Scene scene : SCENE_POOL) {
      setTheme(scene, theme);
    }
    currentTheme = theme;
    for (Consumer<String> callback : SCENE_CALLBACK_POOL) {

      callback.accept(currentStyleSheet);
    }
  }

  /**
   * Apply a theme to a specific scene
   * @param theme the new theme that is being swapped
   */
  public static void setTheme(Scene scene, String theme) {
    String stylesheet = STYLE_FILE_PREFIX + theme.toLowerCase() + STYLE_FILE_TYPE;
    scene.getStylesheets().removeIf(s -> !s.endsWith("base.css"));
    if (StyleConfig.class.getResource(stylesheet) == null || StyleConfig.class.getResource(
        stylesheet).toExternalForm() == null) {
      LOGGER.warn(GameConfig.getText("stylesheetNotFound"), theme);
      scene.getStylesheets().add(
          StyleConfig.class.getResource(STYLE_FILE_PREFIX + DEFAULT_STYLE + STYLE_FILE_TYPE)
              .toExternalForm());
    } else {
      scene.getStylesheets().add(StyleConfig.class.getResource(stylesheet).toExternalForm());
      currentStyleSheet = stylesheet;
    }
  }

  /**
   * @return - The current stylesheet string
   */
  public static String getCurrentTheme() {
    return currentTheme;
  }

  /**
   * @return - The base stylesheet
   */
  public static String getBaseTheme() {
    return BASE_THEME;
  }

  /**
   * @return - the base stylesheet for the default style.
   */
  public static String getBaseStyleSheet() {
    return STYLE_FILE_PREFIX + BASE_THEME + STYLE_FILE_TYPE;
  }

  /**
   * Returns a list of URLs representing the currently applied stylesheets. This includes the
   * stylesheets from the provided scene (if not null and not empty) and the current theme's
   * stylesheet as a fallback.
   *
   * @param scene The scene whose stylesheets should be included, or null if none.
   * @return A list of stylesheet URLs.
   */
  public static List<String> getCurrentStyleSheetURLs(Scene scene) {
    List<String> stylesheets = new ArrayList<>();

    if (scene != null && !scene.getStylesheets().isEmpty()) {
      stylesheets.addAll(scene.getStylesheets());
    } else {
      String themePath = StyleConfig.STYLE_FILE_PREFIX + getCurrentTheme().toLowerCase()
          + StyleConfig.STYLE_FILE_TYPE;
      URL resource = StyleConfig.class.getResource(themePath);
      if (resource != null) {
        stylesheets.add(resource.toExternalForm());
      } else {
        LOGGER.error(GameConfig.getText("couldNotLoadFallback"), themePath);
      }
    }
    return stylesheets;
  }
}

