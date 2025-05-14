package oogasalad.view.runner;

import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.util.Duration;
import oogasalad.model.config.GameConfig;
import oogasalad.model.engine.architecture.Game;
import oogasalad.model.engine.architecture.GameScene;
import oogasalad.model.engine.architecture.KeyCode;
import oogasalad.model.engine.component.Camera;
import oogasalad.model.engine.component.Transform;
import oogasalad.view.gui.error.PopUpError;
import oogasalad.view.renderer.sceneRenderer.GameSceneRenderer;
import oogasalad.view.scene.MainViewManager;

/**
 * The GUI class manages the canvas-based graphical rendering of the OOGASalad game engine. It is
 * meant to be embedded inside a larger JavaFX UI layout, allowing game rendering to happen
 * alongside JavaFX UI controls. This class handles rendering game scenes, processing input, and
 * running the game loop.
 *
 * @author Jack F. Regan, Logan Dracos, and Hsuan-Kai Liao
 */
public class GameRunner {

  private final Canvas canvas;
  private final GameSceneRenderer objectRenderer;

  private Timeline logicLoop;
  private AnimationTimer renderLoop;
  private Game game;

  private double mouseX;
  private double mouseY;

  /**
   * Constructs a new GUI instance for the given game.
   */
  public GameRunner() {

    canvas = new Canvas(GameConfig.getNumber("windowWidth"), GameConfig.getNumber("windowHeight"));
    canvas.setFocusTraversable(true);
    canvas.requestFocus();
    canvas.sceneProperty().addListener((observable, oldScene, newScene) -> {
      if (oldScene != null) {
        oldScene.setOnKeyPressed(null);
        oldScene.setOnKeyReleased(null);
        oldScene.setOnMousePressed(null);
        oldScene.setOnMouseReleased(null);
        oldScene.setOnMouseMoved(null);
      }

      if (newScene != null) {
        newScene.setOnKeyPressed(this::handleKeyPressed);
        newScene.setOnKeyReleased(this::handleKeyReleased);
        newScene.setOnMousePressed(e -> handleMouseClick(true));
        newScene.setOnMouseReleased(e -> handleMouseClick(false));
        newScene.setOnMouseMoved(e -> {
          mouseX = e.getScreenX();
          mouseY = e.getScreenY();
        });
      }
    });

    objectRenderer = new GameSceneRenderer(canvas.getGraphicsContext2D());

    setUpLogicLoop();
    setUpRenderLoop();
  }

  /**
   * Returns the canvas object used for rendering. This should be embedded into a parent layout
   * (e.g., Pane, VBox, StackPane) to display the game.
   *
   * @return the Canvas instance
   */
  public Canvas getCanvas() {
    return canvas;
  }

  private void setUpLogicLoop() {
    double logicFps = GameConfig.getNumber("framesPerSecond");
    logicLoop = new Timeline();
    logicLoop.setCycleCount(Timeline.INDEFINITE);
    logicLoop.getKeyFrames().add(new KeyFrame(Duration.seconds(1.0 / logicFps), event -> {
      try {
        handleMousePos(mouseX, mouseY);
        GameScene current = game.getCurrentScene();
        if (current != null) {
          game.step(1.0 / logicFps);
        }
      } catch (Exception e) {
        String message = GameConfig.getText("errorStartGameLoop", e);
        GameConfig.LOGGER.error(message, e);
        MainViewManager.getInstance().switchToPrevScene();
        Platform.runLater(() -> PopUpError.showError(message));
      }
    }));
  }

  private void setUpRenderLoop() {
    renderLoop = new AnimationTimer() {
      @Override
      public void handle(long now) {
        try {
          GameScene current = game.getCurrentScene();
          if (current != null) {
            objectRenderer.renderWithCamera(current);
          }
        } catch (Exception ignored) {
        }
      }
    };
  }


  /**
   * Set the Game instance for the game runner.
   *
   * @param game The given game instance
   */
  public void setGame(Game game) {
    this.game = game;
  }

  /**
   * Stops an existing game loop
   */
  public void pause() {
    if (logicLoop != null) {
      logicLoop.stop();
    }

    if (renderLoop != null) {
      renderLoop.stop();
    }
  }

  /**
   * Resumes an existing game loop.
   */
  public void play() {
    if (logicLoop != null && game != null) {
      logicLoop.play();
    }

    if (renderLoop != null && game != null) {
      renderLoop.start();
    }
  }

  /**
   * Handles a key press event and maps it to the game engine.
   *
   * @param e The JavaFX key event.
   */
  public void handleKeyPressed(javafx.scene.input.KeyEvent e) {
    KeyCode key = mapToEngineKeyCode(e.getCode());
    if (key != null) {
      game.keyPressed(key.getValue());
    }
  }

  /**
   * Handles a key release event and maps it to the game engine.
   *
   * @param e The JavaFX key event.
   */
  public void handleKeyReleased(javafx.scene.input.KeyEvent e) {
    KeyCode key = mapToEngineKeyCode(e.getCode());
    if (key != null) {
      game.keyReleased(key.getValue());
    }
  }

  private KeyCode mapToEngineKeyCode(javafx.scene.input.KeyCode code) {
    try {
      return KeyCode.valueOf(code.name());
    } catch (IllegalArgumentException e) {
      return null;
    }
  }

  private void handleMouseClick(boolean isClicked) {
    game.mouseClicked(isClicked);
  }

  private void handleMousePos(double x, double y) {
    Point2D localPoint = canvas.screenToLocal(x, y);
    Camera camera = game.getCurrentScene().getMainCamera();

    if (camera != null && localPoint != null) {
      Transform cameraTransform = camera.getComponent(Transform.class);
      game.mouseMoved(localPoint.getX() + cameraTransform.getX(), localPoint.getY() + cameraTransform.getY());
    } else if (localPoint != null) {
      game.mouseMoved(localPoint.getX(), localPoint.getY());
    } else {
      game.mouseMoved(x, y);
    }
  }
}