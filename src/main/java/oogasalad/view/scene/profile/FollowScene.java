package oogasalad.view.scene.profile;

import java.util.List;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import oogasalad.controller.SocialController;
import oogasalad.database.DatabaseException;
import oogasalad.model.config.GameConfig;
import oogasalad.model.profile.PlayerData;
import oogasalad.view.config.StyleConfig;
import oogasalad.view.scene.MainViewManager;
import oogasalad.view.scene.ViewScene;
import static oogasalad.model.config.GameConfig.LOGGER;
import static oogasalad.model.config.GameConfig.getText;

/**
 * Front-end class for the follow display page
 *
 * @author Daniel Rodriguez-Florido
 */
public class FollowScene extends ViewScene {

  private static final String FOLLOWERS_TITLE_ID = "followTitle";
  private static final String FOLLOWERS_LABEL_ID = "followerLabel";
  private static final String FOLLOWING_LABEL_ID = "followingLabel";
  private static final String RETURN_BUTTON_ID = "returnButton";
  private static final int VBOX_SPACING = 20;
  private static final int FOLLOW_CARD_WIDTH = 500;
  private static final int TOP_PADDING = 20;

  private final SocialController socialController = new SocialController();

  private final StackPane root;
  private String username;
  private boolean followersOrFollowing; // True to generate followers, false for following
  private Label title;

  /**
   * Constructor for the FollowScene front-end
   * Initialize the viewScene with the given mainStage.
   *
   * @param stage the given stage of this window scene.
   */
  protected FollowScene(Stage stage)  {
    super(stage);

    root = new StackPane();
    Scene scene = new Scene(root);
    StyleConfig.registerScene(scene);
    stage.setScene(scene);

    configureUtilityButtons();
  }

  private void configureUtilityButtons() {
    VBox utilBox = new VBox();

    Button backButton = new Button(GameConfig.getText(RETURN_BUTTON_ID));
    backButton.setOnAction(e -> MainViewManager.getInstance().switchToPrevScene());
    backButton.setId(RETURN_BUTTON_ID);

    utilBox.getChildren().add(backButton);
    utilBox.setAlignment(Pos.TOP_LEFT);

    root.getChildren().add(utilBox);
  }

  private void configureFollowCard(VBox followCard) {
    followCard.setMaxWidth(FOLLOW_CARD_WIDTH);
    followCard.setAlignment(Pos.TOP_CENTER);
  }

  private void setUpLabel(VBox card) {
    title = new Label(GameConfig.getText(FOLLOWERS_TITLE_ID));
    title.setPadding(new Insets(TOP_PADDING, 0, 0, 0));
    title.setId(FOLLOWERS_TITLE_ID);
    card.getChildren().add(title);
  }

  private void setUpListView(VBox card) throws DatabaseException {
    List<PlayerData> fetched;
    try {
      fetched = followersOrFollowing ? socialController.handleGetFollowers(username) :
          socialController.handleGetFollowings(username);
      int fetchedCount = fetched.size();

      title.setText(followersOrFollowing ?
          getText(FOLLOWERS_LABEL_ID, fetchedCount)
          : getText(FOLLOWING_LABEL_ID, fetchedCount));
    } catch (DatabaseException e) {
      LOGGER.error(GameConfig.getText("couldNotFetchFollowerFollowing"));
      // TODO: Show user message
      throw new DatabaseException(GameConfig.getText("couldNotFetchFollowerFollowing"), e);
    }

    VBox listView = UserListViewFactory.setUpListPlayerDataView(fetched);

    card.getChildren().add(listView);
  }

  /**
   * Sets the username needed to access this screen
   *
   * @param username - the username of the whoever followers/followings being looked at
   */
  public void setUsername(String username) {
    this.username = username;
  }

  /**
   * Sets the field to generate followers or following for the user
   * @param followersOrFollowing True for generating followers list, false for following
   */
  public void setFollowersOrFollowing(boolean followersOrFollowing) {
    this.followersOrFollowing = followersOrFollowing;
  }

  /**
   * Meant to be called after setting the parameters (username and followers/following)
   * to generate the proper UI and update the page.
   * @throws DatabaseException if failure to fetch information from database
   */
  public void updatePage() throws DatabaseException {
    VBox followCard = new VBox(VBOX_SPACING);

    configureFollowCard(followCard);
    setUpLabel(followCard);
    setUpListView(followCard);

    root.getChildren().add(followCard);
  }

}
