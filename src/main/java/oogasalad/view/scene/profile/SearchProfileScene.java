package oogasalad.view.scene.profile;

import static oogasalad.model.config.GameConfig.LOGGER;
import java.util.List;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import oogasalad.controller.GameController;
import oogasalad.database.DatabaseException;
import oogasalad.model.config.GameConfig;
import oogasalad.model.profile.PlayerData;
import oogasalad.view.config.StyleConfig;
import oogasalad.view.gui.error.PopUpError;
import oogasalad.view.scene.MainViewManager;
import oogasalad.view.scene.ViewScene;

/**
 * SearchProfileScene represents the UI used to search to find a user
 *
 * @author Daniel Rodriguez-Florido
 */
public class SearchProfileScene extends ViewScene {

  private static final int VBOX_SPACING = 20;
  private static final int CARD_WIDTH = 500;
  private static final int TOP_PADDING = 40;

  private static final String PROFILE_SEARCH_ID = "profileSearch";

  private final GameController gameController = new GameController();

  /**
   * Initialize the viewScene with the given mainStage.
   *
   * @param stage the given stage of this window scene.
   */
  protected SearchProfileScene(Stage stage) throws DatabaseException {
    super(stage);

    StackPane root = new StackPane();
    Scene scene = new Scene(root);
    StyleConfig.registerScene(scene);
    stage.setScene(scene);

    VBox card = new VBox();
    configureCard(card);

    setUpTitle(card);
    setUpListView(card);
    setUpConfigButtons(root);

    root.getChildren().add(card);
  }

  private void configureCard(VBox card) {
    card.setMaxWidth(CARD_WIDTH);
    card.setAlignment(Pos.TOP_CENTER);
    card.setSpacing(VBOX_SPACING);
  }

  private void setUpTitle(VBox card) {
    Label title = new Label(GameConfig.getText(PROFILE_SEARCH_ID));
    title.setPadding(new Insets(TOP_PADDING, 0, 0, 0));
    title.setId("title");
    card.getChildren().add(title);
  }

  private void setUpConfigButtons(StackPane root) {
    VBox utilButtons = new VBox();
    configureUtilButtonsBox(utilButtons);

    Button retButton = new Button(GameConfig.getText("returnButton"));
    retButton.setId("returnButton");
    retButton.setOnAction(e -> MainViewManager.getInstance().switchToPrevScene());
    utilButtons.getChildren().add(retButton);
    root.getChildren().add(utilButtons);
  }

  private void configureUtilButtonsBox(VBox utilButtons) {
    utilButtons.setAlignment(Pos.TOP_LEFT);
  }

  private void setUpListView(VBox card) throws DatabaseException {
    List<PlayerData> fetched;
    try {
      fetched = gameController.getAllPlayers();
    } catch (DatabaseException e) {
      LOGGER.error(GameConfig.getText("couldNotFetchFollowerFollowing"));
      PopUpError.showError("Couldn't fetch follower following players");
      throw new DatabaseException(GameConfig.getText("couldNotFetchFollowerFollowing"), e);
    }

    VBox listView = UserListViewFactory.setUpListPlayerDataView(fetched);

    card.getChildren().add(listView);
  }
  
}
