package oogasalad.view.scene.auth;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import javafx.stage.Stage;
import oogasalad.controller.GameController;
import oogasalad.database.DatabaseException;
import oogasalad.database.FirebaseManager;
import oogasalad.model.config.GameConfig;
import oogasalad.model.service.PlayerService;
import oogasalad.view.scene.MainViewManager;
import oogasalad.view.scene.menu.MainMenuScene;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.testfx.framework.junit5.ApplicationTest;

class SignUpSceneTest extends ApplicationTest {
  private final PlayerService playerService = new PlayerService();
  @Override
  public void start(Stage stage) throws IOException {
    FirebaseManager.initializeFirebase();

    MainViewManager viewManager = MainViewManager.getInstance();
    viewManager.addViewScene(MainMenuScene.class, GameConfig.getText("defaultScene"));

    Mockito.spy(new GameController());
    viewManager.addViewScene(SignUpScene.class, "SignUpScene");
    viewManager.addViewScene(LogInScene.class, "LogInScene");

    viewManager.switchTo("SignUpScene");
  }

  @Test
  void signUpButtons_correctLogin_changeInScene() throws DatabaseException {
    String username = "testSignUp";
    clickOn("#signUpFirstNameField").write("test1");
    clickOn("#signUpLastNameField").write("test2");
    clickOn("#socialEmailPrompt").write(username);
    clickOn("#socialPasswordPrompt").write("password");
    clickOn("#SignUpButton");

    assertTrue(playerService.deletePlayer(username));
  }

  @Test
  void handleLogInButton_correctLogin_changeInScene() {
    clickOn("#LoginButton");
  }
}