package oogasalad.model.parser;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.lang.reflect.Field;
import oogasalad.model.engine.architecture.Game;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * JSON Parser Test. Cannot test without some other concrete stuff being created.
 * <p>
 * Author: Daniel Rodriguez-Florido
 */
class JsonParserTest {

  private static final String READ_FILE_PATH = "data/GameJsons/DINO/DINO_00_Testing/DINO_00_Testing.json";
  private static final String WRITE_FILE_PATH = "data/GameJsons/DINO/DINO_00_Testing1/DINO_00_Testing1.json";

  JsonParser parser;
  ObjectMapper myMapper;
  Game myGame;

  @BeforeEach
  void setUp() throws ParsingException {
    parser = new JsonParser(READ_FILE_PATH);
    myMapper = new ObjectMapper();
    myGame = parser.parse(myMapper.createObjectNode());
  }

  @Test
  void parse_parseFullJson_success() {
    assertNotNull(myGame);
    assertFalse(myGame.getAllScenes().isEmpty());
    assertNotNull(myGame.getGameInfo());
    assertNotNull(myGame.getLevelOrder());
  }

  @Test
  void parse_parseFullJson_throwsParsingException() {
    String badFilePath = "blah/blah.json";
    parser = new JsonParser(badFilePath);
    assertThrows(ParsingException.class, () -> parser.parse(myMapper.createObjectNode()));
  }

  @Test
  void write_writeFullJson_success() throws IOException {
    parser = new JsonParser(WRITE_FILE_PATH);
    JsonNode writtenGame = parser.write(myGame);
    assertNotNull(writtenGame);
    assertTrue(writtenGame.has("Data"));
    assertTrue(writtenGame.has("Information"));
  }
}