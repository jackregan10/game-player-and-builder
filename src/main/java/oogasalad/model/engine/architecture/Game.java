package oogasalad.model.engine.architecture;

import static oogasalad.model.config.GameConfig.LOGGER;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import oogasalad.controller.ScoreController;
import oogasalad.database.DatabaseException;
import oogasalad.model.config.GameConfig;
import oogasalad.model.engine.datastore.ScriptableDataStore;
import oogasalad.model.parser.GameParser;
import oogasalad.model.parser.GameSceneParser;
import oogasalad.model.parser.ParsingException;
import oogasalad.model.profile.SessionException;
import oogasalad.model.profile.SessionManagement;

/**
 * The Game class is the main entry point for the game engine. It manages the game loop, scene
 * management, and game logic.
 *
 * @author Hsuan-Kai Liao, Christian Bepler
 */
public class Game {

  private final Map<UUID, GameScene> allScenes = new HashMap<>();
  private final Set<Integer> inputKeys = new HashSet<>();
  private final double[] inputMouses = new double[3];
  private Map<String, JsonNode> originalSceneJsonMap = new HashMap<>();
  private final ScoreController scoreController = new ScoreController();
  private final ScriptableDataStore dataStore = new ScriptableDataStore();

  private GameScene currentScene;
  private GameInfo myGameInfo;

  private List<String> levelOrder = new ArrayList<>();
  private int currentLevelIndex = 0;

  /**
   * The main game loop. This method should be called every frame. It updates the current scene and
   * handles any necessary game logic.
   *
   * @param deltaTime The time since the last frame, in seconds.
   */
  public void step(double deltaTime) {
    if (currentScene != null) {
      currentScene.step(deltaTime);
    } else {
      throw new AssertionError(GameConfig.getText("noGameScene"));
    }
  }

  /**
   * Change the current scene to the specified scene
   *
   * @param sceneName The name of the scene to change to
   */
  public void changeScene(String sceneName) {
    GameScene scene = getScene(sceneName);
    if (currentScene != null && !currentScene.equals(scene)) {
      dataStore.saveMarkedKeys();
      currentScene.onDeactivated();
      currentScene = scene;
      dataStore.setScene(currentScene);
      currentScene.onActivated();
      String scoreText = "score";
      if(dataStore.has(scoreText)) {
        checkHighScore(dataStore);
      }
    }
  }

  /**
   * Assumptions: Win and Loss scenes are last two scenes in a game, and score has been saved
   * under an alias "score" (stored in .properties for multiple languages)
   *
   * @param dataStore - Data store for fetching score value
   */
  private void checkHighScore(ScriptableDataStore dataStore) {
    try {
      int lastTwoScenes = 2;
      if (currentLevelIndex < levelOrder.size() && currentLevelIndex >= levelOrder.size() - lastTwoScenes) {
        String scoreText = "score";
        scoreController.handleSavingHighScore(
            SessionManagement.getInstance().getCurrentUser().getUsername(),
            getGameInfo().name(),
            Integer.parseInt(dataStore.get(scoreText).toString()));
      }
    } catch (SessionException e) {
      LOGGER.error(GameConfig.getText("noSuchUser"), e);
    } catch (DatabaseException e) {
      LOGGER.error(GameConfig.getText("databaseError"), e);
    }
  }

  /**
   * Returns the current scene.
   */
  public GameScene getCurrentScene() {
    return currentScene;
  }

  /**
   * Returns the scene based on name.
   */
  public GameScene getScene(String name) {
    return allScenes.values().stream().filter(s -> s.getName().equals(name)).findFirst()
        .orElse(null);
  }

  /**
   * Returns the prefab scene that stores all the prefabricated objects.
   */
  public GameScene getPrefabScene() {
    return getScene(GameScene.PREFAB_SCENE_NAME);
  }

  /**
   * Returns the all the loaded scenes.
   */
  public Map<UUID, GameScene> getAllScenes() {
    return Collections.unmodifiableMap(allScenes);
  }


  /**
   * @return - This game's data storage
   */
  public ScriptableDataStore getDataStore() {
    return dataStore;
  }

  /**
   * Adds a scene to the game.
   */
  public void addScene(GameScene scene) {
    validateScene(scene);
    registerScene(scene);
  }


  // TODO: move this into text
  private void validateScene(GameScene scene) {
    if (allScenes.containsKey(scene.getId())) {
      LOGGER.error(GameConfig.getText("sceneAlreadyExists", scene.getId()));
      throw new IllegalArgumentException(GameConfig.getText("sceneAlreadyExists", scene.getId()));
    }

    if (getScene(GameScene.PREFAB_SCENE_NAME) != null &&
        scene.getName().equals(GameScene.PREFAB_SCENE_NAME)) {
      LOGGER.error(GameConfig.getText("prefabSceneAlreadyExists", scene));
      throw new IllegalArgumentException(GameConfig.getText("prefabSceneAlreadyExists", scene));
    }
  }

  private void registerScene(GameScene scene) {
    allScenes.put(scene.getId(), scene);
    scene.setGame(this);

    if (currentScene == null) {
      currentScene = scene;
      dataStore.setScene(currentScene);
    }

    if (!levelOrder.contains(scene.getName()) &&
        !scene.getName().equals(GameScene.PREFAB_SCENE_NAME)) {
      levelOrder.add(scene.getName());
    }
  }


  /**
   * Removes a scene from the game.
   *
   * @param sceneName The name of the scene to remove
   */
  public void removeScene(String sceneName) {
    for (UUID id : allScenes.keySet()) {
      if (allScenes.get(id).getName().equals(sceneName)) {
        GameScene scene = allScenes.get(id);
        scene.setGame(null);
        allScenes.remove(id);
        levelOrder.remove(sceneName);
        return;
      }
    }
  }

  /**
   * Resets the specified scene to its initial state
   *
   * @param sceneName The name of the scene to reset
   */
  public void resetScene(String sceneName) {
    JsonNode sceneNode = originalSceneJsonMap.get(sceneName);
    if (sceneNode == null) {
      LOGGER.error(GameConfig.getText("resetSceneError", sceneName));
      return;
    }

    try {
      GameScene oldScene = getScene(sceneName);
      allScenes.remove(oldScene.getId());
      oldScene.setGame(null);
      GameScene newScene = new GameSceneParser().parse(sceneNode.deepCopy());
      allScenes.put(newScene.getId(), newScene);
      newScene.setGame(this);
      if (!levelOrder.contains(newScene.getName())) {
        levelOrder.add(newScene.getName());
      }
      LOGGER.info(GameConfig.getText("resetSceneInfo", sceneName));
    } catch (ParsingException e) {
      LOGGER.error(GameConfig.getText("resetSceneError", sceneName));
    }
  }

  /**
   * Called externally when a key is pressed. Note: This method need to be subscribed to the outer
   * input event bus.
   *
   * @param keyCode the key code of the pressed key
   */
  public void keyPressed(int keyCode) {
    inputKeys.add(keyCode);
  }

  /**
   * Called externally when a key is released. Note: This method need to be subscribed to the outer
   * input event bus.
   *
   * @param keyCode the key code of the released key
   */
  public void keyReleased(int keyCode) {
    inputKeys.remove(keyCode);
  }

  /**
   * Called externally for the mouse click state. Note: This method need to be subscribed to the
   * outer input event bus.
   *
   * @param clicked the click state.
   */
  public void mouseClicked(boolean clicked) {
    inputMouses[0] = clicked ? 1 : -1;
  }

  /**
   * Called externally for the mouse position. Note: This method need to be subscribed to the outer
   * input event bus.
   *
   * @param x the cursor position x in the canvas
   * @param y the cursor position y in the canvas
   */
  public void mouseMoved(double x, double y) {
    inputMouses[1] = x;
    inputMouses[2] = y;
  }

  /**
   * Returns a set of all the input keys currently pressed.
   *
   * @return an unmodifiable set of all the input keys currently pressed
   */
  public Set<Integer> getCurrentInputKeys() {
    return Collections.unmodifiableSet(inputKeys);
  }

  /**
   * Returns the input mouses information in the canvas.
   *
   * @return an unmodifiable double list of mouse information
   * @apiNote the first element is clicked state, 1: clicked, -1: unclicked; the second and the
   * third element are the x and y coordinates.
   */
  public List<Double> getInputMouses() {
    List<Double> mouseInfo = new ArrayList<>();
    for (double value : inputMouses) {
      mouseInfo.add(value);
    }
    return Collections.unmodifiableList(mouseInfo);
  }

  /**
   * Setter for the GameInfo
   *
   * @param gameInfo - a class containing the name, author, description, and resolution of a game
   */
  public void setGameInfo(GameInfo gameInfo) {
    myGameInfo = gameInfo;
  }

  /**
   * getter for GameInfo of a Game object
   *
   * @return - a GameInfo object comprised of name, author, description, and resolution
   */
  public GameInfo getGameInfo() {
    return myGameInfo;
  }

  /**
   * Setter for the order of the levels
   *
   * @param levelOrder - a list of strings containing the ordering of levels by name
   */
  public void setLevelOrder(List<String> levelOrder) {
    this.levelOrder = levelOrder;
  }

  /**
   * Method that handles advancing levels -- moves the currentIndex of the level and changes scenes
   */
  public void goToNextLevel() {
    resetScene(levelOrder.get(currentLevelIndex));
    currentLevelIndex++;
    if (currentLevelIndex < levelOrder.size()) {
      changeScene(levelOrder.get(currentLevelIndex));
    }
  }

  /**
   * Getter for the order of the levels
   *
   * @return - a list of strings containing the ordering of levels by name
   */
  public List<String> getLevelOrder() {
    return levelOrder;
  }

  /**
   * Method to go to a new scene based on scene name
   *
   * @param sceneName - the name of the scene that will be switched to
   */
  public void goToScene(String sceneName) {
    int index = levelOrder.indexOf(sceneName);
    if (index != -1) {
      resetScene(levelOrder.get(currentLevelIndex));
      currentLevelIndex = index;
      changeScene(sceneName);
    }
  }

  /**
   * Method to reset the game, including game score. For purpose of try again functionality.
   */
  public void resetGame(){
    dataStore.clear();
    resetScene(levelOrder.get(currentLevelIndex));
    currentLevelIndex= 0;
    changeScene(levelOrder.getFirst());
  }

  /**
   * Sets a map from string of sceneName to their jsonNodes
   *
   * @param map - Map to store all original jsonNodes
   */
  public void setOriginalSceneJsonMap(Map<String, JsonNode> map) {
    originalSceneJsonMap = map;
  }

  /**
   * Create a new Game that has identical attributes to this Game instance.
   */
  @Override
  public Game clone() {
    GameParser parser = new GameParser();
    try {
      ObjectMapper mapper = new ObjectMapper();
      JsonNode node = mapper.readTree(parser.write(this).toString());
      return parser.parse(node);
    } catch (IOException | ParsingException e) {
      throw new FailedCopyException(GameConfig.getText("failedObjectCopy"), e);
    }
  }

}
