package oogasalad.view.scene.profile;

import java.io.IOException;
import javafx.scene.Node;
import javafx.stage.Stage;
import oogasalad.database.DatabaseException;
import oogasalad.database.FirebaseManager;
import oogasalad.model.profile.SessionException;
import oogasalad.view.scene.MainViewManager;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

/**
 * Testing class for SearchProfileScene
 *
 * @author Daniel Rodriguez-Florido
 */
class SearchProfileSceneTest extends ApplicationTest {

  @Override
  public void start(Stage stage) throws IOException, DatabaseException, SessionException {
    FirebaseManager.initializeFirebase();

    MainViewManager.getInstance().addViewScene(ProfileScene.class, "ProfileScene");
    ((ProfileScene) MainViewManager.getInstance().getViewScene("ProfileScene"))
        .setUsername("dannyrod");
    ((ProfileScene) MainViewManager.getInstance().getViewScene("ProfileScene"))
        .updatePage();
    MainViewManager.getInstance().switchTo("ProfileScene");

    MainViewManager.getInstance().addViewScene(SearchProfileScene.class, "SearchProfileScene");
    MainViewManager.getInstance().switchTo("SearchProfileScene");
  }

  @Test
  void testSearchProfile_findsProfile_success() {
    clickOn("#searchBar").write("dannyrodflo");
    Node firstRow = lookup("#listView").lookup("#userRow").nth(0).query();
    clickOn(firstRow);
  }

  @Test
  void returnButton_goesBack_success() {
    clickOn("#returnButton");
  }

}