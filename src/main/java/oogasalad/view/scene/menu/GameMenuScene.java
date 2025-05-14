package oogasalad.view.scene.menu;

import java.io.File;
import java.nio.file.Paths;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import oogasalad.model.config.GameConfig;
import oogasalad.model.resource.ResourcePath;
import oogasalad.view.config.StyleConfig;
import oogasalad.view.gui.error.PopUpError;
import oogasalad.view.scene.MainViewManager;
import oogasalad.view.scene.ViewScene;
import oogasalad.view.scene.display.GameDisplayScene;

/**
 * Displays the game menu UI, where users can:
 */
public class GameMenuScene extends ViewScene {

  private final BorderPane root = new BorderPane();

  private static final String DINO_BUTTON_ID = "dinoButton";
  private static final String GEO_BUTTON_ID = "geoButton";
  private static final String DOODLE_BUTTON_ID = "doodleButton";
  private static final String GAME_PLAYER_SCENE_NAME = "GameDisplayScene";
  private static final String BACK_BUTTON_ID = "returnButton";
  private static final int VBOX_SPACING = 60;

  private final VBox gameCard;
  private final Scene scene;

  private String selectedFilePath = "";

  /**
   * Initializes the game menu scene and sets up the layout and buttons.
   */
  private GameMenuScene(Stage stage) {
    super(stage);
    scene = new Scene(root);
    StyleConfig.registerScene(scene);
    stage.setScene(scene);

    gameCard = new VBox(VBOX_SPACING);
    gameCard.setAlignment(Pos.CENTER);
    root.setCenter(gameCard);

    setUpGameMenuUi();
  }

  /**
   * Builds all components of the game menu UI.
   */
  private void setUpGameMenuUi() {
    makeGameSelectionBox();
    makeGameFileSelection();
    setUpReturnButton();
  }

  /**
   * Adds a button that lets the user choose a game JSON file to load and play.
   */
  private void makeGameFileSelection() {
    Button gameSelector = new Button(GameConfig.getText("gameSelectorInitialValue"));
    gameSelector.setId("gameSelector");
    gameSelector.setOnAction(e -> {
      FileChooser fileChooser = new FileChooser();
      fileChooser.setTitle(GameConfig.getText("builderFileSelection"));
      fileChooser.getExtensionFilters()
          .add(new FileChooser.ExtensionFilter("JSON Files", "*.json"));
      fileChooser.setInitialDirectory(new File(GameConfig.getText("jsonFileDirectory")));

      File selectedFile = fileChooser.showOpenDialog(scene.getWindow());
      if (selectedFile != null) {
        selectedFilePath = selectedFile.getAbsolutePath();
      }
      startPlayingGame(selectedFilePath);
    });
    gameCard.getChildren().add(gameSelector);
  }

  private void startPlayingGame(String selectedFilePath) {
    try {
      String parentDirectory = Paths.get(selectedFilePath).getParent().toString();
      ResourcePath.setFromContext(parentDirectory);

      ((GameDisplayScene) MainViewManager.getInstance()
          .getViewScene(GAME_PLAYER_SCENE_NAME)).play(selectedFilePath);
      MainViewManager.getInstance().switchTo(GAME_PLAYER_SCENE_NAME);
    } catch (IllegalStateException illegalState) {
      PopUpError.showError(illegalState.getMessage());
    }
  }

  /**
   * Adds buttons with images that start a built-in game when clicked.
   */
  private void makeGameSelectionBox() {
    HBox selectionBox = new HBox();
    selectionBox.setAlignment(Pos.CENTER);

    ImageView dinoGameButton = makeImageButton(GameConfig.getText("dinoGameButtonImageFile"), "data/GameJsons/INFINITE/INFINITE_DINO/INFINITE_DINO.json");
    dinoGameButton.setId(DINO_BUTTON_ID);

    ImageView geoDashButton = makeImageButton(GameConfig.getText("geoDashButtonImageFile"), "data/GameJsons/INFINITE/INFINITE_GEODASH/INFINITE_GEODASH.json");
    geoDashButton.setId(GEO_BUTTON_ID);

    // TODO: add file path to infinite games
    ImageView doodleJumpButton = makeImageButton(GameConfig.getText("doodleJumpButtonImageFile"), "data/GameJsons/INFINITE/INFINITE_DOODLE/INFINITE_DOODLE.json");
    doodleJumpButton.setId(DOODLE_BUTTON_ID);

    selectionBox.getChildren().addAll(dinoGameButton, geoDashButton, doodleJumpButton);
    gameCard.getChildren().add(selectionBox);
  }

  /**
   * Creates a clickable image that loads a game when clicked.
   *
   * @param image    path to the image file
   * @param filePath game file to load
   * @return the clickable image button
   */
  private ImageView makeImageButton(String image, String filePath) {
    ImageView buttonImage = new ImageView(image);
    buttonImage.setFitHeight(200);
    buttonImage.setFitWidth(200);
    buttonImage.setCursor(Cursor.HAND);

    buttonImage.setOnMouseClicked(e -> startPlayingGame(filePath));
    return buttonImage;
  }

  /**
   * Adds a return button that goes back to the previous scene.
   */
  private void setUpReturnButton() {
    Button returnButton = new Button(GameConfig.getText(BACK_BUTTON_ID));
    returnButton.setId(BACK_BUTTON_ID);
    returnButton.setOnAction(e -> MainViewManager.getInstance().switchToPrevScene());
    returnButton.setAlignment(Pos.TOP_LEFT);
    root.setTop(returnButton);
  }
}