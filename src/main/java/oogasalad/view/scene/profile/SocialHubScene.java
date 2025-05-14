package oogasalad.view.scene.profile;

import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import oogasalad.controller.GameController;
import oogasalad.database.DatabaseException;
import oogasalad.model.config.GameConfig;
import oogasalad.model.profile.PlayerData;
import oogasalad.model.profile.SessionException;
import oogasalad.model.profile.SessionManagement;
import oogasalad.view.config.StyleConfig;
import oogasalad.view.scene.MainViewManager;
import oogasalad.view.scene.ViewScene;
import oogasalad.view.scene.leaderboard.LeaderboardScene;

import static oogasalad.model.config.GameConfig.LOGGER;

/**
 * Represents the Social Hub Scene of the application, where users can access features such as
 * profile search, leaderboard viewing, and return to previous scenes. It displays a personalized
 * message and the current user's profile image.
 */
public class SocialHubScene extends ViewScene {

  private static final String PROFILE_SCENE_NAME = "ProfileScene";
  private static final String PROFILE_SEARCH_SCENE_NAME = "SearchProfileScene";
  private static final String PROFILE_SEARCH_BUTTON_ID = "profileSearchButton";
  private static final String LEADERBOARD_BUTTON_ID = "leaderboardButton";
  private static final String LOGIN_SCENE_ID = "LoginScene";
  private static final String BACK_BUTTON_ID = "returnButton";
  private static final String PROFILE_IMAGE_ID = "socialHubProfileImage";
  private static final String SOCIAL_HUB_TITLE = "socialHubTitle";
  private static final String SOCIAL_HUB_SUBTITLE = "socialHubSubtitle";
  private static final int VBOX_SPACING = 20;

  private final BorderPane root = new BorderPane();
  private final VBox socialHubCard;
  private String username = "test";
  private PlayerData playerData;
  private ImageView profileImageView;

  /**
   * Constructs a SocialHubScene with the specified stage and initializes the UI.
   *
   * @param stage the primary stage on which the scene is set
   */
  private SocialHubScene(Stage stage) {
    super(stage);
    Scene scene = new Scene(root);

    StyleConfig.registerScene(scene);
    stage.setScene(scene);

    socialHubCard = new VBox(VBOX_SPACING);
    socialHubCard.setAlignment(Pos.CENTER);
    root.setCenter(socialHubCard);

    buildSocialHubUi();
  }

  /**
   * Retrieves player data based on the current username from the session. If the player is not
   * found, redirects to the login scene.
   */
  private void setPlayerData() {
    GameController gameController = new GameController();
    try {
      playerData = gameController.getPlayerByUsername(username);
    } catch (DatabaseException e) {
      LOGGER.error(GameConfig.getText("noSuchPlayer"));
      MainViewManager.getInstance().switchTo(LOGIN_SCENE_ID);
    }
  }

  /**
   * Constructs the full UI layout for the social hub, including buttons and welcome message.
   */
  private void buildSocialHubUi() {
    setUpSocialHubMessage();
    setUpProfileSearchButton();
    setUpLeaderboardButton();
    setUpReturnButton();
  }

  /**
   * Displays the main title and subtitle in the center VBox.
   */
  private void setUpSocialHubMessage() {
    Label title = new Label(GameConfig.getText(SOCIAL_HUB_TITLE));
    title.setId(SOCIAL_HUB_TITLE);
    Label subtitle = new Label(GameConfig.getText(SOCIAL_HUB_SUBTITLE));
    subtitle.setId(SOCIAL_HUB_SUBTITLE);
    socialHubCard.getChildren().addAll(title, subtitle);
  }

  /**
   * Creates and sets the return button at the top of the BorderPane. Navigates to the previous
   * scene when clicked.
   */
  private void setUpReturnButton() {
    Button returnButton = new Button(GameConfig.getText(BACK_BUTTON_ID));
    returnButton.setId(BACK_BUTTON_ID);
    returnButton.setOnAction(e -> MainViewManager.getInstance().switchToPrevScene());
    returnButton.setAlignment(Pos.TOP_LEFT);
    root.setTop(returnButton);
  }

  /**
   * Creates and adds a button to the VBox that will eventually lead to the leaderboard scene.
   */
  private void setUpLeaderboardButton() {
    Button leaderboardButton = new Button(GameConfig.getText("leaderboardButton"));
    leaderboardButton.setId(LEADERBOARD_BUTTON_ID);
    leaderboardButton.setOnAction(e -> {
      MainViewManager.getInstance().addViewScene(LeaderboardScene.class, "LeaderboardScene");
      MainViewManager.getInstance().switchTo("LeaderboardScene");
    });
    socialHubCard.getChildren().add(leaderboardButton);
  }

  /**
   * Creates and adds a button to the VBox that navigates to the profile search scene.
   */
  private void setUpProfileSearchButton() {
    Button profileSearchButton = new Button(GameConfig.getText("profileSearchButton"));
    profileSearchButton.setId(PROFILE_SEARCH_BUTTON_ID);
    profileSearchButton.setOnAction(
        e -> {
          MainViewManager.getInstance().addViewScene(SearchProfileScene.class, PROFILE_SEARCH_SCENE_NAME);
          MainViewManager.getInstance().switchTo(PROFILE_SEARCH_SCENE_NAME);
        });
    socialHubCard.getChildren().add(profileSearchButton);
  }

  /**
   * Creates and displays the current user's circular profile image, with an event handler to switch
   * to the profile scene when clicked.
   */
  private void setUpProfileButton() {
    if (profileImageView == null) {
      profileImageView = new ImageView(playerData.getImageUrl());
      profileImageView.setFitWidth(80);
      profileImageView.setFitHeight(80);
      profileImageView.setId(PROFILE_IMAGE_ID);

      Circle clip = new Circle(40, 40, 40);
      profileImageView.setClip(clip);
      profileImageView.setCursor(Cursor.HAND);

      socialHubCard.getChildren().addAll(profileImageView);

      profileImageView.setOnMouseClicked(e -> {
        try {
          ((ProfileScene) MainViewManager.getInstance()
              .getViewScene(PROFILE_SCENE_NAME))
              .setUsername(SessionManagement.getInstance().getCurrentUser().getUsername());
          ((ProfileScene) MainViewManager.getInstance()
              .getViewScene(PROFILE_SCENE_NAME))
              .updatePage();
          MainViewManager.getInstance().switchTo(PROFILE_SCENE_NAME);
        } catch (SessionException ex) {
          LOGGER.error(GameConfig.getText("noSuchPlayer"));
        }
      });
    }
    profileImageView.setImage(new javafx.scene.image.Image(playerData.getImageUrl()));
  }

  /**
   * Sets the username for the currently logged-in player. This is used to retrieve player data and
   * personalize the scene.
   *
   * @param username the username of the logged-in user
   */
  public void setUsername(String username) {
    this.username = username;
  }

  /**
   * Updates the page to reflect the data for the current user. Should be called after setting the
   * username.
   */
  public void updatePage() {
    setPlayerData();
    setUpProfileButton();
  }
}
