package oogasalad.model.parser;

import static oogasalad.model.config.GameConfig.LOGGER;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import oogasalad.model.config.GameConfig;
import oogasalad.model.engine.architecture.Game;
import oogasalad.model.engine.architecture.GameInfo;
import oogasalad.model.engine.architecture.GameScene;


/**
 * Parses and serializes a Game object to and from a JSON node
 *
 * @author Justin Aronwald, Daniel Rodriguez-Florido
 */
public class GameParser implements Parser<Game> {

  private final ObjectMapper mapper = new ObjectMapper();
  private final GameSceneParser sceneParser = new GameSceneParser();
  private final ResourceParser resourceParser = new ResourceParser();
  private final InformationParser informationParser = new InformationParser();
  private final Map<String, JsonNode> sceneJsonMap = new HashMap<>();

  private static final String DATA = "Data";
  private static final String INFORMATION = "Information";
  private static final String NAME = "Name";
  private static final String SCENE = "Scene";
  private static final String RESOURCES = "Resources";

  private final List<String> levelOrder = new ArrayList<>();


  /**
   * Parses a JSON node into a Game instance
   *
   * @param node - the JSON node given to parse
   * @return - a fully configured GameScene
   * @throws ParsingException - error thrown if reflection or parsing fails
   */
  @Override
  public Game parse(JsonNode node) throws ParsingException {
    if (node == null || !node.has(DATA)) {
      LOGGER.warn(GameConfig.getText("noDataFound"));
      throw new ParsingException(GameConfig.getText("noDataFound"));
    }

    Game newGame = new Game();

    // Information
    JsonNode info = node.get(INFORMATION);
    handleInformationParsing(info, newGame);

    // Data
    JsonNode data = node.get(DATA);
    levelOrder.clear();
    sceneJsonMap.clear();
    handleResourceParsing(data);
    handleSceneParsing(data, newGame);

    // Game JsonStorage
    newGame.setOriginalSceneJsonMap(sceneJsonMap);

    return newGame;
  }

  private void handleInformationParsing(JsonNode info, Game newGame) throws ParsingException {
    GameInfo gameInfo = informationParser.parse(info);
    newGame.setGameInfo(gameInfo);
  }

  private void handleResourceParsing(JsonNode data) throws ParsingException {
    Map<String, String> resourceMap = new HashMap<>();
    if (data.has(RESOURCES)) {
      for (JsonNode resourceNode : data.get(RESOURCES)) {
        Map.Entry<String, String> entry = resourceParser.parse(resourceNode);
        resourceMap.put(entry.getKey(), entry.getValue());
      }
    }
  }

  private void handleSceneParsing(JsonNode data, Game newGame) throws ParsingException {
    if (data.has(SCENE) && data.get(SCENE).isArray()) {
      for (JsonNode sceneNode : data.get(SCENE)) {
        createGameScene(newGame, sceneNode);
      }
    }
  }

  private void createGameScene(Game newGame, JsonNode sceneNode) throws ParsingException {
    GameScene gameScene = sceneParser.parse(sceneNode);
    if (gameScene != null) {
      newGame.addScene(gameScene);
      String sceneName = sceneNode.get(NAME).asText();
      sceneJsonMap.put(sceneName, sceneNode.deepCopy());
    } else {
      LOGGER.error(GameConfig.getText("errorCreatingGameScene", sceneNode.get(NAME)));
    }
  }

  /**
   * Serializes a Game object into a JSON root node
   *
   * @param data - the data object to serialize
   * @return - a JsonNode that holds all the configured Game information
   * @throws IOException - an exception thrown if errors occur with input/output
   */
  @Override
  public JsonNode write(Game data) throws IOException {
    ObjectNode root = mapper.createObjectNode();
    ObjectNode dataNode = mapper.createObjectNode();

    handleInformationWriting(data, root);
    handleSceneWriting(data, dataNode);

    root.set(DATA, dataNode);
    return root;
  }

  private void handleInformationWriting(Game data, ObjectNode root) throws IOException {
    ObjectNode infoNode = (ObjectNode) informationParser.write(data.getGameInfo());
    root.set(INFORMATION, infoNode);
  }

  private void handleSceneWriting(Game data, ObjectNode dataNode) throws IOException {
    ArrayNode sceneArray = mapper.createArrayNode();

    // Regular Parse
    for (String scene : data.getLevelOrder()) {
      GameScene gameScene = data.getAllScenes().values().stream().filter(s -> s.getName().equals(scene)).findFirst().orElse(null);
      if (gameScene != null) {
        sceneArray.add(sceneParser.write(gameScene));
      }
    }

    // Prefab Scene Parse
    if (data.getScene(GameScene.PREFAB_SCENE_NAME) != null) {
      sceneArray.add(sceneParser.write(data.getScene(GameScene.PREFAB_SCENE_NAME)));
    }

    dataNode.set(SCENE, sceneArray);
  }

  /**
   * Returns the map of string to jsonNodes for scene resetting
   *
   * @return - The scene Json map
   */
  public Map<String, JsonNode> getSceneJsonMap() {
    return sceneJsonMap;
  }
}
