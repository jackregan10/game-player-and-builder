package oogasalad.view.gui.panel;

import static org.junit.jupiter.api.Assertions.*;
import static org.testfx.api.FxAssert.verifyThat;

import javafx.scene.Scene;
import javafx.stage.Stage;
import oogasalad.model.engine.architecture.GameObject;
import oogasalad.model.engine.architecture.GameScene;
import oogasalad.model.engine.component.SpriteRenderer;
import oogasalad.model.engine.component.Transform;
import oogasalad.model.resource.ResourcePath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

public class PrefabPanelTest extends ApplicationTest {

  private PrefabPanel prefabPanel;
  private GameScene prefabScene;
  private GameObject prefab;

  private boolean prefabInstantiated;

  @Override
  public void start(Stage stage) {
    prefabPanel = new PrefabPanel();
    Scene scene = new Scene(prefabPanel, 400, 300);
    stage.setScene(scene);
    stage.show();

    prefabInstantiated = false;
    prefabPanel.setOnInstantiatePrefab(obj -> prefabInstantiated = true);
  }

  @BeforeEach
  void setupScene() {
    prefabScene = new GameScene("PrefabScene");

    prefab = new GameObject("TestPrefab", "");
    prefab.addComponent(Transform.class);
    SpriteRenderer renderer = prefab.addComponent(SpriteRenderer.class);
    ResourcePath path = new ResourcePath();
    path.setPath("images/test.png"); // mock path
    renderer.setImagePath(path);

    prefabScene.registerObject(prefab);
  }

  @Test
  void setPrefabScene_WhenCalled_PopulatesPanelWithPrefabButtons() {
    interact(() -> prefabPanel.setPrefabScene(prefabScene));
    verifyThat("TestPrefab", javafx.scene.Node::isVisible);
  }

  @Test
  void clickPrefabButton_WhenClicked_ClonesPrefabAndTriggersCallback() {
    interact(() -> prefabPanel.setPrefabScene(prefabScene));
    clickOn(".button");
    assertTrue(prefabInstantiated);
  }

  @Test
  void setPrefabScene_WithEmptyScene_ClearsAllPrefabs() {
    interact(() -> {
      prefabPanel.setPrefabScene(prefabScene);
      prefabScene.unregisterObject(prefab);
      prefabPanel.prefabsSync();
    });

    // Should be empty now
    assertEquals(0, prefabPanel.lookupAll(".button").size());
  }
}
