package oogasalad.view.scene.auth;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import oogasalad.controller.GameController;
import oogasalad.database.DatabaseException;
import oogasalad.model.config.GameConfig;
import oogasalad.model.config.PasswordHashingException;
import oogasalad.model.profile.SignUpRequest;
import oogasalad.view.config.StyleConfig;
import oogasalad.view.gui.error.PopUpError;
import oogasalad.view.scene.MainViewManager;
import oogasalad.view.scene.ViewScene;

import static oogasalad.model.config.GameConfig.LOGGER;
import static oogasalad.model.config.GameConfig.getText;

/**
 * This is the scene that visualizes the sign-up process so that users can put their information in
 * the Firebase database.
 *
 * @author Daniel Rodriguez-Florido, Justin Aronwald
 */
public class SignUpScene extends ViewScene {

  private static final String DEFAULT_PFP_PATH =
      Objects.requireNonNull(SignUpScene.class.getResource("/oogasalad/auth/defaultPfp.png")).toExternalForm();
  private static final String UPLOAD_BUTTON_ID = "uploadButton";
  private static final String SIGNUP_LABEL_ID = "signUpLabel";
  private static final String SOCIAL_SIGNUP_BUTTON = "socialSignUpButton";
  private static final String SIGNUP_PROMPT_ID = "signUpPrompt";
  private static final String SIGNUP_FIRST_NAME_FIELD_ID = "signUpFirstNameField";
  private static final String SIGNUP_LAST_NAME_FIELD_ID = "signUpLastNameField";
  private static final String SOCIAL_EMAIL_PROMPT_ID = "socialEmailPrompt";
  private static final String SOCIAL_PASSWORD_PROMPT_ID = "socialPasswordPrompt";
  private static final String SOCIAL_BIO_FIELD = "socialBioField";
  private static final String NAME_TEXT_BOX = "nameTextBox";


  private final GameController gameController;
  private TextField usernameField;
  private TextField passwordField;
  private TextField firstNameField;
  private TextField lastNameField;
  private TextField bioField;
  private File uploadedPhoto;


  private SignUpScene(Stage stage) {
    super(stage);

    StackPane root = new StackPane();
    Scene scene = new Scene(root);
    StyleConfig.registerScene(scene);
    stage.setScene(scene);

    VBox card = new VBox(20);
    card.setId("socialCard");

    setUpSignUpLabel(card);
    setUpSignUpPrompt(card);
    setupPfp(card);
    handleUsernameField(card);
    handlePasswordField(card);
    setUpFirstNameField(card);
    setUpLastNameField(card);
    setupBioField(card);
    handleSignUpButton(card);
    handleLoginButton(card);


    gameController = new GameController();

    root.getChildren().add(card);
  }

  private void setUpSignUpLabel(VBox card) {
    Label signUpLabel = new Label(getText(SIGNUP_LABEL_ID));
    signUpLabel.setId(SIGNUP_LABEL_ID);
    card.getChildren().add(signUpLabel);
  }

  private void setUpSignUpPrompt(VBox card) {
    Label signUpPrompt = new Label(getText(SIGNUP_PROMPT_ID));
    signUpPrompt.setId(SIGNUP_PROMPT_ID);
    card.getChildren().add(signUpPrompt);
  }

  private void setUpFirstNameField(VBox card) {
    firstNameField = new TextField();
    firstNameField.setPromptText(getText(SIGNUP_FIRST_NAME_FIELD_ID));
    firstNameField.setId(SIGNUP_FIRST_NAME_FIELD_ID);
    firstNameField.getStyleClass().add(NAME_TEXT_BOX);
    card.getChildren().add(firstNameField);
  }

  private void setUpLastNameField(VBox card) {
    lastNameField = new TextField();
    lastNameField.setPromptText(getText(SIGNUP_LAST_NAME_FIELD_ID));
    lastNameField.setId(SIGNUP_LAST_NAME_FIELD_ID);
    lastNameField.getStyleClass().add(NAME_TEXT_BOX);
    card.getChildren().add(lastNameField);
  }

  private void handleUsernameField(VBox card) {
    usernameField = new TextField();
    usernameField.setPromptText(getText(SOCIAL_EMAIL_PROMPT_ID));
    usernameField.setId(SOCIAL_EMAIL_PROMPT_ID);
    card.getChildren().add(usernameField);
  }

  private void handlePasswordField(VBox card) {
    passwordField = new TextField();
    passwordField.setPromptText(getText(SOCIAL_PASSWORD_PROMPT_ID));
    passwordField.setId(SOCIAL_PASSWORD_PROMPT_ID);
    card.getChildren().add(passwordField);
  }

  private void handleSignUpButton(VBox card) {
    Button signUpButton = new Button(getText(SOCIAL_SIGNUP_BUTTON));
    signUpButton.setId("SignUpButton");
    signUpButton.setOnAction(e -> {
      String username = usernameField.getText().trim();
      String password = passwordField.getText().trim();
      String firstName = firstNameField.getText().trim();
      String lastName = lastNameField.getText().trim();
      String bio = bioField.getText().trim();

      if (username.isEmpty() || password.isEmpty() || firstName.isEmpty() || lastName.isEmpty()) {
        LOGGER.warn(GameConfig.getText("oneOrMoreFieldsMissing"));
        PopUpError.showError(GameConfig.getText("oneOrMoreFieldsMissing"));
        return;
      }
      handleGameControllerSignUp(new SignUpRequest(username, password, firstName, lastName, bio, uploadedPhoto));
    });
    card.getChildren().add(signUpButton);
  }

  private void handleGameControllerSignUp(SignUpRequest signUpRequest) {
    try {
      gameController.handleSignUp(signUpRequest);
    } catch (PasswordHashingException ex) {
      LOGGER.error(GameConfig.getText("errorHashingPasswordWithMessage"), ex.getMessage());
      PopUpError.showError(GameConfig.getText("errorHashingPasswordWithMessage"));
    } catch (DatabaseException ex) {
      LOGGER.error(GameConfig.getText("userAlreadyExistsWithMessage"), ex.getMessage());
      PopUpError.showError(GameConfig.getText("userAlreadyExistsWithMessage"));
    } catch (IOException ex) {
      LOGGER.error(GameConfig.getText("signUpIOError"), ex.getMessage());
      PopUpError.showError("Could not sign up. Please try again.");
    }
  }

  private void handleLoginButton(VBox card) {
    Button logInButton = new Button(GameConfig.getText("logInText"));
    logInButton.setId("LoginButton");
    logInButton.setOnAction(e -> MainViewManager.getInstance().switchTo("LogInScene"));
    card.getChildren().add(logInButton);
  }

  private void setupPfp(VBox card) {
    Image profileImage = new Image(DEFAULT_PFP_PATH, 60, 60, true, true);
    ImageView profileImageView = new ImageView(profileImage);
    profileImageView.setFitWidth(80);
    profileImageView.setFitHeight(80);
    profileImageView.setPreserveRatio(true);

    Circle clip = new Circle(40, 40, 40); // centerX, centerY, radius
    profileImageView.setClip(clip);

    Button uploadFileButton = handleFileUpload(profileImageView);

    card.getChildren().addAll(profileImageView, uploadFileButton);
  }

  private Button handleFileUpload(ImageView profileImageView) {
    Button uploadFileButton = new Button(getText("uploadFileButton"));
    uploadFileButton.setId(UPLOAD_BUTTON_ID);
    uploadFileButton.setPrefWidth(120);
    uploadFileButton.setStyle("-fx-font-size: 12px;");
    uploadFileButton.setPrefHeight(10);
    uploadFileButton.setOnAction(e -> {
      FileChooser fileChooser = new FileChooser();
      fileChooser.setTitle(GameConfig.getText("uploadFileTitle"));
      fileChooser.getExtensionFilters().add(
          new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
      );

      Stage stage = (Stage) uploadFileButton.getScene().getWindow();
      File selectedFile = fileChooser.showOpenDialog(stage);
      if (selectedFile != null) {
        profileImageView.setImage(new Image(selectedFile.toURI().toString()));
        uploadedPhoto = selectedFile;
      }
    });
    return uploadFileButton;
  }

  private void setupBioField(VBox card) {
    bioField = new TextField();
    bioField.setPromptText(GameConfig.getText(SOCIAL_BIO_FIELD));
    bioField.setId(SOCIAL_BIO_FIELD);
    bioField.getStyleClass().add(NAME_TEXT_BOX);
    card.getChildren().add(bioField);

  }

  @Override
  public void onActivate() {
    usernameField.clear();
    passwordField.clear();
    uploadedPhoto = null;
    bioField.clear();
    firstNameField.clear();
    lastNameField.clear();
  }
}
