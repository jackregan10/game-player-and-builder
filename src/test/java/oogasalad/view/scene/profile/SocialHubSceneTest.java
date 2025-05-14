package oogasalad.view.scene.profile;

import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.util.NodeQueryUtils.isVisible;

import java.io.IOException;
import javafx.stage.Stage;
import oogasalad.database.DatabaseException;
import oogasalad.database.FirebaseManager;
import oogasalad.model.config.GameConfig;
import oogasalad.model.profile.SessionException;
import oogasalad.view.scene.MainViewManager;
import oogasalad.view.scene.leaderboard.LeaderboardScene;
import oogasalad.view.scene.menu.MainMenuScene;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

public class SocialHubSceneTest extends ApplicationTest {

  @Override
  public void start(Stage stage)
      throws IOException, SessionException, DatabaseException {
    FirebaseManager.initializeFirebase();

    MainViewManager viewManager = MainViewManager.getInstance();
    viewManager.addViewScene(ProfileScene.class, "ProfileScene");
    viewManager.addViewScene(MainMenuScene.class, GameConfig.getText("defaultScene"));
    viewManager.addViewScene(SearchProfileScene.class, GameConfig.getText("SearchProfileScene"));
    viewManager.addViewScene(LeaderboardScene.class, GameConfig.getText("LeaderboardScene"));

    viewManager.addViewScene(SocialHubScene.class, "SocialHubScene");
    SocialHubScene socialHubScene = (SocialHubScene) MainViewManager.getInstance()
        .getViewScene("SocialHubScene");

    socialHubScene.setUsername("test");
    socialHubScene.updatePage();

    ProfileScene profileScene = (ProfileScene) MainViewManager.getInstance()
        .getViewScene("ProfileScene");

    profileScene.setUsername("test");
    profileScene.updatePage();

    viewManager.switchTo("SocialHubScene");

  }

  @Test
  public void socialInfo_infoAppears_success() {
    verifyThat("#socialHubTitle", isVisible());
    verifyThat("#socialHubSubtitle", isVisible());
  }

  @Test
  public void profileSearchButton_clicked_success() {
    clickOn("#profileSearchButton");
  }

  @Test
  public void leaderboardButton_clicked_success() {
    clickOn("#leaderboardButton");
  }

  @Test
  public void profileButton_clicked_success() {
    clickOn("#socialHubProfileImage");
  }

  @Test
  public void returnButton_clicked_success() {
    clickOn("#returnButton");
  }

}
