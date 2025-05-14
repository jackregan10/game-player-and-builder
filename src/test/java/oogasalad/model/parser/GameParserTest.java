package oogasalad.model.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import oogasalad.model.engine.architecture.Game;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GameParserTest {

  final static String TEST_FILE_NAME = "data/GameJsons/DINO/DINO_00_Testing/DINO_00_Testing.json";
  GameParser myGameParser;
  ObjectMapper myMapper;

  @BeforeEach
  void setUp() {
    myGameParser = new GameParser();
    myMapper = new ObjectMapper();
  }

  @Test
  void parse_parseFullGame_success() throws IOException, ParsingException {
    JsonNode gameFile = myMapper.readTree(new File(TEST_FILE_NAME));
    Game myGame = myGameParser.parse(gameFile);
    assertEquals(2, myGame.getAllScenes().size());

    myGame.getAllScenes().values()
        .forEach(scene -> {
          assertFalse(scene.getName().isEmpty());
          assertFalse(scene.getAllComponents().isEmpty());
        });
  }

  @Test
  void write_writeFullGame_success() throws IOException, ParsingException {
    JsonNode gameJson = myMapper.readTree(new File(TEST_FILE_NAME));
    Game myGame = myGameParser.parse(gameJson);
    JsonNode writtenGame = myGameParser.write(myGame);

    assertTrue(writtenGame.has("Information"));
    assertTrue(writtenGame.has("Data"));
  }
}