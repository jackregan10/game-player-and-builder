package oogasalad.view.scene.menu;

import java.io.File;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import oogasalad.controller.GameController;
import oogasalad.model.config.GameConfig;
import oogasalad.model.profile.SessionException;
import oogasalad.model.profile.SessionManagement;
import oogasalad.view.config.StyleConfig;
import oogasalad.view.gui.error.PopUpError;
import oogasalad.view.scene.MainViewManager;
import oogasalad.view.scene.ViewScene;
import oogasalad.view.scene.builder.BuilderScene;
import oogasalad.view.scene.profile.SocialHubScene;

/**
 * Main menu view with play and builder options
 */
@SuppressWarnings("unused")
public class MainMenuScene extends ViewScene {

  private static final String GAME_PLAYER_MENU_SCENE_NAME = "GameMenuScene";
  private static final String LOGIN_SCENE_NAME = "LoginScene";
  private static final String BUILDER_SCENE_NAME = "BuilderScene";
  private static final String PROFILE_SCENE_NAME = "ProfileScene";
  private static final String SOCIAL_HUB_SCENE_NAME = "SocialHubScene";
  private static final String MAIN_MENU_TITLE_ID = "mainMenuTitle";
  private static final String FILE_SELECTION_PROMPT = "builderFileSelection";
  private static final String JSON_FILE_DIRECTORY = "jsonFileDirectory";
  private static final String PLAY_BUTTON_ID = "playButton";
  private static final String BUILD_BUTTON_ID = "buildButton";
  private static final String SOCIAL_HUB_BUTTON_ID = "socialHubButton";
  private static final String BUTTON_BOX_ID = "buttonBox";
  private static final String SELECTOR_BOX_ID = "selectorBox";
  private static final String THEME_SELECTOR_INITIAL_VALUE = "themeSelectorInitialValue";
  private static final String MENU_SELECTOR_ID = "menuSelector";
  private static final String THEME_SELECTOR_ID = "themeSelector";
  private static final String LOGOUT_BUTTON_ID = "logOutButton";
  private static final String LOGOUT_BUTTON_BOX_ID = "logOutBox";
  private static final String LANGUAGE_SELECTOR_ID = "languageSelector";
  private static final String LANGUAGE_SELECTOR_INITIAL_VALUE_ID = "languageSelectorInitialValue";

  private final GameController gameController;

  private String selectedFilePath = "";

  /**
   * Constructs a new MainMenuScene to display the main menu with game selection and builder
   * options.
   */
  private MainMenuScene(Stage stage) {
    super(stage);

    this.gameController = new GameController();
    VBox root = new VBox();
    root.setAlignment(Pos.CENTER);
    Scene scene = new Scene(root);
    StyleConfig.registerScene(scene);
    stage.setScene(scene);

    Label title = new Label(GameConfig.getText(MAIN_MENU_TITLE_ID));
    title.setId(MAIN_MENU_TITLE_ID);

    HBox buttonBox = setupButtonBox();

    // Language and Theme Selections
    HBox selectorBox = setupSelectorBox();
    HBox logOutBox = setUpLogOutBox();

    // Add to root
    root.getChildren().addAll(title, buttonBox, selectorBox, logOutBox);
  }

  private void setBuildButtonOnAction(Button buildButton) {
    buildButton.setOnAction(e -> {
      try {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(GameConfig.getText(FILE_SELECTION_PROMPT));
        fileChooser.getExtensionFilters()
            .add(new FileChooser.ExtensionFilter("JSON Files", "*.json"));
        fileChooser.setInitialDirectory(new File(GameConfig.getText(JSON_FILE_DIRECTORY)));

        File selectedFile = fileChooser.showOpenDialog(getStage().getScene().getWindow());
        if (selectedFile != null) {
          selectedFilePath = selectedFile.getAbsolutePath();
        }

        ((BuilderScene) MainViewManager.getInstance()
            .getViewScene(BUILDER_SCENE_NAME)).reload(selectedFilePath);
        MainViewManager.getInstance().switchTo(BUILDER_SCENE_NAME);
      } catch (IllegalStateException illegalState) {
        PopUpError.showError(illegalState.getMessage());
      }
    });
  }



  private static void setPlayButtonOnAction(Button playButton) {
    playButton.setOnAction(e -> {
      try {
        MainViewManager.getInstance().switchTo(GAME_PLAYER_MENU_SCENE_NAME);
      } catch (IllegalStateException illegalState) {
        PopUpError.showError(illegalState.getMessage());
      }
    });
  }

  private HBox setupButtonBox() {
    HBox buttonBox = new HBox();
    Button playButton = new Button(GameConfig.getText(PLAY_BUTTON_ID));
    playButton.setId(PLAY_BUTTON_ID);
    setPlayButtonOnAction(playButton);
    Button buildButton = new Button(GameConfig.getText(BUILD_BUTTON_ID));
    buildButton.setId(BUILD_BUTTON_ID);
    setBuildButtonOnAction(buildButton);
    Button socialHubButton = new Button(GameConfig.getText(SOCIAL_HUB_BUTTON_ID));
    socialHubButton.setId(SOCIAL_HUB_BUTTON_ID);
    setSocialHubButtonOnAction(socialHubButton);
    buttonBox.getChildren().addAll(playButton, buildButton, socialHubButton);
    buttonBox.setId(BUTTON_BOX_ID);
    return buttonBox;
  }

  private void setSocialHubButtonOnAction(Button socialHubButton) {
    socialHubButton.setOnAction(e -> {
      try {
        ((SocialHubScene) MainViewManager.getInstance().getViewScene(SOCIAL_HUB_SCENE_NAME)).setUsername(
            SessionManagement.getInstance().getCurrentUser().getUsername());
        ((SocialHubScene) MainViewManager.getInstance()
            .getViewScene(SOCIAL_HUB_SCENE_NAME)).updatePage();
        MainViewManager.getInstance().switchTo(SOCIAL_HUB_SCENE_NAME);
      } catch (SessionException ex) {
        PopUpError.showError(ex.getMessage());
        gameController.handleLogout();
      }
    });
  }

  private HBox setupSelectorBox() {
    HBox selectorBox = new HBox();
    ComboBox<String> languageSelector = setupLanguageSelector();
    ComboBox<String> themeSelector = setupThemeSelector();
    selectorBox.getChildren().addAll(languageSelector, themeSelector);
    selectorBox.setId(SELECTOR_BOX_ID);
    return selectorBox;
  }

  private ComboBox<String> setupLanguageSelector() {
    ComboBox<String> languageSelector = new ComboBox<>();
    languageSelector.setValue(GameConfig.getText(LANGUAGE_SELECTOR_INITIAL_VALUE_ID));
    languageSelector.setId(MENU_SELECTOR_ID);
    languageSelector.getItems().addAll(GameConfig.getTextList(LANGUAGE_SELECTOR_ID));
    languageSelector.setOnAction(e -> GameConfig.setLanguage(languageSelector.getValue()));
    return languageSelector;
  }

  private ComboBox<String> setupThemeSelector() {
    ComboBox<String> themeSelector = new ComboBox<>();
    themeSelector.setValue(GameConfig.getText(THEME_SELECTOR_INITIAL_VALUE));
    themeSelector.setId(MENU_SELECTOR_ID);
    themeSelector.getItems().addAll(GameConfig.getTextList(THEME_SELECTOR_ID));
    themeSelector.setOnAction(e -> StyleConfig.setTheme(themeSelector.getValue()));
    return themeSelector;
  }

  private HBox setUpLogOutBox() {
    HBox logOutBox = new HBox();
    logOutBox.setAlignment(Pos.CENTER);
    logOutBox.setSpacing(10);
    logOutBox.setPadding(new Insets(20, 0, 0, 0));

    Button logOutButton = new Button(GameConfig.getText(LOGOUT_BUTTON_ID));
    logOutButton.setOnMouseClicked((event) -> gameController.handleLogout());

    logOutButton.setId(LOGOUT_BUTTON_ID);
    logOutBox.getChildren().add(logOutButton);
    logOutBox.setId(LOGOUT_BUTTON_BOX_ID);

    return logOutBox;
  }

}

