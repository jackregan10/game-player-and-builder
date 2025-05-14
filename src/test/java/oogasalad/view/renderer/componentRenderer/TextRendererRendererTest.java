package oogasalad.view.renderer.componentRenderer;

import static org.junit.jupiter.api.Assertions.*;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import oogasalad.model.engine.component.TextRenderer;
import oogasalad.model.engine.component.Transform;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.util.WaitForAsyncUtils;

class TextRendererRendererTest extends AbstractComponentRendererTest {

  private TextRenderer textRenderer;

  @Override
  public void start(Stage stage) {
    // Required by TestFX to start JavaFX application thread
  }

  @BeforeEach
  void setup() {
    textRenderer = parent.addComponent(TextRenderer.class);
    textRenderer.setText("Hello World!");
    textRenderer.setStyleClass("testText");
    textRenderer.setFontSize(24);
    textRenderer.setCentered(false);
  }

  @Test
  void renderPane_RealTextRendererComponent_DoesNotCrash() {
    assertDoesNotThrow(() -> ComponentRenderer.render(textRenderer, pane, 0, 0));
  }

  @Test
  void renderCanvas_RealTextRendererComponent_DoesNotCrash() {
    Platform.runLater(() -> assertDoesNotThrow(
        () -> ComponentRenderer.render(textRenderer, graphicsContext, 0, 0)));
    WaitForAsyncUtils.waitForFxEvents();
  }

  @Test
  void renderPane_CenteredText_DoesNotCrash() {
    textRenderer.setCentered(true);
    assertDoesNotThrow(() -> ComponentRenderer.render(textRenderer, pane, 0, 0));
  }

  @Test
  void renderCanvas_CenteredText_DoesNotCrash() {
    textRenderer.setCentered(true);
    Platform.runLater(() -> assertDoesNotThrow(
        () -> ComponentRenderer.render(textRenderer, graphicsContext, 0, 0)));
    WaitForAsyncUtils.waitForFxEvents();
  }

  @Test
  void renderText_CenteredText_CorrectPosition() {
    textRenderer.setCentered(true);
    Transform transform = textRenderer.getComponent(Transform.class);

    pane.getChildren().clear();
    ComponentRenderer.render(textRenderer, pane, 0, 0);
    StackPane wrapper = (StackPane) pane.getChildren().getFirst();

    Platform.runLater(() -> {
      Stage stage = new Stage();
      stage.setScene(new Scene(pane));
      stage.show();

      double centerX = wrapper.getLayoutX() + wrapper.getBoundsInLocal().getWidth() / 2;
      double centerY = wrapper.getLayoutY() + wrapper.getBoundsInLocal().getHeight() / 2;
      double expectedCenterX = transform.getX() + transform.getScaleX() / 2;
      double expectedCenterY = transform.getY() + transform.getScaleY() / 2;

      assertEquals(expectedCenterX, centerX);
      assertEquals(expectedCenterY, centerY);
    });
  }
}
