package oogasalad.view.scene.profile;

import static oogasalad.model.config.GameConfig.LOGGER;
import static oogasalad.model.config.GameConfig.getText;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import oogasalad.controller.GameController;
import oogasalad.controller.LevelController;
import oogasalad.controller.SocialController;
import oogasalad.database.DatabaseException;
import oogasalad.database.FirebaseManager;
import oogasalad.model.config.GameConfig;
import oogasalad.model.profile.LevelData;
import oogasalad.model.profile.PlayerData;
import oogasalad.model.profile.SessionException;
import oogasalad.model.profile.SessionManagement;
import oogasalad.model.resource.ResourcePath;
import oogasalad.view.config.StyleConfig;
import oogasalad.view.gui.error.PopUpError;
import oogasalad.view.scene.MainViewManager;
import oogasalad.view.scene.ViewScene;
import oogasalad.view.scene.builder.BuilderScene;
import oogasalad.view.scene.display.GameDisplayScene;

/**
 * Profile view displaying user information and options for social interaction and profile editing.
 *
 * @author Jack Regan, Daniel Rodriguez-Florido
 */
public class ProfileScene extends ViewScene {

  private static final String BACK_BUTTON_ID                = "returnButton";
  private static final String PROFILE_IMAGE_ID              = "profileImage";
  private static final String FULL_NAME_ID                  = "fullName";
  private static final String USERNAME_ID                   = "username";
  private static final String DATE_CREATED_ID               = "dateCreated";
  private static final String BIO_ID                        = "bio";
  private static final String EDIT_PROFILE_BUTTON_ID        = "editProfileButton";
  private static final String FOLLOW_BUTTON_ID              = "followButton";
  private static final String FOLLOWER_BUTTON_ID            = "followerButton";
  private static final String FOLLOWING_BUTTON_ID           = "followingButton";
  private static final String UNFOLLOW_BUTTON_ID            = "unfollowButton";
  private static final String NO_SUCH_PLAYER_EXCEPTION_KEY  = "noSuchPlayer";
  private static final String ERROR_DETERMINING_FOLLOWER_KEY= "errorDeterminingFollower";
  private static final String ERROR_LEVEL_PLAYING_KEY       = "levelPlayingError";
  private static final String NO_BIO_TEXT_ID                = "noBioText";
  private static final String LOGIN_SCENE_ID                = "LogInScene";
  private static final String FOLLOWER_SCENE_ID             = "FollowScene";
  private static final String EDIT_PROFILE_SCENE_ID         = "EditProfileScene";
  private static final String NO_SUCH_USER_ERROR            = "noSuchUser";
  private static final String DATE_PATTERN                  = "MMMM dd, yyyy";
  private static final String PROFILE_DATE_JOINED_ID        = "profileDateJoined";
  private static final String NO_FOLLOWER_LIST_ERROR        = "noFollowerList";
  private static final String PROFILE_CARD_ID               = "profileCard";

  private static final String LEVEL_CONTAINER_ID            = "levelContainer";
  private static final String LEVEL_CARD_ID                 = "levelCard";
  private static final String LEVEL_PANE_ID                 = "levelPane";
  private static final String LEVEL_TITLE_ID                = "levelTitle";
  private static final String SINGLE_LEVEL_ID               = "singleLevel";
  private static final String LEVEL_PLAY_BUTTON_ID          = "levelPlayButton";
  private static final String LEVEL_EDIT_BUTTON_ID          = "levelEditButton";
  private static final String LEVEL_DELETE_BUTTON_ID        = "levelDeleteButton";
  private static final String LEVEL_DOWNLOAD_BUTTON_ID      = "levelDownloadButton";

  private static final int PROFILE_CARD_MAX_WIDTH           = 400;
  private static final int PROFILE_CARD_MAX_HEIGHT          = 600;
  private static final int VBOX_SPACING                     = 20;
  private static final int HBOX_SPACING                     = 10;
  private static final String GAME_PLAYER_SCENE_NAME        = "GamePlayerScene";
  private static final String BUILDER_SCENE_NAME            = "BuilderScene";


  private final SocialController socialController = new SocialController();
  private final BorderPane root = new BorderPane();

  private PlayerData playerData;
  private List<LevelData> levelData;

  private String username = "test";
  private final VBox profileCard;
  private final VBox levelCard;
  private LevelController levelController;


  /**
   * Constructs a new ProfileScene for the given stage.
   *
   * @param stage The primary stage for the application.
   */
  private ProfileScene(Stage stage) {
    super(stage);
    Scene scene = new Scene(root);

    StyleConfig.registerScene(scene);
    stage.setScene(scene);

    HBox container = new HBox(HBOX_SPACING);
    profileCard = new VBox(VBOX_SPACING);
    VBox levelContainer = new VBox(VBOX_SPACING);
    levelCard = new VBox(VBOX_SPACING);

    ScrollPane scrollPane = setupLevelScrollpane();

    setupLevelContainer(levelContainer, scrollPane);

    container.setAlignment(Pos.CENTER);
    container.getChildren().addAll(profileCard, levelContainer);

    root.setCenter(container);
    levelController = new LevelController();
  }

  private void setupLevelContainer(VBox levelContainer, ScrollPane scrollPane) {
    levelContainer.setId(LEVEL_CONTAINER_ID);
    levelContainer.getChildren().add(scrollPane);
    setFixedSize(levelContainer);
  }

  private ScrollPane setupLevelScrollpane() {
    ScrollPane scrollPane = new ScrollPane(levelCard);
    scrollPane.setId(LEVEL_PANE_ID);
    scrollPane.setFitToWidth(true);
    scrollPane.setFitToHeight(true);
    scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
    scrollPane.setFocusTraversable(false);
    setFixedSize(scrollPane);
    return scrollPane;
  }

  private void buildProfileUI() {
    playerData = setPlayerData();
    levelData = setLevelInformation();

    // All related to the profile card
    setupProfileCard(profileCard, PROFILE_CARD_ID);
    setUpProfileName(profileCard);
    setUpProfileImage(profileCard);
    setUpProfileInfo(profileCard);
    setUpFollowBox(profileCard);
    setUpUtilityButtons(profileCard);
    setUpBackButtons();

    //All related to social card
    setupProfileCard(levelCard, LEVEL_CARD_ID);
    setupLevelTitle(levelCard);
    setupLevels(levelCard);
  }

  private void setupLevelTitle(VBox levelCard) {
    Label title = new Label(getText(LEVEL_TITLE_ID));
    title.setId(LEVEL_TITLE_ID);
    title.setWrapText(true);
    levelCard.getChildren().add(title);
  }

  private void setupLevels(VBox levelCard) {
    levelData.forEach(singleLevelData -> {
      VBox level = new VBox(VBOX_SPACING);
      HBox buttons = new HBox(VBOX_SPACING);
      level.setId(SINGLE_LEVEL_ID);

      Label levelName = new Label(singleLevelData.getLevelName());
      Label levelDescription = new Label(singleLevelData.getLevelDescription());
      levelName.setId(LEVEL_TITLE_ID);

      try {
        createLevelButtonGroup(singleLevelData, buttons);
      } catch (IOException e) {
        LOGGER.error(GameConfig.getText("buttonError", e.getMessage()));
      }
      level.getChildren().addAll(levelName, levelDescription, buttons);
      levelCard.getChildren().add(level);

    });
  }

  private void createLevelButtonGroup(LevelData singleLevelData, HBox buttons) throws IOException {
    String levelName = singleLevelData.getLevelName().replace(" ", "_");

    File levelFolder = FirebaseManager.downloadLevelFolder(username, levelName);
    File jsonFile = new File(levelFolder, levelName+ ".json");

    if (!jsonFile.exists()) {
      throw new IOException("Level JSON file not found after download.");
    }

    Button playButton = createPlayButton(jsonFile, levelFolder);
    Button editButton = createEditButton(singleLevelData, jsonFile, levelFolder);
    Button downloadButton = createDownloadButton(jsonFile);
    Button deleteButton = createDeleteButton(singleLevelData);

    buttons.getChildren().addAll(playButton, editButton, downloadButton, deleteButton);
  }

  private Button createDeleteButton(LevelData singleLevelData) {
    return createButton(LEVEL_DELETE_BUTTON_ID, () -> {
      try {
        levelController.handleDeleteUserLevel(singleLevelData.getLevelName());
        levelData = setLevelInformation();
        updatePage();
      } catch (DatabaseException ex) {
        LOGGER.error(ex.getMessage());
        PopUpError.showError(GameConfig.getText("levelDeleteError", ex.getMessage()));
      }
    });
  }

  private Button createDownloadButton(File levelFile) {
    return createButton(getText(LEVEL_DOWNLOAD_BUTTON_ID), () -> {
      javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
      fileChooser.setTitle(GameConfig.getText("downloadSelectionInitialValue"));
      fileChooser.getExtensionFilters().add(new javafx.stage.FileChooser.ExtensionFilter("JSON Files", "*.json"));
      File saveFile = fileChooser.showSaveDialog(getStage());

      if (saveFile != null) {
        try {
          java.nio.file.Files.copy(
              levelFile.toPath(),
              saveFile.toPath(),
              java.nio.file.StandardCopyOption.REPLACE_EXISTING
          );
        } catch (IOException e) {
          LOGGER.error(GameConfig.getText("fileSaveError", e.getMessage()));
          PopUpError.showError(GameConfig.getText("fileSaveError", e.getMessage()));
        }
      }
    });
  }

  private Button createEditButton(LevelData singleLevelData, File jsonfile, File levelFolder) {
    return createButton(LEVEL_EDIT_BUTTON_ID, () -> {
      ResourcePath.setFromContext(levelFolder.getAbsolutePath());

      try {
        BuilderScene builderScene = ((BuilderScene) MainViewManager.getInstance().getViewScene(BUILDER_SCENE_NAME));
        builderScene.reload(jsonfile.getAbsolutePath());
        builderScene.setEditMode(singleLevelData);
        MainViewManager.getInstance().switchTo(BUILDER_SCENE_NAME);
      } catch (IllegalStateException e) {
        PopUpError.showError(GameConfig.getText(ERROR_LEVEL_PLAYING_KEY, e.getMessage()));
      }
    });
  }

  private Button createPlayButton(File jsonFile, File levelFolder) {
    return createButton(LEVEL_PLAY_BUTTON_ID, () -> {
        try {
          ResourcePath.setFromContext(levelFolder.getAbsolutePath());

          ((GameDisplayScene) MainViewManager.getInstance()
              .getViewScene(GAME_PLAYER_SCENE_NAME)).play(jsonFile.getAbsolutePath());
          MainViewManager.getInstance().switchTo(GAME_PLAYER_SCENE_NAME);
          jsonFile.deleteOnExit();
        } catch (IllegalStateException e) {
          PopUpError.showError(GameConfig.getText(ERROR_LEVEL_PLAYING_KEY, e.getMessage()));
        }
    });
  }


  private List<LevelData> setLevelInformation() {
    levelController = new LevelController();
    try {
       return levelController.handleRetrieveUserLevels(username);
    } catch (DatabaseException e) {
      LOGGER.error(e.getMessage());
      PopUpError.showError(GameConfig.getText(ERROR_LEVEL_PLAYING_KEY, e.getMessage()));
    }
    return new ArrayList<>();
  }



  private void setupProfileCard(VBox card, String id) {
    card.getChildren().clear();
    card.setMaxWidth(PROFILE_CARD_MAX_WIDTH);
    card.setMaxHeight(PROFILE_CARD_MAX_HEIGHT);
    card.setId(id);
  }

  private PlayerData setPlayerData() {
    GameController gameController = new GameController();
    try {
      playerData = gameController.getPlayerByUsername(username);
    } catch (DatabaseException e) {
      LOGGER.error(GameConfig.getText(NO_SUCH_PLAYER_EXCEPTION_KEY));
      MainViewManager.getInstance().switchTo(LOGIN_SCENE_ID);
    }
    return playerData;
  }

  private void setUpProfileName(VBox profileCard) {
    Label fullName = new Label(playerData.getFullName());
    fullName.setId(FULL_NAME_ID);
    profileCard.getChildren().add(fullName);
  }

  private void setUpBackButtons() {
    HBox backBox = new HBox(VBOX_SPACING);
    Button backButton = createButton(BACK_BUTTON_ID, () -> MainViewManager.getInstance().switchToPrevScene());

    backBox.getChildren().add(backButton);
    backBox.setAlignment(Pos.TOP_LEFT);
    root.setTop(backBox);
  }

  private void setUpUtilityButtons(VBox profileCard) {
    String currentUsername = getCurrentUsername();

    if (currentUsername.equals(playerData.getUsername())) {
      setUpEditButton(profileCard);
    } else {
      setUpFollowButton(profileCard);
    }
  }

  private void setUpEditButton(VBox profileCard) {
    Button editProfile = createButton(EDIT_PROFILE_BUTTON_ID, () -> {
      MainViewManager.getInstance().addViewScene(EditProfileScene.class, EDIT_PROFILE_SCENE_ID);
      MainViewManager.getInstance().switchTo(EDIT_PROFILE_SCENE_ID);
    });
    profileCard.getChildren().add(editProfile);
  }

  private void setUpFollowButton(VBox profileCard) {
    boolean isFollowing;

    try {
      isFollowing = socialController.isFollowing(getCurrentUsername(), playerData.getUsername());
    } catch (DatabaseException e) {
      LOGGER.error(GameConfig.getText(ERROR_DETERMINING_FOLLOWER_KEY, getCurrentUsername(), playerData.getUsername()));
      return;
    }

    Button followButton = isFollowing ?
        new Button(GameConfig.getText(UNFOLLOW_BUTTON_ID))
        : new Button(GameConfig.getText(FOLLOW_BUTTON_ID));

    followButton.setId(FOLLOW_BUTTON_ID);

    followButton.setOnAction(e -> {
      String currentUsername = getCurrentUsername();
      String followingUsername = playerData.getUsername();
      try {
        if (!socialController.isFollowing(currentUsername, followingUsername)) {
          socialController.handleFollowRequest(currentUsername, followingUsername);
          followButton.setText(GameConfig.getText(UNFOLLOW_BUTTON_ID));
        } else {
          socialController.handleUnfollowRequest(currentUsername, followingUsername);
          followButton.setText(GameConfig.getText(FOLLOW_BUTTON_ID));
        }
      } catch (DatabaseException ex) {
        LOGGER.error(GameConfig.getText("followerInformationError", currentUsername, followingUsername));
      }
    });

    profileCard.getChildren().add(followButton);
  }

  private String getCurrentUsername() {
    String currentUsername = "";
    try {
      currentUsername = SessionManagement.getInstance().getCurrentUser().getUsername();
    } catch (SessionException exc) {
      LOGGER.error(GameConfig.getText(NO_SUCH_USER_ERROR));
    }
    return currentUsername;
  }

  private void setUpProfileImage(VBox profileCard) {

    profileCard.setAlignment(Pos.CENTER);
    ImageView profileImage = new ImageView(playerData.getImageUrl());
    profileImage.setId(PROFILE_IMAGE_ID);
    profileImage.setFitWidth(80);
    profileImage.setFitHeight(80);

    Circle clip = new Circle(40, 40, 40); // centerX, centerY, radius
    profileImage.setClip(clip);

    profileCard.getChildren().add(profileImage);
  }

  private void setUpProfileInfo(VBox profileCard) {
    Label userName = new Label(GameConfig.getText(USERNAME_ID, playerData.getUsername()));
    userName.setId(USERNAME_ID);

    Label bio = new Label(playerData.getBio());
    if (playerData.getBio() == null) {
      bio.setText(GameConfig.getText(NO_BIO_TEXT_ID));
    }
    bio.setId(BIO_ID);

    Label dateCreated = setUpDateCreated();

    profileCard.getChildren().addAll(userName, bio, dateCreated); // add followerCount back after bug is fixed
  }

  private Label setUpDateCreated() {
    Instant instant = playerData.getCreatedAt().toDate().toInstant();
    ZoneId zoneId = ZoneId.systemDefault();
    Label dateCreated = new Label(
        GameConfig.getText(PROFILE_DATE_JOINED_ID, DateTimeFormatter.ofPattern(DATE_PATTERN, Locale.getDefault()).format(instant.atZone(zoneId))));
    dateCreated.setId(DATE_CREATED_ID);
    return dateCreated;
  }

  private void setUpFollowBox(VBox profileCard) {
    HBox followerBox = new HBox(HBOX_SPACING);
    configureFollowerBox(followerBox);

    configureFollowersButton(followerBox, username);
    configureFollowingButton(followerBox, username);

    profileCard.getChildren().add(followerBox);
  }

  private void configureFollowerBox(HBox followerBox) {
    followerBox.setAlignment(Pos.CENTER);
    followerBox.setSpacing(10);
    followerBox.setPickOnBounds(false);
  }

  private void configureFollowersButton(HBox followerBox, String username) {
    Button followerButton = new Button(GameConfig.getText(FOLLOWER_BUTTON_ID));
    followerButton.setId(FOLLOWER_BUTTON_ID);
    followerButton.setOnAction(e ->
      switchToFollowScene(username, true)
    );
    followerBox.getChildren().add(followerButton);
  }

  private void configureFollowingButton(HBox followerBox, String username) {
    Button followerButton = new Button(GameConfig.getText(FOLLOWING_BUTTON_ID));
    followerButton.setId(FOLLOWING_BUTTON_ID);
    followerButton.setOnAction(e ->
      switchToFollowScene(username, false)
    );
    followerBox.getChildren().add(followerButton);
  }

  private void switchToFollowScene(String username, boolean followersOrFollowing) {
    MainViewManager.getInstance().addViewScene(FollowScene.class, FOLLOWER_SCENE_ID);
    FollowScene followScene = (FollowScene) MainViewManager.getInstance().getViewScene(FOLLOWER_SCENE_ID);
    followScene.setFollowersOrFollowing(followersOrFollowing);
    followScene.setUsername(username);
    try {
      followScene.updatePage();
    } catch (DatabaseException ex) {
      LOGGER.error(GameConfig.getText(NO_FOLLOWER_LIST_ERROR, username));
    }
    MainViewManager.getInstance().switchTo(FOLLOWER_SCENE_ID);
  }

  private void setFixedSize(Region node) {
    node.setPrefSize(ProfileScene.PROFILE_CARD_MAX_WIDTH, ProfileScene.PROFILE_CARD_MAX_HEIGHT);
    node.setMaxSize(ProfileScene.PROFILE_CARD_MAX_WIDTH, ProfileScene.PROFILE_CARD_MAX_HEIGHT);
  }

  private Button createButton(String id, Runnable onClick) {
    Button btn = new Button(getText(id));
    btn.setId(id);
    btn.setOnAction(e -> onClick.run());
    btn.getStyleClass().add("small");
    return btn;
  }

  /**
   * Sets the username for the profile to be displayed. This method should be called before the
   * scene is shown to ensure the correct user's profile is loaded.
   *
   * @param username The username of the profile to display.
   */
  public void setUsername(String username) {
    this.username = username;
  }

  /**
   * Refreshes page with current profile. Call this method after username has been set for the profile
   * to be loaded.
   *
   */
  public void updatePage() {
    buildProfileUI();
  }
}