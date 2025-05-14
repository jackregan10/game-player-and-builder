package oogasalad.model.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.IOException;
import oogasalad.model.engine.architecture.GameScene;
import oogasalad.model.parser.GameSceneParser;
import oogasalad.model.parser.ParsingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GameSceneParserTest {

  GameSceneParser myGameSceneParser;
  ObjectMapper myMapper;
  String goodJsonString = """
     {
       "Name": "MainGame",
       "GameObjects": [
         {
           "Name": "Dino",
           "Tag": "player",
           "Components": [
             {
               "Name": "Transform",
               "Configurations": {
                 "x": 100,
                 "y": 420,
                 "rotation": 0,
                 "scaleX": 40,
                 "scaleY": 40
               }
             }
           ],
           "BehaviorController": {
             "Behaviors": [
               {
                 "Name": "JumpWhenPressed",
                 "constraints": [
                   {
                     "name": "KeyPressConstraint",
                     "parameter": "SPACE",
                     "parameterType": "KeyCode"
                   },
                   {
                     "name": "TouchingFromAboveConstraint",
                     "parameter": "Ground",
                     "parameterType": "String"
                   }
                 ],
                 "actions": [
                   {
                     "name": "VelocityYSetAction",
                     "parameter": -500,
                     "parameterType": "Double"
                   }
                 ]
               },
               {
                 "Name": "CrouchWhenPressed",
                 "constraints": [
                   {
                     "name": "KeyPressConstraint",
                     "parameter": "DOWN",
                     "parameterType": "KeyCode"
                   }
                 ],
                 "actions": [
                   {
                     "name": "ScaleYSetAction",
                     "parameter": null,
                     "parameterType": "Void"
                   }
                 ]
               }
             ]
           }
         }
       ]
     }
     """;

  // The bad json string has no name, should return error
  String badJsonString = """
      {
        "GameObjects": [
          {
            "Name": "Test Object",
            "Components": [
              {
                "Name": "Transform",
                "Configurations": {
                  "x": 100,
                  "y": 420,
                  "rotation": 0,
                  "scaleX": 40,
                  "scaleY": 40
                }
              }
            ],
            "Behaviors": [
              {
                "Name": "ResetOnHit",
                "constraints": [
                  {
                    "name": "CollidesWithConstraint",
                    "parameter": "player",
                    "parameterType": "String"
                  }
                ],
                "actions": [
                  {
                    "name": "ChangeSceneAction",
                    "parameter": "MainMenuScene",
                    "parameterType": "String"
                  }
                ]
              }
            ]
          }
        ]
      }
      """;

  @BeforeEach
  void setUp() {
    myGameSceneParser = new GameSceneParser();
    myMapper = new ObjectMapper();
  }

  @Test
  void parse_validJson_properlyParses() throws JsonProcessingException, ParsingException {
    JsonNode node = myMapper.readTree(goodJsonString);
    myGameSceneParser.parse(node);
    assertEquals("MainGame", node.get("Name").textValue());
    assertTrue(node.has("GameObjects"));
  }

  @Test
  void parse_invalidJson_throwsError() throws JsonProcessingException, ParsingException {
    JsonNode node = myMapper.readTree(badJsonString);
    // Will throw error for not having Name field
    assertThrows(ParsingException.class, () -> myGameSceneParser.parse(node));
  }

  @Test
  void write_fullGameScene_success() throws IOException, ParsingException {
    JsonNode myNode = myMapper.readTree(goodJsonString);

    GameScene gameScene = myGameSceneParser.parse(myNode);
    JsonNode myGameScene = myGameSceneParser.write(gameScene);

   assertTrue(myGameScene.has("GameObjects"));
   assertTrue(myGameScene.has("StoreObjects"));
  }
}