package oogasalad.view.config;

import java.io.IOException;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import util.DukeApplicationTest;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class StyleConfigTest extends DukeApplicationTest {

  private static final String DEFAULT_STYLE = "light";
  private static final String STYLE_FILE_TYPE = ".css";
  private static final String STYLE_FILE_PREFIX = "/oogasalad/stylesheets/";
  private static String stylesheet = STYLE_FILE_PREFIX + DEFAULT_STYLE + STYLE_FILE_TYPE;
  private Scene scene;

  public void start(Stage stage) throws IOException {
    StackPane root = new StackPane();
    root.getChildren().add(new Label("Test Scene")); // Add a label to the scene
    scene = new Scene(root, 800, 600); // Set the size of the scene
    stage.setScene(scene);
    stage.show();
  }

  @Test
  void setTheme_positiveTest() {
    // Given
    String theme = "light";
    String expectedStylesheet = STYLE_FILE_PREFIX + theme.toLowerCase() + STYLE_FILE_TYPE;

    StyleConfig.setTheme(scene, theme);

    assertEquals(1, scene.getStylesheets().size(), "Should have one stylesheet");
    assertTrue(scene.getStylesheets().get(0).contains(expectedStylesheet),
        "Should be the default stylesheet");
  }

  @Test
  void setTheme_negativeTest() {
    String theme = "nonexistent";
    String defaultStylesheet = STYLE_FILE_PREFIX + DEFAULT_STYLE.toLowerCase() + STYLE_FILE_TYPE;

    StyleConfig.setTheme(scene, theme);

    assertEquals(1, scene.getStylesheets().size(), "Should have one stylesheet");
    assertTrue(scene.getStylesheets().getFirst().contains(defaultStylesheet),
        "Should be the default stylesheet");
  }

}
