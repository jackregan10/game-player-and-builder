package oogasalad.view.scene;


import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.stage.Stage;
import oogasalad.model.resource.ResourceCache;
import org.reflections.Reflections;
import oogasalad.model.config.GameConfig;


/**
 * Handles switching between ViewScenes in the application
 *
 * @author Justin Aronwald
 */
public class MainViewManager {

  private static final String SCENE_PACKAGE = "oogasalad.view.scene";

  private static MainViewManager instance;
  private final Map<String, ViewScene> viewScenes = new HashMap<>();
  private final Deque<ViewScene> prevScenes = new ArrayDeque<>();
  private ViewScene currentScene;
  private boolean skipHistoryPush = false;

  /**
   * Get instance - necessary for view scene switching
   *
   * @return - the main view manager object
   */
  public static MainViewManager getInstance() {
    if (instance == null) {
      instance = new MainViewManager();
    }

    return instance;
  }

  /**
   * Creates and stores a ViewScene instance with the given class and constructor args.
   *
   * @param viewSceneClass The class of the ViewScene
   * @return The created instance
   */
  public <T extends ViewScene> T addViewScene(Class<T> viewSceneClass, String name) {
    try {
      Constructor<T> constructor = viewSceneClass.getDeclaredConstructor(Stage.class);
      constructor.setAccessible(true);
      Stage stage = new Stage();
      T instance = constructor.newInstance(stage);
      viewScenes.put(name, instance);
      return instance;
    } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
      GameConfig.LOGGER.error(GameConfig.getText("errorAddNewViewScene", viewSceneClass.getName()), e);
      // TODO: handle exception here
    }
    return null;
  }

  /**
   * Gets the viewScene instance with the given name.
   */
  public ViewScene getViewScene(String name) {
    return viewScenes.get(name);
  }


  /**
   * Function that uses reflection to either create or switch to a ViewScene
   *
   * @param viewSceneName - Name of view scene to be switched to - must match class name
   */
  public void switchTo(String viewSceneName) {
    try {
      if (!viewScenes.containsKey(viewSceneName)) {
        Reflections reflections = new Reflections(SCENE_PACKAGE);
        List<Class<?>> classes = List.copyOf(reflections.getSubTypesOf(ViewScene.class));
        Class<?> clazz = classes.stream()
            .filter(c -> c.getSimpleName().equals(viewSceneName))
            .findFirst()
            .orElse(null);

        if (clazz != null) {
          ViewScene viewScene = (ViewScene) clazz.getDeclaredConstructor(MainViewManager.class).newInstance(this);
          viewScenes.put(viewSceneName, viewScene);
        }
      }
      switchTo(viewScenes.get(viewSceneName));
    } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException | IllegalArgumentException e) {
      switchToMainMenu();
      throw new SceneSwitchException(GameConfig.getText("noSuchViewScene", viewSceneName), e);
    }
  }

  private void switchTo(ViewScene viewScene) {
    if (currentScene == viewScene) {
      return;
    }

    ResourceCache.clearCache();

    if (currentScene != null) {
      if (skipHistoryPush) {
        skipHistoryPush = false;
      } else {
        prevScenes.push(currentScene);
      }
      currentScene.onDeactivate();
      currentScene.getStage().hide();
    }

    if (viewScene == null) {
      throw new IllegalArgumentException(String.valueOf((Object) null));
    }

    currentScene = viewScene;
    currentScene.getStage().show();
    currentScene.onActivate();
  }


  /**
   * Shortcut to go to the main menu
   */
  public void switchToMainMenu() {
    switchTo(GameConfig.getText("defaultScene"));
  }

  /**
   * Method to switch to the previous scene on the stack
   */
  public void switchToPrevScene() {
    if (prevScenes.isEmpty()) {
      return;
    }
    skipHistoryPush = true;
    switchTo(prevScenes.pop());
  }

  /**
   * @return - Name of current scene
   */
  public String getCurrentSceneName() {
    return viewScenes.entrySet().stream()
        .filter(entry -> entry.getValue().equals(currentScene))
        .map(Map.Entry::getKey)
        .findFirst()
        .orElse(null);
  }
}