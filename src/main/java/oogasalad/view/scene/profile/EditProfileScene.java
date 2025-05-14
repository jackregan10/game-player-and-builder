package oogasalad.view.scene.profile;

import static oogasalad.model.config.GameConfig.LOGGER;

import java.io.File;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import oogasalad.controller.GameController;
import oogasalad.database.DatabaseException;
import oogasalad.model.config.GameConfig;
import oogasalad.model.profile.PlayerData;
import oogasalad.model.profile.SessionException;
import oogasalad.model.profile.SessionManagement;
import oogasalad.view.config.StyleConfig;
import oogasalad.view.gui.error.PopUpError;
import oogasalad.view.scene.MainViewManager;
import oogasalad.view.scene.ViewScene;

/**
 * ViewScene that allows users to edit their profile information
 *
 * @author Jack Regan
 */
public class EditProfileScene extends ViewScene {

  private static final int VBOX_SPACING = 10;
  private static final int CARD_WIDTH = 640;
  private static final int CARD_HEIGHT = 400;
  private static final String PROFILE_CARD_ID = "profileCard";
  private static final String UPLOAD_PHOTO_BUTTON_ID = "uploadPhotoButton";
  private static final String SELECTED_FILE_LABEL_ID = "selectedFileLabel";
  private static final String PHOTO_LABEL_ID = "photoLabel";
  private static final String NAME_LABEL_ID = "nameLabel";
  private static final String NAME_TEXT_FIELD_ID = "nameTextField";
  private static final String USERNAME_VALUE_ID = "usernameValue";
  private static final String SUBMIT_BUTTON_ID = "submitButton";
  private static final String BIO_LABEL_ID = "bioLabel";
  private static final String BIO_TEXT_AREA_ID = "bioTextArea";
  private static final String RETURN_BUTTON_ID = "returnButton";
  private static final String SELECTED_PHOTO_SPACING_LABEL = "selectedPhotoSpacing";
  private static final String SCENE_NAME = "ProfileScene";
  private static final String LOGIN_SCENE_NAME = "LoginScene";


  private final PlayerData playerData;
  private final GameController gameController;
  private final VBox editProfileCard;

  private TextField nameTextField;
  private TextArea bioTextArea;
  private File imageFile;
  private ImageView profileImage;

  private EditProfileScene(Stage stage) {
    super(stage);
    gameController = new GameController();
    playerData = setPlayerData();
    BorderPane root = new BorderPane();
    Scene scene = new Scene(root);

    StyleConfig.registerScene(scene);
    stage.setScene(scene);

    editProfileCard = new VBox(VBOX_SPACING);
    editProfileCard.setMaxWidth(CARD_WIDTH);
    editProfileCard.setMaxHeight(CARD_HEIGHT);
    editProfileCard.setId(PROFILE_CARD_ID);
    if (playerData != null) {
      buildEditProfileUI();
    }
    setUpBackButton(root);
    editProfileCard.setAlignment(Pos.CENTER);
    root.setCenter(editProfileCard);
  }

  private void buildEditProfileUI() {
    setUpProfileImage(editProfileCard);
    setUpUsernameBox(editProfileCard);
    setUpProfilePhoto(editProfileCard);
    setUpNameBox(editProfileCard);
    setUpBioBox(editProfileCard);
    setUpSubmitButton(editProfileCard);
  }

  private PlayerData setPlayerData() {
    try {
      String username = SessionManagement.getInstance().getCurrentUser().getUsername();
      return gameController.getPlayerByUsername(username);
    } catch (SessionException | DatabaseException e) {
      LOGGER.error(GameConfig.getText("noSuchPlayer"));
      MainViewManager.getInstance().switchTo(LOGIN_SCENE_NAME);
      return null; // Should not happen due to the switch
    }
  }

  private void setUpProfilePhoto(VBox container) {
    Label photoLabel = new Label(GameConfig.getText("profilePhoto"));
    photoLabel.setId(PHOTO_LABEL_ID);
    FileChooser fileChooser = new FileChooser();
    fileChooser.getExtensionFilters().add(
        new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
    );
    Button uploadPhotoButton = new Button(GameConfig.getText("profilePhotoUploadButton"));
    uploadPhotoButton.setId(UPLOAD_PHOTO_BUTTON_ID);
    Label selectedFileLabel = new Label(GameConfig.getText("selectedPhotoLabelEmpty"));
    selectedFileLabel.setId(SELECTED_FILE_LABEL_ID);

    uploadPhotoButton.setOnAction(event -> {
      File selectedFile = fileChooser.showOpenDialog(uploadPhotoButton.getScene().getWindow());
      if (selectedFile != null) {
        selectedFileLabel.setText(GameConfig.getText("selectedPhotoLabel", selectedFile.getName()));
        if (playerData != null) {
          imageFile = selectedFile;
          playerData.setImageUrl(String.valueOf(selectedFile));
          profileImage.setImage(new Image(selectedFile.toURI().toString()));
        }
      }
    });
    HBox photoBox = new HBox(GameConfig.getNumber(SELECTED_PHOTO_SPACING_LABEL), photoLabel, uploadPhotoButton, selectedFileLabel);
    photoBox.setAlignment(Pos.CENTER_LEFT);
    container.getChildren().add(photoBox);
  }

  private void setUpNameBox(VBox container) {
    Label nameLabel = new Label(GameConfig.getText("profileFullName"));
    nameLabel.setId(NAME_LABEL_ID);
    nameTextField = new TextField();
    nameTextField.setId(NAME_TEXT_FIELD_ID);

    if (playerData != null && playerData.getFullName() != null) {
      nameTextField.setText(playerData.getFullName());
    }

    HBox nameBox = new HBox(GameConfig.getNumber(SELECTED_PHOTO_SPACING_LABEL), nameLabel, nameTextField);
    nameBox.setAlignment(Pos.CENTER_LEFT);
    container.getChildren().add(nameBox);
  }

  private void setUpBioBox(VBox container) {
    Label bioLabel = new Label(GameConfig.getText("profileBio"));
    bioLabel.setId(BIO_LABEL_ID);
    bioTextArea = new TextArea();
    bioTextArea.setId(BIO_TEXT_AREA_ID);
    bioTextArea.setPrefRowCount((int) Double.parseDouble(
        String.valueOf(GameConfig.getNumber("profileBioLineCount"))));
    if (playerData != null && playerData.getBio() != null) {
      bioTextArea.setText(playerData.getBio());
    }
    HBox bioBox = new HBox(GameConfig.getNumber(SELECTED_PHOTO_SPACING_LABEL), bioLabel, bioTextArea);
    bioBox.setAlignment(Pos.CENTER_LEFT);
    container.getChildren().add(bioBox);
  }

  private void setUpUsernameBox(VBox container) {
    Label usernameValue = new Label();
    usernameValue.setId(USERNAME_VALUE_ID);

    if (playerData != null && playerData.getUsername() != null) {
      usernameValue.setText(playerData.getUsername());
    }

    HBox usernameBox = new HBox(GameConfig.getNumber(SELECTED_PHOTO_SPACING_LABEL), usernameValue);
    usernameBox.setAlignment(Pos.CENTER);
    container.getChildren().add(usernameBox);
  }

  private void setUpSubmitButton(VBox container) {
    Button submitButton = new Button(GameConfig.getText("profileEditSaveButton"));
    submitButton.setId(SUBMIT_BUTTON_ID);
    submitButton.setOnAction(event -> handleProfileUpdate());
    container.getChildren().add(submitButton);
  }

  private void handleProfileUpdate() {
    if (playerData == null) return;

    playerData.setBio(bioTextArea.getText());
    playerData.setFullName(nameTextField.getText());

    try {
      if (gameController.handleUpdateProfile(playerData, imageFile)) {
        switchToUpdatedProfileScene();
      }
    } catch (DatabaseException e) {
      LOGGER.error(e.getMessage());
      PopUpError.showError(GameConfig.getText("invalidProfileUpdate"));
    }
  }

  private void switchToUpdatedProfileScene() {
    MainViewManager manager = MainViewManager.getInstance();
    try {
      ProfileScene profileScene = (ProfileScene) manager.getViewScene(SCENE_NAME);
      String currentUsername = SessionManagement.getInstance().getCurrentUser().getUsername();
      profileScene.setUsername(currentUsername);
      profileScene.updatePage();
    } catch (SessionException ex) {
      LOGGER.error(GameConfig.getText("noSuchPlayer"));
      gameController.handleLogout();
    }
    manager.switchTo(SCENE_NAME);
  }

  private void setUpProfileImage(VBox profileCard) {
    profileImage = new ImageView(playerData.getImageUrl());
    profileImage.setFitWidth(80);
    profileImage.setFitHeight(80);

    Circle clip = new Circle(40, 40, 40);
    profileImage.setClip(clip);
    profileCard.getChildren().add(profileImage);
  }

  private void setUpBackButton(BorderPane root) {
    HBox backBox = new HBox();
    Button backButton = new Button(GameConfig.getText(RETURN_BUTTON_ID));
    backButton.setId(RETURN_BUTTON_ID);
    backButton.setOnAction(e -> MainViewManager.getInstance().switchToPrevScene());
    backBox.getChildren().add(backButton);
    backBox.setAlignment(Pos.TOP_LEFT);
    root.setTop(backButton);
  }
}
