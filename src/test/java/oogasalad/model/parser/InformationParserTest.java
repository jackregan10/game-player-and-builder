package oogasalad.model.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.awt.Dimension;
import oogasalad.model.engine.architecture.GameInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class InformationParserTest {

  private ObjectMapper mapper;
  private InformationParser informationParser;
  public String json;

  @BeforeEach
  void setUp() {
    mapper = new ObjectMapper();
    informationParser = new InformationParser();
    json = """
          {
            "Name": "TestGame",
            "Description": "A fun game",
            "Author": "Justin",
            "Resolution": {
              "Width": 1280,
              "Height": 720
            }
          }
        """;
  }

  @Test
  void parse_validJson_returnsGameInfo() throws ParsingException, JsonProcessingException {
    JsonNode node = mapper.readTree(json);
    GameInfo info = informationParser.parse(node);

    assertEquals("TestGame", info.name());
    assertEquals("A fun game", info.description());
    assertEquals("Justin", info.author());
    assertEquals(new Dimension(1280, 720), info.resolution());
  }


  @Test
  void write_gameInfo_returnsCorrectJsonNode() throws Exception {
    GameInfo info = new GameInfo("TestGame", "A fun game", "Justin", new Dimension(1280, 720));
    JsonNode result = informationParser.write(info);

    assertEquals("TestGame", result.get("Name").asText());
    assertEquals("A fun game", result.get("Description").asText());
    assertEquals("Justin", result.get("Author").asText());

    JsonNode resolution = result.get("Resolution");
    assertNotNull(resolution);
    assertEquals(1280, resolution.get("Width").asInt());
    assertEquals(720, resolution.get("Height").asInt());
  }

  @Test
  void parse_missingField_throwsParsingException()
      throws ParsingException, JsonProcessingException {
    json = """
          {
            "Description": "A fun game",
            "Author": "Justin",
            "Resolution": {
              "Width": 1280,
              "Height": 720
            }
          }
        """;

    JsonNode node = mapper.readTree(json);
    assertThrows(ParsingException.class, () -> informationParser.parse(node));
  }

  @Test
  void parse_missingResolution_throwsParsingException()
      throws ParsingException, JsonProcessingException {
    json = """
          {
            "Name": "TestGame",
            "Description": "A fun game",
            "Author": "Justin"
          }
        """;

    JsonNode node = mapper.readTree(json);
    assertThrows(ParsingException.class, () -> informationParser.parse(node));
  }


}