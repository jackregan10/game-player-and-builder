package oogasalad;

import java.io.IOException;
import javafx.application.Application;
import javafx.stage.Stage;
import oogasalad.database.FirebaseManager;
import oogasalad.model.config.GameConfig;
import oogasalad.model.profile.SessionManagement;
import oogasalad.view.scene.MainViewManager;
import oogasalad.view.scene.auth.LogInScene;
import oogasalad.view.scene.auth.SignUpScene;
import oogasalad.view.scene.builder.BuilderScene;
import oogasalad.view.scene.display.GameDisplayScene;
import oogasalad.view.scene.leaderboard.LeaderboardScene;
import oogasalad.view.scene.menu.GameMenuScene;
import oogasalad.view.scene.menu.MainMenuScene;
import oogasalad.view.scene.profile.EditProfileScene;
import oogasalad.view.scene.profile.FollowScene;
import oogasalad.view.scene.profile.ProfileScene;
import oogasalad.view.scene.profile.SearchProfileScene;
import oogasalad.view.scene.profile.SocialHubScene;

/**
 * This is the main class of the OOGASalad Platformer Game Sandbox. Run the start method to open the
 * program.
 */
public class Main extends Application {

  private static final String LOGIN_SCENE_NAME = "LogInScene";

  /**
   * Start of the program
   */
  public static void main(String[] args) {
    launch(args);
  }

  /**
   * Create a new game and open the gui.
   *
   * @param stage the primary stage for this application, onto which the application scene can be
   *              set. Applications may create other stages, if needed, but they will not be primary
   *              stages.
   */
  @Override
  public void start(Stage stage) throws IOException {
    FirebaseManager.initializeFirebase();

    MainViewManager viewManager = MainViewManager.getInstance();
    setupViewScenes(viewManager);

    if (SessionManagement.getInstance().tryAutoLogin()) {
      viewManager.switchToMainMenu();
    } else {
      viewManager.switchTo(LOGIN_SCENE_NAME);
    }
  }

  private void setupViewScenes(MainViewManager viewManager) {
    GameConfig.renderScenes(viewManager);
  }

}