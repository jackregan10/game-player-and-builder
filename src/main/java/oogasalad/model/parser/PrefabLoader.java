package oogasalad.model.parser;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import oogasalad.model.config.GameConfig;
import oogasalad.model.engine.architecture.GameObject;

import static oogasalad.model.config.GameConfig.LOGGER;

/**
 * PrefabLoader is responsible for loading prefab JSON files from a specified directory. It parses
 * the JSON files and creates GameObject instances based on the parsed data. This class is used to
 * load prefabs that can be placed in the game.
 */

public class PrefabLoader {

  // Directory where prefab JSON files are located
  public static final String PREFAB_DIRECTORY = "src/main/java/oogasalad/model/engine/prefab/";

  /**
   * Loads all prefab JSON files in the directory and returns the list of GameObjects that are
   * allowed to be placed (i.e., only Bird and Cactus in this case).
   *
   * @param directory the package within /prefab/ containing the desired prefabs, such as
   *                  "dinosaur"
   */
  public static List<GameObject> loadAvailablePrefabs(String directory) {
    List<GameObject> validPrefabs = new ArrayList<>();
    File dir = new File(PREFAB_DIRECTORY + directory + "/");
    if (!dir.exists()) {
      LOGGER.error(GameConfig.getText("prefabDirectoryError", dir.getAbsolutePath()));
      throw new IllegalArgumentException(
          GameConfig.getText("prefabDirectoryError", dir.getAbsolutePath()));
    }
    File[] files = dir.listFiles((d, name) -> name.endsWith(".json"));
    if (files == null || files.length == 0) {
      LOGGER.error(GameConfig.getText("noJsonFoundInDirectory", dir.getAbsolutePath()));
      throw new IllegalArgumentException(
          GameConfig.getText("noJsonFoundInDirectory", dir.getAbsolutePath()));
    }
    ObjectMapper mapper = new ObjectMapper();
    GameObjectParser parser = new GameObjectParser();
    parseFiles(files, mapper, parser, validPrefabs);
    return validPrefabs;
  }

  private static void parseFiles(File[] files, ObjectMapper mapper, GameObjectParser parser,
      List<GameObject> validPrefabs) {
    for (File file : files) {
      try {
        JsonNode node = mapper.readTree(file);
        GameObject obj = parser.parse(node);
        validPrefabs.add(obj);
      } catch (IOException | ParsingException e) {
        LOGGER.error(GameConfig.getText("errorParsingPrefab", file.getName(), e.getMessage()));
        throw new PrefabLoadException(
            GameConfig.getText("errorParsingPrefab", file.getName(), e.getMessage()), e);
      }
    }
  }
}

