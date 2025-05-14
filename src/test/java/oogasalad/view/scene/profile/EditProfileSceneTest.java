package oogasalad.view.scene.profile;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.util.NodeQueryUtils.isVisible;

import javafx.stage.Stage;
import oogasalad.database.FirebaseManager;
import oogasalad.model.config.GameConfig;
import oogasalad.model.profile.PlayerData;
import oogasalad.model.profile.SessionManagement;
import oogasalad.view.scene.MainViewManager;
import oogasalad.view.scene.menu.MainMenuScene;
import org.junit.jupiter.api.Test;
import util.DukeApplicationTest;

class EditProfileSceneTest extends DukeApplicationTest {

  @Override
  public void start(Stage stage) throws Exception {
    FirebaseManager.initializeFirebase();

    PlayerData mockPlayer = mock(PlayerData.class);
    SessionManagement.getInstance().login(mockPlayer, false);

    when(mockPlayer.getUsername()).thenReturn("test");
    when(mockPlayer.getFullName()).thenReturn("Test User");
    when(mockPlayer.getBio()).thenReturn("Hello I'm a test user");
    when(mockPlayer.getImageUrl()).thenReturn("file:test.png");

    MainViewManager viewManager = MainViewManager.getInstance();
    viewManager.addViewScene(MainMenuScene.class, GameConfig.getText("defaultScene"));

    viewManager.addViewScene(ProfileScene.class, "ProfileScene");
    ((ProfileScene) MainViewManager.getInstance()
        .getViewScene("ProfileScene"))
        .setUsername("test");
    viewManager.addViewScene(EditProfileScene.class, "EditProfileScene");
    viewManager.switchTo("EditProfileScene");
  }

  @Test
  public void testBackButton() {
    clickOn("#returnButton");
  }

  // NOTE: FileChooser only supported locally, rejected by pipeline
  // @Test
  // public void testUploadPhotoButton() {

    // clickOn("#uploadPhotoButton");
  // }

  @Test
  public void testEditProfileButton() {
    clickOn("#submitButton");
  }

  @Test
  public void testEditProfileInformation() {
    clickOn("#nameTextField").write("new name");
    clickOn("#bioTextArea").write("new bio");
  }

  @Test
  public void testLabels_Visible() {
    verifyThat("#photoLabel", isVisible());
    verifyThat("#selectedFileLabel", isVisible());
    verifyThat("#nameLabel", isVisible());
    verifyThat("#usernameValue", isVisible());
    verifyThat("#bioLabel", isVisible());
  }
}