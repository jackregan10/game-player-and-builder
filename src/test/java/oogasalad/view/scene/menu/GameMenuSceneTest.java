package oogasalad.view.scene.menu;

import javafx.stage.Stage;
import oogasalad.model.config.GameConfig;
import oogasalad.view.scene.MainViewManager;
import oogasalad.view.scene.display.GameDisplayScene;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

public class GameMenuSceneTest extends ApplicationTest {

  @Override
  public void start(Stage stage) {
    MainViewManager viewManager = MainViewManager.getInstance();

    viewManager.addViewScene(MainMenuScene.class, GameConfig.getText("defaultScene"));
    viewManager.addViewScene(GameDisplayScene.class, "GameDisplayScene");

    viewManager.addViewScene(GameMenuScene.class, "GameMenuScene");

    viewManager.switchTo("GameMenuScene");
  }

  @Test
  void clickOnDino_enterDinoGame() {
    clickOn("#dinoButton");
  }

  @Test
  void clickOnGeo_enterGeoGame() {
    clickOn("#geoButton");
  }

  @Test
  void clickOnDoodle_enterDoodleGame() {
    clickOn("#doodleButton");
  }
  // Note: runs locally but file selection operation fails pipeline
  // @Test
  // void clickOn_selectGame() {
    // clickOn("#gameSelector");
  // }

  @Test
  void clickOn_returnButton() {
    clickOn("#returnButton");
  }
}
