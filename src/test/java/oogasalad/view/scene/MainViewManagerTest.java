package oogasalad.view.scene;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

import javafx.application.Platform;
import javafx.stage.Stage;
import oogasalad.model.config.GameConfig;
import oogasalad.view.scene.auth.LogInScene;
import oogasalad.view.scene.menu.MainMenuScene;
import org.junit.jupiter.api.Test;
import org.testfx.util.WaitForAsyncUtils;
import util.DukeApplicationTest;

class MainViewManagerTest extends DukeApplicationTest {

  private MainViewManager manager;

  @Override
  public void start(Stage stage) {
    manager = MainViewManager.getInstance();
    manager.addViewScene(MainMenuScene.class, GameConfig.getText("defaultScene"));
  }

  @Test
  void addAndGetViewScene_addsViewScene_returnsViewScene() {
    // fire event on the FX thread
    ViewScene retrieved = manager.getViewScene("MainMenuScene");
    assertNotNull(retrieved);
    assertInstanceOf(MainMenuScene.class, retrieved);
  }
}