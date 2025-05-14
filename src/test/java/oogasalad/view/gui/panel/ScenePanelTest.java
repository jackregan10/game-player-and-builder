package oogasalad.view.gui.panel;

import java.util.ArrayList;
import java.util.List;
import javafx.scene.Scene;
import javafx.stage.Stage;
import oogasalad.model.engine.architecture.Game;
import oogasalad.model.engine.architecture.GameScene;
import org.junit.jupiter.api.Test;
import util.DukeApplicationTest;

import static org.junit.jupiter.api.Assertions.*;

class ScenePanelTest extends DukeApplicationTest {

  private ScenePanel scenePanel;
  private Game game;

  @Override
  public void start(Stage stage) {
    scenePanel = new ScenePanel();
    game = new Game();
    GameScene scene1 = new GameScene("Scene1");
    GameScene scene2 = new GameScene("Scene2");
    game.addScene(scene1);
    game.addScene(scene2);
    List<String> scenes = new ArrayList<>();
    scenes.add("Scene1");
    scenes.add("Scene2");
    game.setLevelOrder(scenes);
    game.changeScene("Scene1");

    scenePanel.setGame(game);
    stage.setScene(new Scene(scenePanel));
    stage.show();
  }

  @Test
  void setGame_ShouldDisplayScenes() {
    assertFalse(scenePanel.getChildren().isEmpty(), "ScenePanel should not be empty after setting a game.");
  }

  @Test
  void addScene_ShouldIncreaseSceneCount() {
    int initialSize = game.getAllScenes().size();
    clickOn(scenePanel.getChildren().get(1));
    int newSize = game.getAllScenes().size();
    assertTrue(newSize > initialSize, "Scene count should increase after adding a scene.");
  }

  @Test
  void getPrefabScene_ShouldReturnPrefabScene() {
    GameScene prefab = scenePanel.getPrefabScene();
    assertNotNull(prefab, "Prefab scene should not be null.");
    assertEquals(GameScene.PREFAB_SCENE_NAME, prefab.getName(), "Prefab scene should have the correct name.");
  }

  @Test
  void deleteScene_ShouldRemoveScene() {
    GameScene sceneToDelete = new GameScene("ToDelete");
    game.addScene(sceneToDelete);
    interact(() -> scenePanel.setGame(game));
    int initialSize = game.getAllScenes().size();
    interact(() -> {
      try {
        var method = ScenePanel.class.getDeclaredMethod("handleDelete", GameScene.class);
        method.setAccessible(true);
        method.invoke(scenePanel, sceneToDelete);
      } catch (Exception e) {
        fail("Reflection failed: " + e.getMessage());
      }
    });

    int newSize = game.getAllScenes().size();
    assertEquals(initialSize - 1, newSize, "Scene count should decrease after deletion.");
  }
}
