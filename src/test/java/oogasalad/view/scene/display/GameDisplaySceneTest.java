package oogasalad.view.scene.display;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

import javafx.application.Platform;
import javafx.stage.Stage;
import oogasalad.database.FirebaseManager;
import oogasalad.model.config.GameConfig;
import oogasalad.model.engine.architecture.Game;
import oogasalad.model.engine.architecture.GameObject;
import oogasalad.model.engine.architecture.GameScene;
import oogasalad.view.scene.MainViewManager;
import oogasalad.view.scene.menu.MainMenuScene;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import java.io.IOException;

class GameDisplaySceneTest extends ApplicationTest {

  GameDisplayScene gameDisplayScene;

  @Override
  public void start(Stage stage) throws IOException {
    FirebaseManager.initializeFirebase();
    MainViewManager viewManager = MainViewManager.getInstance();

    viewManager.addViewScene(MainMenuScene.class, GameConfig.getText("defaultScene"));
    viewManager.switchToMainMenu();
    viewManager.addViewScene(GameDisplayScene.class, "GameDisplayScene");

    gameDisplayScene = (GameDisplayScene) viewManager.getViewScene("GameDisplayScene");
    gameDisplayScene.play("data/GameJsons/GEODASH/GEO_01_SingleLevel/GEO_01_SingleLevel.json");

    viewManager.switchTo("GameDisplayScene");
  }

  @Test
  void play_startDisplayGame_InvalidGameExceptionCaught() {
    gameDisplayScene.onDeactivate();

    Game game = new Game();
    GameScene gameScene = new GameScene("test");
    GameObject gameObject = new GameObject("testObject");
    gameScene.registerObject(gameObject);
    game.addScene(gameScene);

    Platform.runLater(() -> assertDoesNotThrow(() -> gameDisplayScene.play(game)));
  }

  @Test
  void clickReturnButton_whenInGameDisplay_shouldSwitchToMainMenuScene() {
    clickOn("#returnButton");
  }

  @Test
  void clickPauseButton_shouldPauseGameAndShowOverlay() {
    clickOn("#pauseButton");

    verifyPauseOverlayVisible(true);
    verifyPauseButtonText(GameConfig.getText("resumeButton"));
  }

  @Test
  void clickPauseButtonTwice_shouldResumeGameAndHideOverlay() {
    clickOn("#pauseButton"); // pause
    clickOn("#pauseButton"); // resume

    verifyPauseOverlayVisible(false);
    verifyPauseButtonText(GameConfig.getText("pauseButton"));
  }

  private void verifyPauseOverlayVisible(boolean expectedVisible) {
    assertEquals(expectedVisible,
        lookup("#pause-overlay").queryLabeled().isVisible(),
        "Pause overlay visibility should match expected: " + expectedVisible);
  }

  private void verifyPauseButtonText(String expectedText) {
    assertEquals(expectedText,
        lookup("#pauseButton").queryButton().getText(),
        "Pause button text should match expected: " + expectedText);
  }
}