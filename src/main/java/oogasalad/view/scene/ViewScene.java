package oogasalad.view.scene;

import javafx.application.Platform;
import javafx.stage.Stage;
import oogasalad.model.config.GameConfig;

/**
 * Abstract class for a JavaFX window that uses a GameObjectRenderer to render game objects. This
 * class is a template for creating different types of JavaFX windows
 */

public abstract class ViewScene {

  private final Stage myStage;

  /**
   * Initialize the viewScene with the given mainStage.
   * @param mainStage the given stage of this window scene.
   */
  protected ViewScene(Stage mainStage) {
    mainStage.setOnCloseRequest(event -> Platform.exit());
    this.myStage = mainStage;
    this.myStage.setWidth(GameConfig.getNumber("windowWidth"));
    this.myStage.setHeight(GameConfig.getNumber("windowHeight"));
  }

  /**
   * Return this window's Stage object
   *
   * @return The JavaFX Stage object this window holds.
   */
  protected Stage getStage() {
    return myStage;
  }

  /**
   * Run the necessary actions when entering a scene.
   */
  public void onActivate() {
    // Note: Implement if necessary
  }

  /**
   * Run all necessary actions when exiting a scene.
   */
  public void onDeactivate() {
    // Note: Implement if necessary
  }

}
