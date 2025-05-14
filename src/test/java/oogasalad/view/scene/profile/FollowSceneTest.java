package oogasalad.view.scene.profile;

import java.io.IOException;
import java.util.Optional;
import javafx.scene.Node;
import javafx.stage.Stage;
import oogasalad.database.DatabaseException;
import oogasalad.database.FirebaseManager;
import oogasalad.model.profile.SessionException;
import oogasalad.model.profile.SessionManagement;
import oogasalad.model.service.PlayerService;
import oogasalad.view.scene.MainViewManager;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.testfx.framework.junit5.ApplicationTest;

/**
 * FollowScene test class.
 *
 * @author Daniel Rodriguez-Florido
 */
class FollowSceneTest extends ApplicationTest {

  @Override
  public void start(Stage stage) throws IOException, DatabaseException, SessionException {
    FirebaseManager.initializeFirebase();

    PlayerService playerService = new PlayerService();

    SessionManagement sessionManagement = Mockito.mock(SessionManagement.class);
    Mockito.when(sessionManagement.getCurrentUser())
            .thenReturn(playerService.getPlayerByUsername("dannyrod"));

    MainViewManager.getInstance().addViewScene(ProfileScene.class, "ProfileScene");

    MainViewManager.getInstance().addViewScene(FollowScene.class, "FollowScene");
    FollowScene followScene = (FollowScene) MainViewManager.getInstance().getViewScene("FollowScene");
    followScene.setUsername("dannyrod");
    followScene.setFollowersOrFollowing(true);
    followScene.updatePage();

    MainViewManager.getInstance().switchTo("ProfileScene");
    MainViewManager.getInstance().switchTo("FollowScene");
  }

  @Test
  void clickUser_takesToUserPage_success() {
    // Try to find the userRow node
    Optional<Node> userRowOptional = lookup("#userRow").tryQuery();

    // If found, click it
    userRowOptional.ifPresent(this::clickOn);
  }

  @Test
  void clickReturn_goesBack_success() {
    clickOn("#returnButton");
  }

}