package oogasalad.model.engine.datastore;

import static org.junit.jupiter.api.Assertions.*;

import oogasalad.model.engine.architecture.Game;
import oogasalad.model.engine.architecture.GameObject;
import oogasalad.model.engine.architecture.GameScene;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link ParsedKey}.
 */
public class ParsedKeyTest {

  private ScriptableDataStore dataStore;
  GameObject gameObject;

  @BeforeEach
  void setUp() {
    Game game = new Game();
    GameScene scene = new GameScene("TestScene");
    game.addScene(scene);
    dataStore = game.getDataStore();
    dataStore.setScene(scene);
    gameObject = new GameObject("test");
    dataStore.set("test.base", 10.0);
    dataStore.set("test.add", 5.0);
    dataStore.set("test.sub", 2.0);
  }


  @Test
  void constructor_BaseKeyOnly_ParsesCorrectly() {
    ParsedKey key = new ParsedKey("test.base");
    assertEquals("test.base", key.getBaseKeyPath());
    assertFalse(key.shouldSave());
    assertEquals("test.base", key.getOriginal());
  }

  @Test
  void constructor_SaveFlagIncluded_ParsesShouldSaveTrue() {
    ParsedKey key = new ParsedKey("test.base/save");
    assertEquals("test.base", key.getBaseKeyPath());
    assertTrue(key.shouldSave());
  }

  @Test
  void constructor_OperationIncluded_ParsesOperationsCorrectly() {
    ParsedKey key = new ParsedKey("test.base/op:add,test.add;sub,test.sub");
    assertEquals("test.base", key.getBaseKeyPath());
    assertFalse(key.shouldSave());
    // No direct access to operations, but we'll validate via applyOperations
  }


  @Test
  void applyOperations_NoOperations_ReturnsOriginalValueRounded() {
    ParsedKey key = new ParsedKey("test.base");
    Object result = key.applyOperations(10.7, dataStore);
    assertEquals(11L, result); // Rounded
  }

  @Test
  void applyOperations_WithLiteralArgument_AppliesCorrectly() {
    ParsedKey key = new ParsedKey("test.base/op:add,5;sub,2");
    Object result = key.applyOperations(dataStore.get("test.base"), dataStore);
    // (10 + 5) - 2 = 13
    assertEquals(13L, result);
  }

  @Test
  void applyOperations_WithNoOpsAndNonNumericValue_DoesNotRound() {
    ParsedKey key = new ParsedKey("some.text");
    Object result = key.applyOperations("nonNumeric", dataStore);
    assertEquals("nonNumeric", result);
  }


  @Test
  void constructor_InvalidOperation_IgnoresOperation() {
    ParsedKey key = new ParsedKey("test.base/op:invalid,test.add");
    Object result = key.applyOperations(dataStore.get("test.base"), dataStore);
    assertEquals(10L, result);
  }

  @Test
  void constructor_SaveAliasWithoutFurtherOps_ParsesCorrectly() {
    ParsedKey key = new ParsedKey("player.score/save,finalScore");
    assertEquals("player.score", key.getBaseKeyPath());
    assertTrue(key.shouldSave());
    assertEquals("finalScore", key.getSaveAlias());
  }

  @Test
  void constructor_SaveAliasWithFurtherOps_ParsesBothCorrectly() {
    ParsedKey key = new ParsedKey("player.score/save,finalScore/op:add,bonus.points");
    assertEquals("player.score", key.getBaseKeyPath());
    assertTrue(key.shouldSave());
    assertEquals("finalScore", key.getSaveAlias());
  }

  @Test
  void constructor_SaveAliasMultipleSlashes_HandlesProperly() {
    ParsedKey key = new ParsedKey("enemy.hp/save,healthCopy/op:sub,damageTaken");
    assertEquals("enemy.hp", key.getBaseKeyPath());
    assertTrue(key.shouldSave());
    assertEquals("healthCopy", key.getSaveAlias());
  }
}
