package oogasalad.view.scene.display;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.file.Paths;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import oogasalad.model.config.GameConfig;
import oogasalad.model.engine.architecture.Game;
import oogasalad.model.engine.architecture.GameInfo;
import oogasalad.model.parser.JsonParser;
import oogasalad.model.parser.Parser;
import oogasalad.model.parser.ParsingException;
import oogasalad.model.resource.ResourcePath;
import oogasalad.model.resource.ResourcePath.PathType;
import oogasalad.view.config.StyleConfig;
import oogasalad.view.runner.GameRunner;
import oogasalad.view.scene.MainViewManager;
import oogasalad.view.scene.ViewScene;

/**
 * Superclass for GamePlayerScene and GamePreview Scene
 *
 * @author Hsuan-Kai Liao, Reyan Shariff
 */
public class GameDisplayScene extends ViewScene {
  private static final String PAUSE_TEXT = "pauseButton";
  private static final String RESUME_TEXT = "resumeButton";

  private final StackPane root;
  private final VBox buttonBox;
  private final Label pauseOverlay;
  private final Button pauseButton;
  private final GameRunner gameRunner;
  private boolean isPaused = false;

  /**
   * Class constructor - creates a Game Runner and a return and pause button
   */
  private GameDisplayScene(Stage stage) {
    super(stage);

    gameRunner = new GameRunner();
    root = new StackPane();
    root.setStyle("-fx-background-color: Black;");
    Scene scene = new Scene(root);
    StyleConfig.registerScene(scene);
    stage.setScene(scene);

    pauseOverlay = setUpPauseModal();

    Button returnButton = new Button(GameConfig.getText("returnButton"));
    returnButton.setId("returnButton");
    returnButton.setOnAction(e -> MainViewManager.getInstance().switchToPrevScene());

    pauseButton = new Button(GameConfig.getText(PAUSE_TEXT));
    pauseButton.setId(PAUSE_TEXT);
    pauseButton.setOnAction(e -> togglePause());

    buttonBox = new VBox(10); // 10 is the vertical spacing between buttons
    buttonBox.setAlignment(Pos.TOP_RIGHT);
    buttonBox.getChildren().addAll(returnButton, pauseButton);

    StackPane.setAlignment(buttonBox, Pos.TOP_RIGHT);
    StackPane.setAlignment(pauseOverlay, Pos.CENTER);
    root.getChildren().addAll(buttonBox, pauseOverlay);
  }

  private Label setUpPauseModal() {
    Label pauseOverlay = new Label(GameConfig.getText("paused"));
    pauseOverlay.setId("pause-overlay");
    pauseOverlay.setVisible(false);
    return pauseOverlay;
  }

  private void togglePause() {
    if (isPaused) {
      gameRunner.play();
      pauseButton.setText(GameConfig.getText(PAUSE_TEXT)); // Set back to "Pause"
      pauseOverlay.setVisible(false);
    } else {
      gameRunner.pause();
      pauseButton.setText(GameConfig.getText(RESUME_TEXT)); // Set to "Resume"
      pauseOverlay.setVisible(true);
    }
    isPaused = !isPaused;
    gameRunner.getCanvas().requestFocus();
  }

  /**
   * Gets game runner
   */
  public GameRunner getGameRunner() {
    return gameRunner;
  }

  /**
   * Play the preview to start the game.
   */
  public void play(String filepath) {
    try {
      String parentDirectory = Paths.get(filepath).getParent().toString();
      ResourcePath.setToContext(parentDirectory, PathType.NORMAL);
      Parser<?> parser = new JsonParser(filepath);
      ObjectMapper mapper = new ObjectMapper();
      JsonNode newNode = mapper.createObjectNode();
      Game game = (Game) parser.parse(newNode);
      play(game);
    } catch (ParsingException e) {
      throw new IllegalStateException(GameConfig.getText("noGameFileSelected", e.getMessage()), e);
    }
  }

  /**
   * Play the preview to start the game.
   */
  /**
   * Play the preview to start the game, with canvas scaled to fit within root,
   * and adjusted dynamically on window resizing.
   */
  public void play(Game game) {
    root.getChildren().clear();
    root.getChildren().addAll(buttonBox, pauseOverlay);
    setPauseText(game);

    // Set up canvas
    Canvas canvas = getGameRunner().getCanvas();
    StackPane canvasPane = new StackPane();
    canvasPane.getChildren().add(canvas);
    root.getChildren().addFirst(canvasPane);
    updateCanvasSize();
    root.widthProperty().addListener((observable, oldWidth, newWidth) -> updateCanvasSize());
    root.heightProperty().addListener((observable, oldHeight, newHeight) -> updateCanvasSize());
    canvas.widthProperty().addListener((observable, oldWidth, newWidth) -> updateCanvasSize());
    canvas.heightProperty().addListener((observable, oldHeight, newHeight) -> updateCanvasSize());
    canvas.requestFocus();

    // Start the game
    getGameRunner().setGame(game);
    getGameRunner().play();
  }

  private void updateCanvasSize() {
    double rootWidth = root.getWidth();
    double rootHeight = root.getHeight();

    double canvasWidth = getGameRunner().getCanvas().getWidth();
    double canvasHeight = getGameRunner().getCanvas().getHeight();

    double scaleX = rootWidth / canvasWidth;
    double scaleY = rootHeight / canvasHeight;

    double scale = Math.min(scaleX, scaleY);

    StackPane canvasPane = (StackPane) root.getChildren().getFirst();
    canvasPane.setScaleX(scale);
    canvasPane.setScaleY(scale);

    canvasPane.setAlignment(Pos.CENTER);
  }

  private void setPauseText(Game game) {
    GameInfo info = game.getGameInfo();
    if (info != null) {
      pauseOverlay.setText(
          GameConfig.getText("paused") + "\n" +
              String.format(GameConfig.getText("gameLabel"), info.name()) + "\n" +
              String.format(GameConfig.getText("authorLabel"), info.author())  + "\n" +
              String.format(GameConfig.getText("descriptionLabel"), info.description())
      );
    }
  }

  /**
   * Deactivates the game engine for this scene.
   */
  @Override
  public void onDeactivate() {
    gameRunner.pause();

    if (isPaused) {
      togglePause();
    }
  }

  @Override
  public void onActivate() {
    gameRunner.play();
  }
}
