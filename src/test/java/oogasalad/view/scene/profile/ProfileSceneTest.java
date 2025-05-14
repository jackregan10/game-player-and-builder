package oogasalad.view.scene.profile;

import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.util.NodeQueryUtils.isVisible;

import com.google.cloud.Timestamp;
import java.io.IOException;
import java.util.Collections;
import javafx.stage.Stage;
import oogasalad.controller.GameController;
import oogasalad.database.DatabaseException;
import oogasalad.database.FirebaseManager;
import oogasalad.model.config.GameConfig;
import oogasalad.model.profile.Password;
import oogasalad.model.profile.PlayerData;
import oogasalad.model.profile.SessionException;
import oogasalad.model.service.PlayerService;
import oogasalad.model.service.SocialService;
import oogasalad.view.scene.MainViewManager;
import oogasalad.view.scene.menu.MainMenuScene;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.testfx.framework.junit5.ApplicationTest;

class ProfileSceneTest extends ApplicationTest {

  @Override
  public void start(Stage stage)
      throws IOException, SessionException, DatabaseException {
    FirebaseManager.initializeFirebase();

    MainViewManager viewManager = MainViewManager.getInstance();
    viewManager.addViewScene(MainMenuScene.class, GameConfig.getText("defaultScene"));

    viewManager.addViewScene(ProfileScene.class, "ProfileScene");
    ProfileScene profileScene = (ProfileScene) MainViewManager.getInstance().getViewScene("ProfileScene");

    profileScene.setUsername("test");
    profileScene.updatePage();

    viewManager.switchTo("ProfileScene");
  }

  @Test
  void profileInfo_infoAppears_success() {
    verifyThat("#username", isVisible());
    verifyThat("#fullName", isVisible());
    verifyThat("#dateCreated", isVisible());
    verifyThat("#bio", isVisible());
  }

  @Test
  void clickOnBackButton_goesBack_success() {
    clickOn("#returnButton");
  }

  @Test
  void clickOnFollowersButton_generatesFollowersPage_success() {
    clickOn("#followerButton");
  }

  @Test
  void clickOnFollowingButton_generatesFollowingPage_success() {
    clickOn("#followingButton");
  }
}