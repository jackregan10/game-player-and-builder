package oogasalad.model.engine.datastore;

import oogasalad.model.engine.subComponent.behavior.action.SetTextFromDataStoreAction;
import oogasalad.model.engine.architecture.Game;
import oogasalad.model.engine.architecture.GameObject;
import oogasalad.model.engine.architecture.GameScene;
import oogasalad.model.engine.subComponent.behavior.Behavior;
import oogasalad.model.engine.component.BehaviorController;
import oogasalad.model.engine.component.TextRenderer;
import oogasalad.model.engine.component.Transform;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ScriptableDataStoreTest {

  private GameScene scene;
  private ScriptableDataStore store;
  private SetTextFromDataStoreAction action;

  @BeforeEach
  void setup() {
    Game game = new Game();
    scene = new GameScene("TestScene");
    game.addScene(scene);
    store = game.getDataStore();
    store.setScene(scene);

    GameObject player = new GameObject("player", "PlayerTag");
    BehaviorController controller = player.addComponent(BehaviorController.class);

    Behavior behavior = new Behavior("TestBehavior");
    action = new SetTextFromDataStoreAction();
    action.setParameter("player.x/save");

    behavior.getActions().add(action);
    controller.getBehaviors().add(behavior);

    Transform transform = player.addComponent(Transform.class);
    transform.setX(10.0); // Setting the live value
    transform.setScaleX(20.0);

    TextRenderer tr = new TextRenderer();
    tr.setText("Hello World");
    tr.setFontSize(20);
    player.addComponent(tr);

    scene.registerObject(player);
  }

  @Test
  void setAndGet_ValidKey_ReturnsValue() {
    store.set("score", 100);
    assertEquals(100, store.get("score"));
  }

  @Test
  void get_NonexistentKey_ThrowsMissingKeyException() {
    assertThrows(MissingKeyException.class, () -> store.get("nonexistent"));
  }

  @Test
  void getAs_CorrectType_ReturnsValue() {
    store.set("lives", 3);
    int value = store.getAs("lives", Integer.class);
    assertEquals(3, value);
  }

  @Test
  void getAs_WrongType_ThrowsClassCastException() {
    store.set("text", "not an int");
    assertThrows(ClassCastException.class, () -> store.getAs("text", Integer.class));
  }

  @Test
  void has_KeyExists_ReturnsTrue() {
    store.set("score", 123);
    assertTrue(store.has("score"));
  }

  @Test
  void has_KeyMissing_ReturnsFalse() {
    assertFalse(store.has("missingKey"));
  }

  @Test
  void getValue_ValidSerializableField_ReturnsValue() {
    Object value = store.getValue("player.text");
    assertEquals("Hello World", value);
  }

  @Test
  void getValue_InvalidPathFormat_ThrowsIllegalArgumentException() {
    assertThrows(IllegalArgumentException.class, () -> store.getValue("badpath"));
  }

  @Test
  void getValue_FieldNotFound_ThrowsMissingKeyException() {
    assertThrows(MissingKeyException.class, () -> store.getValue("player.nonexistentField"));
  }

  @Test
  void getValue_FieldAccessThrows_ThrowsFieldAccessException() {
    GameObject glitched = new GameObject("glitch", "Tag");
    TextRenderer bad = new TextRenderer() {
      @Override
      public String getText() {
        throw new RuntimeException("Access Error");
      }
    };
    glitched.addComponent(bad);
    scene.registerObject(glitched);
    assertThrows(FieldAccessException.class, () -> store.getValue("glitch.text"));
  }

  @Test
  void saveMarkedKeys_WithExistingBehaviorAction_SavesCorrectField() {
    assertFalse(store.has("player.x"));

    store.saveMarkedKeys();

    assertTrue(store.has("player.x"));
  }

  @Test
  void saveMarkedKeys_NoSaveFlag_DoesNotSaveField() {
    action.setParameter("player.y");
    assertFalse(store.has("player.y"));

    store.saveMarkedKeys();

    assertFalse(store.has("player.y"));
  }

  @Test
  void saveMarkedKeys_WithSaveAlias_SavesUnderAliasName() {
    action.setParameter("player.scaleX/save,size");

    assertFalse(store.has("size"));

    store.saveMarkedKeys();

    assertTrue(store.has("size"));
    assertFalse(store.has("player.scaleX"));

    Object saved = store.get("size");
    assertEquals(20L, saved);
  }
}