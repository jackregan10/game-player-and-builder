package oogasalad.model.parser;

import static oogasalad.model.config.GameConfig.LOGGER;

import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import oogasalad.model.config.GameConfig;
import oogasalad.model.engine.architecture.Game;
import org.apache.logging.log4j.Level;

/**
 * Json Parser Class. Highest level of hierarchy (JsonParser uses GameParser uses SceneParser uses
 * GameObjectParser uses Component/BehaviorParser
 * <p>
 * Author: Daniel Rodriguez-Florido
 */

public class JsonParser implements Parser<Game> {

  private static final GameParser GAME_PARSER = new GameParser();

  private final ObjectMapper mapper;
  private final String filePath;

  private Game myGame;

  /**
   * Constructor to create parser that can read and write.
   *
   * @param filePath The path which the game json is located.
   */
  public JsonParser(String filePath) {
    this.filePath = filePath;
    mapper = new ObjectMapper();
  }

  /**
   * @param node - the JSON node given to parse. For this purpose, throw in new node as this parser
   *             grabs the JSON node on its own from the constructor filename.
   * @return The Game object to run
   * @throws ParsingException if cannot find file or if error processing file
   */
  @Override
  public Game parse(JsonNode node) throws ParsingException {
    JsonNode rootNode;
    try {
      rootNode = mapper.readTree(new File(filePath));
    } catch (FileNotFoundException e) {
      LOGGER.error(GameConfig.getText("noParseFileNotFound", filePath));
      throw new ParsingException(GameConfig.getText("noParseFileNotFound", filePath), e);
    } catch (IOException e) {
      LOGGER.error(GameConfig.getText("noParseFileCorrupted", filePath));
      throw new ParsingException(GameConfig.getText("noParseFileCorrupted", filePath), e);
    }

    return myGame = GAME_PARSER.parse(rootNode);
  }

  /**
   * @return The game object that was parsed
   */
  public Game getGame() {
    return myGame;
  }

  /**
   * @param game - the Game object that we wish to write to JSON
   * @return JsonNode of the game, indicating success
   * @throws IOException - exception for failed input or output
   */
  @Override
  public JsonNode write(Game game) throws IOException {
    JsonNode gameNode = null;

    try {
      gameNode = GAME_PARSER.write(game);
      File outputFile = new File(filePath);
      mapper.writeValue(outputFile, gameNode);
    } catch (DatabindException e) {
      LOGGER.error(GameConfig.getText("errorMappingJson", filePath));
    }

    handleNullGameNode(gameNode);

    LOGGER.log(Level.INFO, GameConfig.getText("gameFileCreatedInfo", filePath));
    return gameNode;
  }

  private void handleNullGameNode(JsonNode gameNode) throws IOException {
    if (gameNode == null) {
      LOGGER.error(GameConfig.getText("nullGameNode", filePath));
      throw new IOException(GameConfig.getText("nullGameNode", filePath));
    }
  }


}
