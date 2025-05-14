package oogasalad.model.parser;

import static oogasalad.model.config.GameConfig.LOGGER;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import oogasalad.model.config.GameConfig;
import oogasalad.model.engine.architecture.GameComponent;
import oogasalad.model.serialization.serializable.Serializable;
import oogasalad.model.serialization.serializable.SerializedField;
import oogasalad.model.serialization.serializer.Serializer;

/**
 * Parses and serializes a GameComponent object to and from a JSON node
 *
 * @author Justin Aronwald, Daniel Rodriguez-Florido
 */
public class ComponentParser implements Parser<GameComponent>, Serializable {

  private static final String NAME = "Name";
  private static final String CONFIG = "Configurations";
  private static final String CLASS_PATH = "oogasalad.model.engine.component.";

  private final ObjectMapper mapper = new ObjectMapper();

  /**
   * Parses a JSON node into a GameComponent instance
   *
   * @param componentNode - the JSON node given to parse
   * @return - a GameComponent that gets configured in the parent class
   * @throws ParsingException - error thrown if reflection or parsing fails
   */
  @Override
  @SuppressWarnings("unchecked")
  public GameComponent parse(JsonNode componentNode) throws ParsingException {
    validateComponentName(componentNode);

    String name = componentNode.get(NAME).asText();
    try {
      String fullClassName = CLASS_PATH + name;

      Class<?> rawClass = Class.forName(fullClassName);
      if (!GameComponent.class.isAssignableFrom(rawClass)) {
        throw new ParsingException(GameConfig.getText("errorExtendingComponent", fullClassName));
      }

      Class<? extends GameComponent> componentClass = (Class<? extends GameComponent>) rawClass;
      GameComponent myComponent = componentClass.getDeclaredConstructor().newInstance();
      JsonNode configNode = componentNode.get(CONFIG);

      myComponent.getSerializedFields().forEach(field -> setFieldFromConfig(configNode, field));

      return myComponent;
    } catch (ClassNotFoundException | InvocationTargetException | InstantiationException | IllegalAccessException |NoSuchMethodException e) {
      LOGGER.error(GameConfig.getText("errorInstantiatingComponent", name), e);
      throw new ParsingException(GameConfig.getText("errorInstantiatingComponent", name), e);
    }
  }

  private void setFieldFromConfig(JsonNode config, SerializedField field) {
    String name = field.getFieldName();

    if (!config.has(name)) return;

    JsonNode valueNode = config.get(name);
    Serializer.deserialize(field, valueNode);
  }

  private void validateComponentName(JsonNode componentNode) throws ParsingException {
    if (!componentNode.has(NAME)) {
      LOGGER.error(GameConfig.getText("noMatchingComponent"));
      throw new ParsingException(GameConfig.getText("noMatchingComponent"));
    }
  }

  /**
   * Serializes a gameComponent into a JSON node
   *
   * @param data - the data object to serialize
   * @return - a JSON node with the component's data
   */
  @Override
  public JsonNode write(GameComponent data) throws IOException {
    ObjectNode root = mapper.createObjectNode();
    root.put(NAME, data.getClass().getSimpleName());

    ObjectNode config = root.putObject(CONFIG);
    for (SerializedField field : data.getSerializedFields()) {
      JsonNode fieldNode = Serializer.serialize(field);
      config.set(field.getFieldName(), fieldNode);
    }

    return root;
  }
}
