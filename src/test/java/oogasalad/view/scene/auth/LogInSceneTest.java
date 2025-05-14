package oogasalad.view.scene.auth;

import com.google.cloud.Timestamp;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import javafx.stage.Stage;
import oogasalad.controller.GameController;
import oogasalad.database.DatabaseException;
import oogasalad.database.FirebaseManager;
import oogasalad.model.config.GameConfig;
import oogasalad.model.config.PasswordHashingException;
import oogasalad.model.profile.Password;
import oogasalad.model.profile.PlayerData;
import oogasalad.model.profile.SessionManagement;
import oogasalad.model.service.PlayerService;
import oogasalad.view.scene.MainViewManager;
import oogasalad.view.scene.menu.MainMenuScene;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.testfx.framework.junit5.ApplicationTest;

class LogInSceneTest extends ApplicationTest {

  PlayerService playerService = new PlayerService();

  @BeforeEach
  void setUp() throws DatabaseException, PasswordHashingException {
    playerService.createNewPlayer(new PlayerData("t1", "n1", Password.fromPlaintext("p1"), Timestamp.now(), null, null, null));
  }
  @AfterEach
  public void cleanUp() throws DatabaseException {
    playerService.deletePlayer("t1");
    SessionManagement.getInstance().logout(); // reset session
    try {
      Files.deleteIfExists(Paths.get("rememberme.properties"));
    } catch (IOException e) {
      System.err.println("Error cleaning up user properties: " + e.getMessage());
    }
  }

  @Override
  public void start(Stage stage) throws IOException {
    FirebaseManager.initializeFirebase();

    MainViewManager viewManager = MainViewManager.getInstance();
    viewManager.addViewScene(MainMenuScene.class, GameConfig.getText("defaultScene"));

    viewManager.addViewScene(LogInScene.class, "LogInScene");
    viewManager.switchTo("LogInScene");
  }

  @Test
  void loginButtons_incorrectLogin_notChangeInScene() {
    clickOn("#socialEmailPrompt").write("t1");
    clickOn("#socialPasswordPrompt").write("p");
    clickOn("#rememberMeCheckbox");
    clickOn("#socialLoginButton");
  }

  @Test
  void loginButtons_correctLogin_changeInScene() {
    clickOn("#socialEmailPrompt").write("t1");
    clickOn("#socialPasswordPrompt").write("p1");

    clickOn("#togglePasswordButton");
    clickOn("#togglePasswordButton");


    clickOn("#socialLoginButton");
  }
}