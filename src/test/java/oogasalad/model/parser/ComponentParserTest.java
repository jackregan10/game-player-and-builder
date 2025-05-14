package oogasalad.model.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import oogasalad.model.engine.architecture.GameComponent;
import oogasalad.model.serialization.serializable.SerializedField;
import oogasalad.model.engine.component.Transform;
import oogasalad.model.serialization.serializable.TypeRef;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ComponentParserTest {

  ComponentParser myComponentParser;
  ObjectMapper myMapper;

  String goodJsonString =
      """
          {
            "Name": "Transform",
            "Configurations": {
              "x": 1,
              "y": 1,
              "rotation": 1,
              "scaleX": 1,
              "scaleY": 1
            }
          }
          """;

  String badJsonString = """
      {
        "Configurations": {
          "x": 1,
          "y": 1,
          "rotation": 1,
          "scaleX": 1,
          "scaleY": 1
        }
      }
      """;

  @BeforeEach
  void setUp() {
    myComponentParser = new ComponentParser();
    myMapper = new ObjectMapper();
  }

  @Test
  void parse_validJsonFile_success() throws JsonProcessingException, ParsingException {
    JsonNode node = myMapper.readTree(goodJsonString);
    GameComponent myComponent = myComponentParser.parse(node);
    assertNotNull(myComponent);
    for (SerializedField serializedField : myComponent.getSerializedFields()) {
      if (serializedField.getFieldType() == new TypeRef<Double>() {}.getType()) {
        assertEquals(1.0, (double) serializedField.getValue());
      }
    }
    // Use this print statement to verify output
//    myComponent.getSerializedFields()
//        .forEach(serializedField -> System.out.println(serializedField + ": " +
//            serializedField.getValue()));
  }

  @Test
  void parse_invalidJsonFile_catchError() throws JsonProcessingException, ParsingException {
    JsonNode node = myMapper.readTree(badJsonString);
    assertThrows(ParsingException.class, () -> myComponentParser.parse(node));
  }

  @Test
  void write_validComponent_success() throws IOException {
    Transform transformComponent = new Transform();
    transformComponent.setX(1);
    transformComponent.setY(1);
    transformComponent.setRotation(1);
    transformComponent.setScaleX(1);
    transformComponent.setScaleY(1);

    JsonNode gameComponent = myComponentParser.write(transformComponent);
    assertNotNull(gameComponent);
    assertTrue(gameComponent.isObject());
    assertTrue(gameComponent.has("Name"));
    assertTrue(gameComponent.has("Configurations"));

    JsonNode configurations = gameComponent.get("Configurations");
    assertTrue(configurations.has("x"));
    assertTrue(configurations.has("y"));
    assertTrue(configurations.has("rotation"));
    assertTrue(configurations.has("scaleX"));
    assertTrue(configurations.has("scaleY"));
  }

}