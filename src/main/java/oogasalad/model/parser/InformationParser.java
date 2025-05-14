package oogasalad.model.parser;

import static oogasalad.model.config.GameConfig.LOGGER;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.awt.Dimension;
import java.io.IOException;
import oogasalad.model.config.GameConfig;
import oogasalad.model.engine.architecture.GameInfo;

/**
 * Parses and serializes the information node of a Game object to and from a JSON node
 *
 * @author Justin Aronwald
 */
public class InformationParser implements Parser<GameInfo> {

  private final ObjectMapper mapper = new ObjectMapper();

  private static final String NAME = "Name";
  private static final String DESCRIPTION = "Description";
  private static final String AUTHOR = "Author";
  private static final String RESOLUTION = "Resolution";
  private static final String WIDTH = "Width";
  private static final String HEIGHT = "Height";

  /**
   * Parses a JSON node into a GameInfo class
   *
   * @param node - the JSON node given to parse
   * @return - a fully configured GameInfo object
   * @throws ParsingException - error thrown if reflection or parsing fails
   */
  @Override
  public GameInfo parse(JsonNode node) throws ParsingException {
    if (node == null || !node.has(NAME)) {
      LOGGER.warn(GameConfig.getText("missingName"));
      throw new ParsingException(GameConfig.getText("missingName"));
    }
    handleGameInfoParsing result = getHandleGameInfoParsing(node);
    return new GameInfo(result.name(), result.description(), result.author(), result.resolution());
  }

  private static handleGameInfoParsing getHandleGameInfoParsing(JsonNode node)
      throws ParsingException {
    String name = node.get(NAME).asText();
    String description = node.has(DESCRIPTION) ? node.get(DESCRIPTION).asText() : "";
    String author = node.has(AUTHOR) ? node.get(AUTHOR).asText() : "";

    JsonNode resolutionNode = node.get(RESOLUTION);
    if (resolutionNode == null || !resolutionNode.has(WIDTH) || !resolutionNode.has(HEIGHT)) {
      LOGGER.warn(GameConfig.getText("missingResolutionBlock"));
      throw new ParsingException(GameConfig.getText("missingResolutionBlock"));
    }

    int width = resolutionNode.get(WIDTH).asInt();
    int height = resolutionNode.get(HEIGHT).asInt();
    Dimension resolution = new Dimension(width, height);
    return new handleGameInfoParsing(name, description, author, resolution);
  }

  private record handleGameInfoParsing(String name, String description, String author,
                                       Dimension resolution) {

  }

  /**
   * Serializes a GameInfo object into a JSON node
   *
   * @param info - the GameInfo object to serialize
   * @return - a JsonNode that holds all the configured GameInfo information (name, author ...)
   * @throws IOException - an exception thrown if errors occur with input/output
   */
  @Override
  public JsonNode write(GameInfo info) throws IOException {
    ObjectNode root = mapper.createObjectNode();
    root.put(NAME, info.name());
    root.put(DESCRIPTION, info.description());
    root.put(AUTHOR, info.author());

    ObjectNode resolutionNode = mapper.createObjectNode();
    resolutionNode.put(WIDTH, info.resolution().width);
    resolutionNode.put(HEIGHT, info.resolution().height);
    root.set(RESOLUTION, resolutionNode);

    return root;
  }
}