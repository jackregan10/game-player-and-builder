package oogasalad.model.parser;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.IOException;
import java.util.List;
import oogasalad.model.config.GameConfig;
import oogasalad.model.engine.architecture.GameComponent;
import oogasalad.model.engine.architecture.GameObject;
import oogasalad.model.engine.component.Transform;

import static oogasalad.model.config.GameConfig.LOGGER;

/**
 * This class parses and serializes gameObjects to and from a JSON
 *
 * @author Justin Aronwald
 */
public class GameObjectParser implements Parser<GameObject> {

  private final ComponentParser componentParser = new ComponentParser();
  private final ObjectMapper mapper = new ObjectMapper();

  private static final String TAG = "Tag";
  private static final String NAME = "Name";
  private static final String COMPONENTS = "Components";

  /**
   * Parses a JSON node into a gameObject (entity)
   *
   * @param node - the JSON node given to parse
   * @return - a new gameObject instance with components and behaviors added
   * @throws ParsingException - if there's an error or parsing fails
   */
  @Override
  public GameObject parse(JsonNode node) throws ParsingException {
    if (node == null || !node.has(NAME)) {
      throw new ParsingException(GameConfig.getText("noNameFound"));
    }

    String name = node.get(NAME).asText();
    String tag = node.has(TAG) ? node.get(TAG).asText() : null;

    GameObject gameObject = new GameObject(name, tag);
    handleAddingComponents(node, gameObject);

    // By design, all components should have Transform components
    if (!gameObject.getAllComponents().containsKey(Transform.class)) {
      LOGGER.error(GameConfig.getText("gameObjectWithoutTransform", name));
      throw new ParsingException(GameConfig.getText("gameObjectWithoutTransform", name));
    }

    return gameObject;
  }

  private void handleAddingComponents(JsonNode node, GameObject gameObject) throws ParsingException {
    if (node.has(COMPONENTS)) {
      JsonNode components = node.get(COMPONENTS);
      parseComponents(gameObject, components);
    }
  }

  private void parseComponents(GameObject gameObject, JsonNode componentsNode) throws ParsingException {
    if (componentsNode.isArray()) {
      for (JsonNode component : componentsNode) {
        gameObject.addComponent(componentParser.parse(component));
      }
    }
  }

  /**
   * Serializes a GameObject into a JSON node
   *
   * @param data - the data object to serialize
   * @return - a JSON node with the game object's data
   */
  @Override
  public JsonNode write(GameObject data) throws IOException {
    ObjectNode root = mapper.createObjectNode();
    root.put(NAME, data.getName());

    if (data.getTag() != null) {
      root.put(TAG, data.getTag());
    }

    List<GameComponent> components = data.getAllComponents().values().stream().toList();
    handleWritingComponents(root, components);

    return root;
  }

  private void handleWritingComponents(ObjectNode root, List<GameComponent> componentList)
      throws IOException {
    ArrayNode components = mapper.createArrayNode();

    for (GameComponent component : componentList) {
      JsonNode componentNode = componentParser.write(component);
      components.add(componentNode);
    }
    root.set(COMPONENTS, components);
  }
}
