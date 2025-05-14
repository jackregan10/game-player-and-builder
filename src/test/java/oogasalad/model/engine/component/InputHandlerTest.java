package oogasalad.model.engine.component;

import oogasalad.model.engine.architecture.Game;
import oogasalad.model.engine.architecture.GameObject;
import oogasalad.model.engine.architecture.GameScene;
import oogasalad.model.engine.architecture.KeyCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class InputHandlerTest {

  private InputHandler inputHandler;

  @BeforeEach
  void setup() {
    Game game = new Game();
    GameScene gameScene = new GameScene("testScene");
    game.addScene(gameScene);
    GameObject player = new GameObject("player");
    gameScene.registerObject(player);

    inputHandler = new InputHandler();
    player.addComponent(inputHandler);

    Transform transform = new Transform();
    transform.setX(100);
    transform.setY(50);
    transform.setScaleX(200);
    transform.setScaleY(200);
    player.addComponent(transform);

    inputHandler.awake();
  }

  @Test
  void getPressedKeys_NoKeysSet_ReturnsEmptySet() {
    assertTrue(inputHandler.getPressedKeys().isEmpty());
  }

  @Test
  void setPressedKeys_ValidKeys_UpdatesCurrentKeys() {
    inputHandler.setPressedKeys(Set.of(KeyCode.A.getValue(), KeyCode.D.getValue()));
    Set<Integer> pressedKeys = inputHandler.getPressedKeys();

    assertTrue(pressedKeys.contains(KeyCode.A.getValue()));
    assertTrue(pressedKeys.contains(KeyCode.D.getValue()));
  }

  @Test
  void isKeyHold_KeyPresentInCurrentKeys_ReturnsTrue() {
    inputHandler.setPressedKeys(Set.of(KeyCode.SPACE.getValue()));
    assertTrue(inputHandler.isKeyHold(KeyCode.SPACE));
  }

  @Test
  void isKeyHold_KeyNotInCurrentKeys_ReturnsFalse() {
    inputHandler.setPressedKeys(Set.of(KeyCode.W.getValue()));
    assertFalse(inputHandler.isKeyHold(KeyCode.S));
  }

  @Test
  void isKeyHold_NullKeyCode_ReturnsFalse() {
    inputHandler.setPressedKeys(Set.of(KeyCode.W.getValue()));
    assertFalse(inputHandler.isKeyHold(null));
  }

  @Test
  void isKeyPressed_KeyOnlyInCurrentKeys_ReturnsTrue() {
    inputHandler.setPressedKeys(Set.of(KeyCode.A.getValue()));
    inputHandler.update(0.016);
    inputHandler.setPressedKeys(Set.of(KeyCode.A.getValue(), KeyCode.S.getValue()));

    assertTrue(inputHandler.isKeyPressed(KeyCode.S));
    assertFalse(inputHandler.isKeyPressed(KeyCode.A));
  }

  @Test
  void isKeyPressed_KeyNeverPressed_ReturnsFalse() {
    inputHandler.setPressedKeys(Set.of(KeyCode.W.getValue()));
    inputHandler.update(0.016);
    inputHandler.setPressedKeys(Set.of(KeyCode.W.getValue()));
    assertFalse(inputHandler.isKeyPressed(KeyCode.W));
  }

  @Test
  void isKeyPressed_NullKeyCode_ReturnsFalse() {
    inputHandler.setPressedKeys(Set.of(KeyCode.W.getValue()));
    assertFalse(inputHandler.isKeyPressed(null));
  }

  @Test
  void isKeyReleased_KeyWasHeldNowNotInCurrent_ReturnsTrue() {
    inputHandler.setPressedKeys(Set.of(KeyCode.A.getValue()));
    inputHandler.update(0.016);
    inputHandler.setPressedKeys(Set.of());

    assertTrue(inputHandler.isKeyReleased(KeyCode.A));
  }

  @Test
  void isKeyReleased_NeverPressedKey_ReturnsFalse() {
    inputHandler.setPressedKeys(Set.of(KeyCode.A.getValue()));
    inputHandler.update(0.016);
    inputHandler.setPressedKeys(Set.of(KeyCode.A.getValue()));
    assertFalse(inputHandler.isKeyReleased(KeyCode.S));
  }

  @Test
  void isKeyReleased_NullKeyCode_ReturnsFalse() {
    inputHandler.setPressedKeys(Set.of(KeyCode.D.getValue()));
    assertFalse(inputHandler.isKeyReleased(null));
  }

  @Test
  void resetInputState_AfterSettingKeys_ClearsBothSets() {
    inputHandler.setPressedKeys(Set.of(KeyCode.A.getValue(), KeyCode.D.getValue()));
    inputHandler.update(0.016);
    inputHandler.resetInputState();

    assertTrue(inputHandler.getPressedKeys().isEmpty());
    assertFalse(inputHandler.isKeyHold(KeyCode.A));
    assertFalse(inputHandler.isKeyPressed(KeyCode.D));
  }

  @Test
  void registerMouseClick_ValidClick_MouseIsClicked() {
    inputHandler.registerMouseClick(150.0, 100.0);
    assertTrue(inputHandler.isMouseClicked());
    assertEquals(150.0, inputHandler.getMouseX());
    assertEquals(100.0, inputHandler.getMouseY());
  }

  @Test
  void update_ClearsMouseClickedFlag_AfterTwoUpdates() {
    inputHandler.registerMouseClick(200.0, 200.0);
    assertTrue(inputHandler.isMouseClicked());
    inputHandler.update(0.016);
    assertFalse(inputHandler.isMouseClicked());
  }

  @Test
  void componentTag_Always_ReturnsInputTag() {
    assertEquals(ComponentTag.INPUT, inputHandler.componentTag());
  }
}
